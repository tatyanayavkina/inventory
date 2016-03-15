package com.tatiana.inventory.controller;

import com.tatiana.inventory.Application;
import com.tatiana.inventory.TestUtil;
import com.tatiana.inventory.billing.BillingService;
import com.tatiana.inventory.entity.Service;
import com.tatiana.inventory.entity.Subscription;
import com.tatiana.inventory.entry.PurchaseIdentifier;
import com.tatiana.inventory.repository.ServiceRepository;
import com.tatiana.inventory.repository.SubscriptionRepository;
import com.tatiana.inventory.service.SubscriptionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class SubscriptionControllerTest {
    private MockMvc mockMvc;
    private ServiceRepository serviceRepositoryMock;
    private SubscriptionRepository subscriptionRepositoryMock;
    private SubscriptionService subscriptionServiceMock;
    private BillingService billingServiceMock;

    @Before
    public void setUp() {
        serviceRepositoryMock = mock(ServiceRepository.class);
        subscriptionRepositoryMock = mock(SubscriptionRepository.class);
        subscriptionServiceMock = mock(SubscriptionService.class);
        billingServiceMock = mock(BillingService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new SubscriptionController(serviceRepositoryMock, subscriptionRepositoryMock, subscriptionServiceMock, billingServiceMock)).build();
    }

    @Test
    public void testIsClientHasSubscription_ShouldReturnFalse() throws Exception{
        PurchaseIdentifier identifier = new PurchaseIdentifier(5, "user1@gmail.com");

        mockMvc.perform(post("/subscriptions/info")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(identifier))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string("false"));
    }

    @Test
    public void testIsClientHasSubscription_ShouldReturnTrue() throws Exception{
        Integer serviceId = 5;
        String clientEmail = "user1@gmail.com";
        PurchaseIdentifier identifier = new PurchaseIdentifier(serviceId, clientEmail);

        Service service = new Service();
        service.setId(serviceId);
        Subscription subscription = new Subscription();
        subscription.setService(service);
        subscription.setClient(clientEmail);
        subscription.setState(Subscription.ServiceState.ACTIVE);

        List<Subscription> subscriptions = new ArrayList<>();
        subscriptions.add(subscription);

        when(subscriptionRepositoryMock.findByServiceAndClientAndState(serviceId,clientEmail, Subscription.ServiceState.ACTIVE)).thenReturn(subscriptions);

        mockMvc.perform(post("/subscriptions/info")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(identifier))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string("true"));
    }
}
