package com.tatiana.inventory.service;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.tatiana.inventory.Application;
import com.tatiana.inventory.entity.Service;
import com.tatiana.inventory.entity.Subscription;
import com.tatiana.inventory.repository.ServiceRepository;
import org.jboss.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@SpringApplicationConfiguration(classes = Application.class)
@DbUnitConfiguration(databaseConnection="dataSource")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class})
@DatabaseSetup(connection="dataSource", value="classpath:entries/subscription-entries.xml")
public class ITSubscriptionServiceTest {
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private SubscriptionService subscriptionService;
    private final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final Logger logger = Logger.getLogger(ITSubscriptionServiceTest.class);

//    private Date getDateWithFormatter(String dateString){
//        Date formatted = null;
//        try{
//            formatted =  formatter.parse(dateString);
//        } catch(ParseException e){
//            logger.error("Can not parse dateString");
//        }
//        return formatted;
//    }

    @Test
    public void testCreateSubscription_ShouldRenewalSubscriptionAndSetStartDateAsEndDateOfExpired(){
        Service service = serviceRepository.findOne(2);
        String clientEmail = "user1@gmail.com";
        Subscription subscription = subscriptionService.createSubscription( service, clientEmail);
        assertEquals(formatter.format(subscription.getStartDate()), "2016-03-15 15:13:28");
    }
}
