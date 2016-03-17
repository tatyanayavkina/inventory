package com.tatiana.inventory.test.controller;

import com.tatiana.inventory.billing.BillingService;
import com.tatiana.inventory.entity.Service;
import com.tatiana.inventory.entity.Subscription;
import com.tatiana.inventory.entry.PurchaseIdentifier;
import com.tatiana.inventory.repository.ServiceRepository;
import com.tatiana.inventory.repository.SubscriptionRepository;
import com.tatiana.inventory.service.SubscriptionService;
import com.tatiana.inventory.test.utils.TestUtil;
import com.tatiana.inventory.test.config.MockApplicationConfiguration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MockApplicationConfiguration.class)
@WebAppConfiguration
public class SubscriptionControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ServiceRepository serviceRepositoryMock;
    @Autowired
    private SubscriptionRepository subscriptionRepositoryMock;
    @Autowired
    private SubscriptionService subscriptionServiceMock;
    @Autowired
    private BillingService billingServiceMock;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() {
        Mockito.reset(serviceRepositoryMock);
        Mockito.reset(subscriptionRepositoryMock);
        Mockito.reset(subscriptionServiceMock);
        Mockito.reset(billingServiceMock);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testIsClientHasSubscription_ShouldReturnFalse() throws Exception{
        Integer serviceId = 5;
        String clientEmail = "user1@gmail.com";
        List<Subscription> subscriptions = new ArrayList<>();

        when(subscriptionRepositoryMock.findByServiceAndClientAndState(serviceId, clientEmail, Subscription.ServiceState.ACTIVE)).thenReturn(subscriptions);

        mockMvc.perform(get("/subscriptions/info")
                    .param("serviceId", serviceId.toString())
                    .param("email", clientEmail)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string("false"));

        verify(subscriptionRepositoryMock, times(1)).findByServiceAndClientAndState(serviceId, clientEmail, Subscription.ServiceState.ACTIVE);
        verifyNoMoreInteractions(subscriptionRepositoryMock);
    }

    @Test
    public void testIsClientHasSubscription_ShouldReturnTrue() throws Exception{
        Integer serviceId = 5;
        String clientEmail = "user1@gmail.com";

        Service service = new Service();
        service.setId(serviceId);
        Subscription subscription = new Subscription();
        subscription.setService(service);
        subscription.setClient(clientEmail);
        subscription.setState(Subscription.ServiceState.ACTIVE);

        List<Subscription> subscriptions = new ArrayList<>();
        subscriptions.add(subscription);

        when(subscriptionRepositoryMock.findByServiceAndClientAndState(serviceId, clientEmail, Subscription.ServiceState.ACTIVE)).thenReturn(subscriptions);

        mockMvc.perform(get("/subscriptions/info")
                        .param("serviceId", serviceId.toString())
                        .param("email", clientEmail)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string("true"));

        verify(subscriptionRepositoryMock, times(1)).findByServiceAndClientAndState(serviceId, clientEmail, Subscription.ServiceState.ACTIVE);
        verifyNoMoreInteractions(subscriptionRepositoryMock);
    }

    @Test
    public void testBuyService_ShouldReturnInternalServerError() throws Exception{
        Integer serviceId = 3;
        String clientEmail = "user1@gmail.com";
        PurchaseIdentifier identifier = new PurchaseIdentifier(serviceId, clientEmail);

        when(serviceRepositoryMock.findOne(serviceId)).thenReturn(null);
        exception.expect(NestedServletException.class);

        MvcResult result = mockMvc.perform(post("/subscriptions")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(identifier))
        )
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8));

        verify(serviceRepositoryMock, times(1)).findOne(serviceId);
        verifyNoMoreInteractions(serviceRepositoryMock);
        verifyZeroInteractions(subscriptionRepositoryMock);
        verifyZeroInteractions(subscriptionServiceMock);
        verifyZeroInteractions(billingServiceMock);
    }

    @Test
    public void testBuyItem_ShouldReturnSubscriptionWithStateNOFUNDS() throws Exception{
        Integer serviceId = 1;
        String clientEmail = "user4@gmail.com";
        Integer subscriptionId = 2;
        PurchaseIdentifier identifier = new PurchaseIdentifier(serviceId, clientEmail);

        Service service = new Service();
        service.setId(serviceId);
        Subscription createdSubscription = new Subscription(service, clientEmail);
        createdSubscription.setId(subscriptionId);

        Subscription nofundsSubscription = new Subscription(service, clientEmail);
        nofundsSubscription.setId(subscriptionId);
        nofundsSubscription.setState(Subscription.ServiceState.NOFUNDS);

        CompletableFuture<Boolean> paymentResult = CompletableFuture.completedFuture(false);

        when(serviceRepositoryMock.findOne(serviceId)).thenReturn(service);
        when(subscriptionServiceMock.createSubscription(service, clientEmail)).thenReturn(createdSubscription);
        when(billingServiceMock.pay(createdSubscription)).thenReturn(paymentResult);

        MvcResult result = mockMvc.perform(post("/subscriptions")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(identifier))
        )
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(subscriptionId)))
                .andExpect(jsonPath("$.state", is("NOFUNDS")))
                .andExpect(jsonPath("$.client", is(clientEmail)))
                .andExpect(jsonPath("$.service.id", is(serviceId)));

        verify(serviceRepositoryMock, times(1)).findOne(serviceId);
        verifyNoMoreInteractions(serviceRepositoryMock);
        ArgumentCaptor<Subscription> subscriptionCaptor = ArgumentCaptor.forClass(Subscription.class);
        verify(subscriptionRepositoryMock, times(2)).save(subscriptionCaptor.capture());
        verifyNoMoreInteractions(subscriptionRepositoryMock);
        verify(subscriptionServiceMock, times(1)).createSubscription(service, clientEmail);
        verifyNoMoreInteractions(subscriptionServiceMock);
        verify(billingServiceMock, times(1)).pay(createdSubscription);
        verifyNoMoreInteractions(billingServiceMock);
    }

    @Test
    public void testBuyItem_ShouldReturnSubscriptionWithStateActive() throws Exception{
        Integer serviceId = 1;
        String clientEmail = "user4@gmail.com";
        Integer subscriptionId = 2;
        PurchaseIdentifier identifier = new PurchaseIdentifier(serviceId, clientEmail);

        Service service = new Service();
        service.setId(serviceId);
        Subscription createdSubscription = new Subscription(service, clientEmail);
        createdSubscription.setId(subscriptionId);

        Subscription activeSubscription = new Subscription(service, clientEmail);
        activeSubscription.setId(subscriptionId);
        activeSubscription.setState(Subscription.ServiceState.ACTIVE);
        CompletableFuture<Boolean> paymentResult = CompletableFuture.completedFuture(true);

        when(serviceRepositoryMock.findOne(serviceId)).thenReturn(service);
        when(subscriptionServiceMock.createSubscription(service, clientEmail)).thenReturn(createdSubscription);
        when(billingServiceMock.pay(createdSubscription)).thenReturn(paymentResult);

        MvcResult result = mockMvc.perform(post("/subscriptions")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(identifier))
        )
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(subscriptionId)))
                .andExpect(jsonPath("$.state", is("ACTIVE")))
                .andExpect(jsonPath("$.client", is(clientEmail)))
                .andExpect(jsonPath("$.service.id", is(serviceId)));

        verify(serviceRepositoryMock, times(1)).findOne(serviceId);
        verifyNoMoreInteractions(serviceRepositoryMock);
        ArgumentCaptor<Subscription> subscriptionCaptor = ArgumentCaptor.forClass(Subscription.class);
        verify(subscriptionRepositoryMock, times(2)).save(subscriptionCaptor.capture());
        verifyNoMoreInteractions(subscriptionRepositoryMock);
        verify(subscriptionServiceMock, times(1)).createSubscription(service, clientEmail);
        verifyNoMoreInteractions(subscriptionServiceMock);
        verify(billingServiceMock, times(1)).pay(createdSubscription);
        verifyNoMoreInteractions(billingServiceMock);
    }
}
