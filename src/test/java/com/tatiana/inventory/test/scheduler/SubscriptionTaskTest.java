package com.tatiana.inventory.test.scheduler;

import com.nitorcreations.junit.runners.NestedRunner;
import com.tatiana.inventory.billing.BillingService;
import com.tatiana.inventory.entity.Service;
import com.tatiana.inventory.entity.Subscription;
import com.tatiana.inventory.repository.SubscriptionRepository;
import com.tatiana.inventory.scheduler.SubscriptionTask;
import com.tatiana.inventory.service.SubscriptionService;
import com.tatiana.inventory.test.utils.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(NestedRunner.class)
public class SubscriptionTaskTest {
    private SubscriptionRepository subscriptionRepositoryMock;
    private SubscriptionService subscriptionServiceMock;
    private BillingService billingServiceMock;
    private SubscriptionTask subscriptionTask;

    @Before
    public void setUp() {
        subscriptionRepositoryMock = Mockito.mock(SubscriptionRepository.class);
        subscriptionServiceMock = Mockito.mock(SubscriptionService.class);
        billingServiceMock = Mockito.mock(BillingService.class);
        subscriptionTask = new SubscriptionTask(subscriptionRepositoryMock, subscriptionServiceMock, billingServiceMock);
    }

    @Test
    public void testSetSubscriptionExpired_ShouldSetSubscriptionsStateExpired() {
        Service service = new Service();
        service.setLength(Service.Length.MONTH);

        Subscription subscription1 = new Subscription();
        subscription1.setService(service);
        subscription1.setEndDate(TestUtil.getDateFromString("2016-04-14 15:13:28"));
        subscription1.setState(Subscription.ServiceState.ACTIVE);

        Subscription subscription2 = new Subscription();
        subscription2.setService(service);
        subscription2.setEndDate(TestUtil.getDateFromString("2016-04-14 15:13:28"));
        subscription2.setState(Subscription.ServiceState.ACTIVE);

        List<Subscription> subscriptionList = new ArrayList<>();
        subscriptionList.add(subscription1);
        subscriptionList.add(subscription2);

        when(subscriptionRepositoryMock.findByActiveStateAndEndDateLessThan(any(Date.class))).thenReturn(subscriptionList);

        subscriptionTask.setSubscriptionExpired();

        assertEquals(subscription1.getState(), Subscription.ServiceState.EXPIRED);
        assertEquals(subscription2.getState(), Subscription.ServiceState.EXPIRED);

        verify(subscriptionRepositoryMock, times(1)).findByActiveStateAndEndDateLessThan(any(Date.class));
        ArgumentCaptor<Subscription> subscriptionCaptor = ArgumentCaptor.forClass(Subscription.class);
        verify(subscriptionRepositoryMock, times(2)).save(subscriptionCaptor.capture());
        verifyNoMoreInteractions(subscriptionRepositoryMock);
    }

    @Test
    public void testRenewalSubscription_ShouldRenewalOneWithStateActiveAndOneWithStateNofunds() {
        Service service = new Service();
        service.setLength(Service.Length.MONTH);

        Subscription subscription1 = new Subscription();
        subscription1.setService(service);
        subscription1.setEndDate(TestUtil.getDateFromString("2016-04-14 15:13:28"));
        subscription1.setState(Subscription.ServiceState.ACTIVE);

        Subscription subscription2 = new Subscription();
        subscription2.setService(service);
        subscription2.setEndDate(TestUtil.getDateFromString("2016-04-14 15:13:28"));
        subscription2.setState(Subscription.ServiceState.ACTIVE);

        List<Subscription> subscriptionsBefore = new ArrayList<>();
        subscriptionsBefore.add(subscription1);
        subscriptionsBefore.add(subscription2);

        Subscription renewalCreatedSubscription1 = new Subscription();
        renewalCreatedSubscription1.setService(service);
        renewalCreatedSubscription1.setStartDate(TestUtil.getDateFromString("2016-05-14 15:13:28"));
        renewalCreatedSubscription1.setState(Subscription.ServiceState.CREATED);

        Subscription renewalCreatedSubscription2 = new Subscription();
        renewalCreatedSubscription2.setService(service);
        renewalCreatedSubscription2.setStartDate(TestUtil.getDateFromString("2016-05-14 15:13:28"));
        renewalCreatedSubscription2.setState(Subscription.ServiceState.CREATED);

        CompletableFuture<Boolean> paymentTrueResult = CompletableFuture.completedFuture(true);
        CompletableFuture<Boolean> paymentFalseResult = CompletableFuture.completedFuture(false);

        when(subscriptionRepositoryMock.findByStateAndIsAutoAndEndDateBetween(any(Date.class), any(Date.class))).thenReturn(subscriptionsBefore);
        when(subscriptionServiceMock.createSubscription(subscription1)).thenReturn(renewalCreatedSubscription1);
        when(subscriptionServiceMock.createSubscription(subscription2)).thenReturn(renewalCreatedSubscription2);
        when(billingServiceMock.pay(renewalCreatedSubscription1)).thenReturn(paymentTrueResult);
        when(billingServiceMock.pay(renewalCreatedSubscription2)).thenReturn(paymentFalseResult);

        subscriptionTask.renewalSubscription();

        assertEquals(renewalCreatedSubscription1.getState(), Subscription.ServiceState.ACTIVE);
        assertEquals(renewalCreatedSubscription2.getState(), Subscription.ServiceState.NOFUNDS);

        verify(subscriptionRepositoryMock, times(1)).findByStateAndIsAutoAndEndDateBetween(any(Date.class), any(Date.class));
        ArgumentCaptor<Subscription> subscriptionCaptor = ArgumentCaptor.forClass(Subscription.class);
        verify(subscriptionRepositoryMock, times(4)).save(subscriptionCaptor.capture());
        verifyNoMoreInteractions(subscriptionRepositoryMock);
    }
}
