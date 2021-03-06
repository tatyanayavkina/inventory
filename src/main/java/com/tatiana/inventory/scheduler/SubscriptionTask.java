package com.tatiana.inventory.scheduler;

import com.tatiana.inventory.billing.BillingService;
import com.tatiana.inventory.entity.Subscription;
import com.tatiana.inventory.repository.SubscriptionRepository;
import com.tatiana.inventory.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class SubscriptionTask {
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionService subscriptionService;
    private final BillingService billingService;

    @Autowired
    public SubscriptionTask(SubscriptionRepository subscriptionRepository, SubscriptionService subscriptionService, BillingService billingService) {
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionService = subscriptionService;
        this.billingService = billingService;
    }

    @Scheduled(cron = "${scheduling.setExpired}")
    public void setSubscriptionExpired() {
        Date date = new Date();
        List<Subscription> subscriptions = subscriptionRepository.findByActiveStateAndEndDateLessThan(date);
        subscriptions.stream()
                .forEach((s) -> {
                    s.setState(Subscription.ServiceState.EXPIRED);
                    subscriptionRepository.save(s);
                });
    }

    @Scheduled(cron = "${scheduling.renewal}")
    public void renewalSubscription() {
        Date first = getNextDayMidnight(new Date());
        Date second = addDay(first);
        List<Subscription> subscriptions = subscriptionRepository.findByStateAndIsAutoAndEndDateBetween(first, second);
        subscriptions.stream()
                .forEach((s) -> {
                    Subscription renewal = subscriptionService.createSubscription(s);
                    subscriptionRepository.save(renewal);
                    billingService.pay(renewal).thenAccept((success) -> {
                        if (success) {
                            renewal.setState(Subscription.ServiceState.ACTIVE);
                        } else {
                            renewal.setState(Subscription.ServiceState.NOFUNDS);
                        }
                        subscriptionRepository.save(renewal);
                    });
                });
    }

    private Date getNextDayMidnight(Date current) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        calendar.add(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    private Date addDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);

        return calendar.getTime();
    }
}
