package com.tatiana.inventory.repository;

import com.tatiana.inventory.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

//@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription,Integer> {

    List<Subscription> findByServiceAndClient(Integer serviceId, String client);

    List<Subscription> findByService(Integer serviceId);

    @Query("select s from Subscription s where s.service.id = :serviceId and s.client = :client " +
            "and s.state = :state order by s.id desc")
    List<Subscription> findByServiceAndClientAndState(@Param("serviceId") Integer serviceId, @Param("client") String client,
                                                @Param("state") Subscription.ServiceState state);
}
