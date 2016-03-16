package com.tatiana.inventory.test.config;

import com.tatiana.inventory.billing.BillingService;
import com.tatiana.inventory.repository.ItemRepository;
import com.tatiana.inventory.repository.PurchaseRepository;
import com.tatiana.inventory.repository.ServiceRepository;
import com.tatiana.inventory.repository.SubscriptionRepository;
import com.tatiana.inventory.service.SubscriptionService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan(value = "com.tatiana.inventory", excludeFilters = {
        @ComponentScan.Filter(pattern = "com.tatiana.inventory.billing.impl.*", type= FilterType.ASPECTJ),
        @ComponentScan.Filter(pattern = "com.tatiana.inventory.repository.*", type= FilterType.ASPECTJ),
        @ComponentScan.Filter(pattern = "com.tatiana.inventory.service.impl.*", type= FilterType.ASPECTJ),
        @ComponentScan.Filter(pattern = "com.tatiana.inventory.config.*", type= FilterType.ASPECTJ),
        @ComponentScan.Filter(value = com.tatiana.inventory.Application.class, type= FilterType.ASSIGNABLE_TYPE)
})
@EnableWebMvc
@EnableAsync
public class MockApplicationConfiguration {

    @Bean
    public SubscriptionRepository subscriptionRepository(){
        return Mockito.mock(SubscriptionRepository.class);
    }

    @Bean
    public PurchaseRepository purchaseRepository(){
        return Mockito.mock(PurchaseRepository.class);
    }

    @Bean
    public ItemRepository itemRepository(){
        return Mockito.mock(ItemRepository.class);
    }

    @Bean
    public ServiceRepository serviceRepository(){
        return Mockito.mock(ServiceRepository.class);
    }

    @Bean
    public SubscriptionService subscriptionService(){
        return Mockito.mock(SubscriptionService.class);
    }

    @Bean
    public BillingService billingService(){
        return Mockito.mock(BillingService.class);
    }
}
