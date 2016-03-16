package com.tatiana.inventory.test.controller;

import com.tatiana.inventory.billing.BillingService;
import com.tatiana.inventory.entity.Item;
import com.tatiana.inventory.entity.Purchase;
import com.tatiana.inventory.entry.PurchaseIdentifier;
import com.tatiana.inventory.repository.ItemRepository;
import com.tatiana.inventory.repository.PurchaseRepository;
import com.tatiana.inventory.test.TestUtil;
import com.tatiana.inventory.test.config.MockApplicationConfiguration;
import org.hibernate.ObjectNotFoundException;
import org.jboss.logging.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MockApplicationConfiguration.class)
@WebAppConfiguration
public class PurchaseControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PurchaseRepository purchaseRepositoryMock;
    @Autowired
    private ItemRepository itemRepositoryMock;
    @Autowired
    private BillingService billingServiceMock;

    @Rule
    public ExpectedException exception = ExpectedException.none();


    @Before
    public void setUp() {
        Mockito.reset(purchaseRepositoryMock);
        Mockito.reset(itemRepositoryMock);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testIsClientHasPurchase_ShouldReturnFalse() throws Exception{
        Integer itemId = 5;
        String clientEmail = "user1@gmail.com";
        List<Purchase> purchases = new ArrayList<>();

        when(purchaseRepositoryMock.findByItemAndClientAndState(itemId, clientEmail, Purchase.ItemState.ACTIVE)).thenReturn(purchases);

        mockMvc.perform(get("/purchases/info")
                .param("itemId", itemId.toString())
                .param("email", clientEmail)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string("false"));

        verify(purchaseRepositoryMock, times(1)).findByItemAndClientAndState(itemId, clientEmail, Purchase.ItemState.ACTIVE);
        verifyNoMoreInteractions(purchaseRepositoryMock);
    }

    @Test
    public void testIsClientHasPurchase_ShouldReturnTrue() throws Exception{
        Integer itemId = 1;
        String clientEmail = "user1@gmail.com";

        Item item = new Item();
        item.setId(itemId);
        Purchase purchase = new Purchase();
        purchase.setClient(clientEmail);
        purchase.setItem(item);
        purchase.setState(Purchase.ItemState.ACTIVE);

        List<Purchase> purchases = new ArrayList<>();
        purchases.add(purchase);

        when(purchaseRepositoryMock.findByItemAndClientAndState(itemId, clientEmail, Purchase.ItemState.ACTIVE)).thenReturn(purchases);

        mockMvc.perform(get("/purchases/info")
                        .param("itemId", itemId.toString())
                        .param("email", clientEmail)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(content().string("true"));

        verify(purchaseRepositoryMock, times(1)).findByItemAndClientAndState(itemId, clientEmail, Purchase.ItemState.ACTIVE);
        verifyNoMoreInteractions(purchaseRepositoryMock);
    }

    @Test
    //todo: fails
    public void testBuyItem_ShouldReturnStatusNotFound() throws Exception{
        PurchaseIdentifier identifier = new PurchaseIdentifier(5, "user1@gmail.com");
        exception.expect(ObjectNotFoundException.class);

        mockMvc.perform(post("/purchases")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(identifier))
        );
    }
}
