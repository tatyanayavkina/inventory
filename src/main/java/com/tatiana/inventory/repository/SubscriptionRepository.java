package com.tatiana.inventory.repository;

import com.tatiana.inventory.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {

    @Query("select s from Subscription s where s.service.id = :serviceId and s.client = :client " +
            "and s.state = :state order by s.id desc")
    List<Subscription> findByServiceAndClientAndState(@Param("serviceId") Integer serviceId, @Param("client") String client,
                                                      @Param("state") Subscription.ServiceState state);

    @Query("select s from Subscription s where s.state='ACTIVE' and s.endDate<:date")
    List<Subscription> findByActiveStateAndEndDateLessThan(@Param("date") Date date);

    @Query("select s from Subscription s inner join s.service ser where s.state='ACTIVE' and ser.isAuto=true " +
            "and s.endDate>=:first and s.endDate<:second")
    List<Subscription> findByStateAndIsAutoAndEndDateBetween(@Param("first") Date first, @Param("second") Date second);
}
