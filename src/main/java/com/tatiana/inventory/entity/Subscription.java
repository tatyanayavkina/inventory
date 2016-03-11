package com.tatiana.inventory.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="subscriptions")
public class Subscription extends BasicEntity  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subscriptions_seq_gen")
    @SequenceGenerator(name = "subscriptions_seq_gen", sequenceName = "subscription_id_seq")
    @Column(name="id", nullable=false, unique=true, length=11)
    private Integer id;

    @Column(name="client")
    private String client;

    @ManyToOne
    @JoinColumn(name="service_id")
    private Service service;

    @Column(name="start_date")
    private Date startDate;

    @Column(name="end_date")
    private Date endDate;

    @Column(name="state")
    @Enumerated(EnumType.STRING)
    private ServiceState state;

    @Column(name="is_auto")
    private Boolean isAuto;

    public enum ServiceState{
        CREATED("created"), ACTIVE("active"), EXPIRED("expired"),
        CANCELLED("cancelled"), RETURNED("returned"), NOFUNDS("nofunds");

        ServiceState (String state){
            this.state = state;
        }

        private String state;

        public String getState(){
            return state;
        }
    }


    public Subscription(Service service, String client){
        this.service = service;
        this.client = client;
        this.state = ServiceState.CREATED;
    }

    public Integer getId(){
        return id;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public Service getService(){
        return service;
    }

    public void setService(Service service){
        this.service = service;
    }

    public String getClient(){
        return client;
    }

    public void setClient(String client){
        this.client = client;
    }

    public Date getStartDate(){
        return  startDate;
    }

    public void setStartDate(Date startDate){
        this.startDate = startDate;
    }

    public Date getEndDate(){
        return  endDate;
    }

    public void setEndDate(Date endDate){
        this.endDate = endDate;
    }

    public Boolean getIsAuto(){
        return isAuto;
    }

    public void setIsAuto(Boolean isAuto){
        this.isAuto = isAuto;
    }

    public ServiceState getState(){
        return state;
    }

    public void  setState(ServiceState state){
        this.state = state;
    }

    public void setActive(){
        startDate = new Date();
        endDate = Service.Length.getNextDate( startDate, service.getLength() );
        state = ServiceState.ACTIVE;
    }
}
