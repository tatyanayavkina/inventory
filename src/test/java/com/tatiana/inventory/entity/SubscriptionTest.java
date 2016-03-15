package com.tatiana.inventory.entity;

import com.nitorcreations.junit.runners.NestedRunner;
import org.jboss.logging.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

@RunWith(NestedRunner.class)
public class SubscriptionTest {
    private final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final Logger logger = Logger.getLogger(SubscriptionTest.class);

    private Service service1;
    private Subscription lastActiveSubscription1;
    private Subscription lastExpiredSubscription1;
    private Subscription lastActiveSubscription2;
    private Subscription lastExpiredSubscription2;

    @Before
    public void setData(){
        setServices();
        setSubscriptions();
    }

    private void setServices(){
        service1 = new Service();
        service1.setName( "service1" );
        service1.setLength( Service.Length.DAY );
        service1.setIsContinuous( true);
    }

    private void setSubscriptions(){
        // ------
        lastActiveSubscription1 = new Subscription();
        lastActiveSubscription1.setService(service1);
        lastActiveSubscription1.setState(Subscription.ServiceState.ACTIVE);
        lastActiveSubscription1.setStartDate(getDateFromString("2016-03-14 15:13:28"));
        lastActiveSubscription1.setEndDate(getDateFromString("2016-03-15 15:13:28"));

        lastExpiredSubscription1 = new Subscription();
        lastExpiredSubscription1.setService( service1 );
        lastExpiredSubscription1.setState(Subscription.ServiceState.EXPIRED);
        lastExpiredSubscription1.setStartDate(getDateFromString("2016-03-13 15:13:28"));
        lastExpiredSubscription1.setEndDate(getDateFromString("2016-03-14 15:13:28"));
        // -----
        lastActiveSubscription2 = null;

        lastExpiredSubscription2 = new Subscription();
        lastExpiredSubscription2.setService( service1 );
        lastExpiredSubscription2.setState( Subscription.ServiceState.EXPIRED );
        lastExpiredSubscription2.setStartDate( getDateFromString( "2016-03-10 15:13:28" ) );
        lastExpiredSubscription2.setEndDate( getDateFromString( "2016-03-11 15:13:28" ) );
    }

    private Date getDateFromString(String dateString){
        Date date = null;
        try{
            date = formatter.parse(dateString);
        } catch (ParseException ex){
            logger.info("ParseException in dateString parsing");
        }
        return date;
    }

    @Test
    public void testCalculateStartAndEndDate_ShouldUseEndDateOfLastActive(){
        Subscription subscription1 = new Subscription( service1, "user1@mail.ru" );
        subscription1.calculateStartAndEndDate(lastActiveSubscription1, lastExpiredSubscription1);
        assertEquals(formatter.format(subscription1.getStartDate()), "2016-03-15 15:13:28");
        assertEquals(formatter.format(subscription1.getEndDate()), "2016-03-16 15:13:28");
    }

    @Test
    public void testCalculateStartAndEndDate_ShouldUseEndDateOfLastExpired(){
        Subscription subscription2 = new Subscription( service1, "user2@gmail.com" );
        subscription2.calculateStartAndEndDate(lastActiveSubscription2, lastExpiredSubscription2);
        assertEquals(formatter.format(subscription2.getStartDate()), "2016-03-11 15:13:28");
        assertEquals(formatter.format(subscription2.getEndDate()), "2016-03-12 15:13:28");
    }

}
