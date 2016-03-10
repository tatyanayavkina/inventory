package com.tatiana.inventory.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="purchase")
public class Purchase extends BasicEntity  implements Serializable {
    @Id
    @GeneratedValue
    @GenericGenerator(name = "generator", strategy = "increment")
    @Column(name="id", nullable=false, unique=true, length=11)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="client_id")
    private User client;

    @ManyToOne
    @JoinColumn(name="item_id")
    private Item item;

    @Column(name="state")
    @Enumerated(EnumType.STRING)
    private ItemState state;

    public enum ItemState{
        CREATED("created"), ACTIVE("active"), NOFUNDS("nofunds"),
        CANCELLED("cancelled"), RETURNED("returned");

        ItemState (String state){
            this.state = state;
        }

        private String state;

        public String getState(){
            return state;
        }
    }

    public Purchase(){}

    public Purchase(Item item, User client){
        this.item = item;
        this.client = client;
        this.state = ItemState.CREATED;
    }

    public Integer getId(){
        return id;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public User getClient(){
        return client;
    }

    public void setClient(User client){
        this.client = client;
    }

    public Item getItem(){
        return item;
    }

    public void setItem(Item item){
        this.item = item;
    }

    public ItemState getState(){
        return state;
    }

    public void  setState(ItemState state){
        this.state = state;
    }
}
