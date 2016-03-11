package com.tatiana.inventory.repository;

import com.tatiana.inventory.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

//@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription,Integer> {

    Subscription findByServiceAndClient(Integer serviceId, Integer clientId);

    List<Subscription> findByService(Integer serviceId);

    @Query("select p from Subscription p where p.service.id = :serviceId and p.client.id = :clientId " +
            "and p.state = :state")
    Subscription findByServiceAndClientAndState(@Param("serviceId") Integer serviceId, @Param("clientId") Integer clientId,
                                                @Param("state") Subscription.ServiceState state);
}
