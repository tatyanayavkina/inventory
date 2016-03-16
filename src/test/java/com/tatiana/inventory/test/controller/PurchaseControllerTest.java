package com.tatiana.inventory.test.controller;


import com.tatiana.inventory.Application;
import com.tatiana.inventory.billing.BillingService;
import com.tatiana.inventory.controller.PurchaseController;
import com.tatiana.inventory.entity.Item;
import com.tatiana.inventory.entity.Purchase;
import com.tatiana.inventory.entry.PurchaseIdentifier;
import com.tatiana.inventory.repository.ItemRepository;
import com.tatiana.inventory.repository.PurchaseRepository;
import com.tatiana.inventory.test.TestUtil;
import org.hibernate.ObjectNotFoundException;
import org.jboss.logging.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Rule
    public ExpectedException exception = ExpectedException.none();


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
