package com.tatiana.inventory.controller;


import com.tatiana.inventory.Application;
import com.tatiana.inventory.TestUtil;
import com.tatiana.inventory.billing.BillingService;
import com.tatiana.inventory.entity.Item;
import com.tatiana.inventory.entity.Purchase;
import com.tatiana.inventory.entry.PurchaseIdentifier;
import com.tatiana.inventory.repository.ItemRepository;
import com.tatiana.inventory.repository.PurchaseRepository;
import org.jboss.logging.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class PurchaseControllerTest {
    private MockMvc mockMvc;
    private PurchaseRepository purchaseRepositoryMock;
    private ItemRepository itemRepositoryMock;
    private BillingService billingServiceMock;

//    @Autowired
//    private WebApplicationContext webApplicationContext;

//    private final Logger logger = Logger.getLogger(PurchaseControllerTest.class);

    @Before
    public void setUp() {
        purchaseRepositoryMock = mock(PurchaseRepository.class);
        itemRepositoryMock = mock(ItemRepository.class);
        billingServiceMock = mock(BillingService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new PurchaseController(itemRepositoryMock, purchaseRepositoryMock, billingServiceMock)).build();
        //We have to reset our mock between tests because the mock objects
        //are managed by the Spring container. If we would not reset them,
        //stubbing and verified behavior would "leak" from one test to another.
//        Mockito.reset(purchaseRepositoryMock);

//        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testIsClientHasPurchase_ShouldReturnFalse() throws Exception{
        PurchaseIdentifier identifier = new PurchaseIdentifier(5, "user1@gmail.com");

        mockMvc.perform(post("/purchases/info")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(identifier))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string("false"));
    }

    @Test
    public void testIsClientHasPurchase_ShouldReturnTrue() throws Exception{
        String clientEmail = "user1@gmail.com";
        Integer itemId = 1;
        PurchaseIdentifier identifier = new PurchaseIdentifier(itemId, clientEmail);

        Item item = new Item();
        item.setId(itemId);
        Purchase found = new Purchase();
        found.setClient(clientEmail);
        found.setItem(item);
        found.setState(Purchase.ItemState.ACTIVE);

        when(purchaseRepositoryMock.findByItemAndClientAndState(itemId, clientEmail, Purchase.ItemState.ACTIVE)).thenReturn(found);

        mockMvc.perform(post("/purchases/info")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(identifier))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string("true"));

        verify(purchaseRepositoryMock, times(1)).findByItemAndClientAndState(itemId, clientEmail, Purchase.ItemState.ACTIVE);
        verifyNoMoreInteractions(purchaseRepositoryMock);
    }
}
