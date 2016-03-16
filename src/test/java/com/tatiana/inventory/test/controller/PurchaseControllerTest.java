package com.tatiana.inventory.test.controller;

import com.tatiana.inventory.billing.BillingService;
import com.tatiana.inventory.entity.Item;
import com.tatiana.inventory.entity.Purchase;
import com.tatiana.inventory.entry.PurchaseIdentifier;
import com.tatiana.inventory.repository.ItemRepository;
import com.tatiana.inventory.repository.PurchaseRepository;
import com.tatiana.inventory.test.utils.TestUtil;
import com.tatiana.inventory.test.config.MockApplicationConfiguration;
import org.jboss.logging.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    private final Logger logger = Logger.getLogger(PurchaseControllerTest.class);

    @Before
    public void setUp() {
        Mockito.reset(purchaseRepositoryMock);
        Mockito.reset(itemRepositoryMock);
        Mockito.reset(billingServiceMock);
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
        Purchase purchase = new Purchase(item, clientEmail);
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

    public void testBuyItem_ShouldReturnInternalServerError() throws Exception{
        Integer itemId = 2;
        String clientEmail = "user5@gmail.com";
        PurchaseIdentifier identifier = new PurchaseIdentifier(itemId, clientEmail);

        when(itemRepositoryMock.findOne(itemId)).thenReturn(null);

        MvcResult result = mockMvc.perform(post("/purchases")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(identifier))
        )
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8));

        verify(itemRepositoryMock, times(1)).findOne(itemId);
        verifyNoMoreInteractions(itemRepositoryMock);
        verifyZeroInteractions(purchaseRepositoryMock);
        verifyZeroInteractions(billingServiceMock);
    }

    @Test
    public void testBuyItem_ShouldReturnExistedActivePurchase() throws Exception{
        Integer itemId = 1;
        String clientEmail = "user4@gmail.com";
        Integer purchaseId = 2;
        PurchaseIdentifier identifier = new PurchaseIdentifier(itemId, clientEmail);

        Item item = new Item();
        item.setId(itemId);
        Purchase purchase = new Purchase();
        purchase.setId(purchaseId);
        purchase.setItem(item);
        purchase.setClient(clientEmail);
        purchase.setState(Purchase.ItemState.ACTIVE);
        List<Purchase> purchases = new ArrayList<>();
        purchases.add(purchase);

        when(itemRepositoryMock.findOne(itemId)).thenReturn(item);
        when(purchaseRepositoryMock.findByItemAndClientAndState(itemId, clientEmail, Purchase.ItemState.ACTIVE)).thenReturn(purchases);

        MvcResult result = mockMvc.perform(post("/purchases")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(identifier))
        )
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(purchaseId)))
                .andExpect(jsonPath("$.state", is("ACTIVE")))
                .andExpect(jsonPath("$.client", is(clientEmail)))
                .andExpect(jsonPath("$.item.id", is(itemId)));

        verify(itemRepositoryMock, times(1)).findOne(itemId);
        verifyNoMoreInteractions(itemRepositoryMock);
        verify(purchaseRepositoryMock, times(1)).findByItemAndClientAndState(itemId, clientEmail, Purchase.ItemState.ACTIVE);
        verifyNoMoreInteractions(purchaseRepositoryMock);
        verifyZeroInteractions(billingServiceMock);
    }

    @Test
    //todo: error NullPointerException :(
    public void testBuyItem_ShouldReturnNewPurchaseWithStateNOFUNDS() throws Exception{
        Integer itemId = 1;
        String clientEmail = "user4@gmail.com";
        Integer purchaseId = 2;
        PurchaseIdentifier identifier = new PurchaseIdentifier(itemId, clientEmail);

        Item item = new Item();
        item.setId(itemId);
        Purchase newPurchase = new Purchase(item, clientEmail);

        Purchase createdPurchase = new Purchase(item, clientEmail);
        createdPurchase.setId(purchaseId);

        Purchase nofundsPurchase = new Purchase(item, clientEmail);
        createdPurchase.setId(purchaseId);
        createdPurchase.setState(Purchase.ItemState.NOFUNDS);

        List<Purchase> purchases = new ArrayList<>();

        when(itemRepositoryMock.findOne(itemId)).thenReturn(item);
        when(purchaseRepositoryMock.findByItemAndClientAndState(itemId, clientEmail, Purchase.ItemState.ACTIVE)).thenReturn(purchases);
        when(billingServiceMock.pay(createdPurchase)).thenReturn(CompletableFuture.completedFuture(false));
        when(purchaseRepositoryMock.save(newPurchase)).thenReturn(createdPurchase);
        when(purchaseRepositoryMock.save(nofundsPurchase)).thenReturn(nofundsPurchase);

        MvcResult result = mockMvc.perform(post("/purchases")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(identifier))
        )
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(purchaseId)))
                .andExpect(jsonPath("$.state", is("NOFUNDS")))
                .andExpect(jsonPath("$.client", is(clientEmail)))
                .andExpect(jsonPath("$.item.id", is(itemId)));

        verify(itemRepositoryMock, times(1)).findOne(itemId);
        verifyNoMoreInteractions(itemRepositoryMock);
        verify(purchaseRepositoryMock, times(1)).findByItemAndClientAndState(itemId, clientEmail, Purchase.ItemState.ACTIVE);
        verifyNoMoreInteractions(purchaseRepositoryMock);
        verify(billingServiceMock, times(1)).pay(newPurchase);
        verifyNoMoreInteractions(billingServiceMock);
    }

    @Test
    //todo: error NullPointerException :(
    public void testBuyItem_ShouldReturnNewPurchaseWithStateACTIVE() throws Exception{
        Integer itemId = 1;
        String clientEmail = "user4@gmail.com";
        Integer purchaseId = 2;
        PurchaseIdentifier identifier = new PurchaseIdentifier(itemId, clientEmail);

        Item item = new Item();
        item.setId(itemId);
        Purchase newPurchase = new Purchase(item, clientEmail);

        Purchase createdPurchase = new Purchase(item, clientEmail);
        createdPurchase.setId(purchaseId);

        Purchase nofundsPurchase = new Purchase(item, clientEmail);
        createdPurchase.setId(purchaseId);
        createdPurchase.setState(Purchase.ItemState.ACTIVE);

        List<Purchase> purchases = new ArrayList<>();

        when(itemRepositoryMock.findOne(itemId)).thenReturn(item);
        when(purchaseRepositoryMock.findByItemAndClientAndState(itemId, clientEmail, Purchase.ItemState.ACTIVE)).thenReturn(purchases);
        when(billingServiceMock.pay(createdPurchase)).thenReturn(CompletableFuture.completedFuture(true));
        when(purchaseRepositoryMock.save(newPurchase)).thenReturn(createdPurchase);
        when(purchaseRepositoryMock.save(nofundsPurchase)).thenReturn(nofundsPurchase);

        MvcResult result = mockMvc.perform(post("/purchases")
                        .contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(identifier))
        )
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(purchaseId)))
                .andExpect(jsonPath("$.state", is("ACTIVE")))
                .andExpect(jsonPath("$.client", is(clientEmail)))
                .andExpect(jsonPath("$.item.id", is(itemId)));

        verify(itemRepositoryMock, times(1)).findOne(itemId);
        verifyNoMoreInteractions(itemRepositoryMock);
        verify(purchaseRepositoryMock, times(1)).findByItemAndClientAndState(itemId, clientEmail, Purchase.ItemState.ACTIVE);
        verifyNoMoreInteractions(purchaseRepositoryMock);
        verify(billingServiceMock, times(1)).pay(newPurchase);
        verifyNoMoreInteractions(billingServiceMock);
    }
}
