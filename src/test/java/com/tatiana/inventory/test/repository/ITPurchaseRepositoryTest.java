package com.tatiana.inventory.test.repository;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.tatiana.inventory.config.JpaConfig;
import com.tatiana.inventory.entity.Purchase;
import com.tatiana.inventory.repository.PurchaseRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.CoreMatchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JpaConfig.class})
@TestPropertySource(locations="classpath:integration-test.properties")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class})
@DatabaseSetup("purchase-entries.xml")
public class ITPurchaseRepositoryTest {
    @Autowired
    private PurchaseRepository purchaseRepository;

    @Test
    public void findByItemAndClientAndState_ShouldFoundPurchase(){
        Integer itemId = 2;
        String client = "user1@gmail.com";
        Purchase.ItemState activeState = Purchase.ItemState.ACTIVE;
        Purchase purchaseWithActiveState = purchaseRepository.findByItemAndClientAndState( itemId, client, activeState );
        assertThat( purchaseWithActiveState, is( notNullValue() ) );
        assertThat( purchaseWithActiveState.getId(), is( equalTo( 1 ) ) );
    }

    @Test
    public void findByItemAndClientAndState_ShouldNOTFoundPurchase(){
        Integer itemId = 1;
        String client ="user1@gmail.com";
        Purchase.ItemState activeState = Purchase.ItemState.ACTIVE;
        Purchase purchaseWithActiveState = purchaseRepository.findByItemAndClientAndState( itemId, client, activeState );
        assertThat( purchaseWithActiveState, is( nullValue() ) );
    }
}
