package com.tatiana.inventory.repository;

import com.tatiana.inventory.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

//@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription,Integer> {

    List<Subscription> findByServiceAndClient(Integer serviceId, String client);

    List<Subscription> findByService(Integer serviceId);

    @Query("select s from Subscription s where s.service.id = :serviceId and s.client = :client " +
            "and s.state = :state order by s.id desc")
    List<Subscription> findByServiceAndClientAndState(@Param("serviceId") Integer serviceId, @Param("client") String client,
                                                @Param("state") Subscription.ServiceState state);

    @Query("select s from Subscription s where s.state=:state and s.endDate<:date")
    List<Subscription> findByStateAndEndDateLessThan(@Param("state") Subscription.ServiceState state, @Param("date")Date date);

    @Query("select s from Subscription s inner join s.service ser where s.state=:state and ser.isAuto=true " +
            "and s.endDate>=:first and s.endDate<:second")
    List<Subscription> findByStateAndIsAutoAndEndDateBetween(@Param("state") Subscription.ServiceState state,
                                                             @Param("first") Date first, @Param("second") Date second);
}
