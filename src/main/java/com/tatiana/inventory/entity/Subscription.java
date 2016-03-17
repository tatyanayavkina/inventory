package com.tatiana.inventory.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "subscriptions")
public class Subscription extends BasicEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subscriptions_seq_gen")
    @SequenceGenerator(name = "subscriptions_seq_gen", sequenceName = "subscription_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false, unique = true, length = 11)
    private Integer id;

    @Column(name = "client")
    private String client;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private ServiceState state;

    public Subscription() {
    }

    public Subscription(Service service, String client) {
        this.service = service;
        this.client = client;
        this.state = ServiceState.CREATED;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public ServiceState getState() {
        return state;
    }

    public void setState(ServiceState state) {
        this.state = state;
    }

    public void calculateEndDate() {
        endDate = Service.Length.getNextDate(startDate, service.getLength());
    }

    /**
     * Finds startDate for subscription according to conditions and then calculate endDate
     *
     * @param lastActiveSubscription  - Subscription
     * @param lastExpiredSubscription - Subscription
     *                                If client has an active subscription then then we get its endDate and use it as startDate for new subscription
     *                                If there are no active subscription for client then we set startDate as current date,
     *                                but we change it if service is continuous and client has an expired subscription.
     */
    public void calculateStartAndEndDate(Subscription lastActiveSubscription, Subscription lastExpiredSubscription) {
        if (lastActiveSubscription == null) {
            setStartDate(new Date());

            if (service.getIsContinuous()) {
                if (lastExpiredSubscription != null) {
                    setStartDate(lastExpiredSubscription.getEndDate());
                }
            }
        } else {
            setStartDate(lastActiveSubscription.getEndDate());
        }
        calculateEndDate();
    }

    public enum ServiceState {
        CREATED("created"), ACTIVE("active"), EXPIRED("expired"),
        CANCELLED("cancelled"), RETURNED("returned"), NOFUNDS("nofunds");

        private final String state;

        ServiceState(String state) {
            this.state = state;
        }

        public String getState() {
            return state;
        }
    }
}
