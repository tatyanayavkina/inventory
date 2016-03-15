package com.tatiana.inventory.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="purchases")
public class Purchase extends BasicEntity  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "purchases_seq_gen")
    @SequenceGenerator(name = "purchases_seq_gen", sequenceName = "purchases_id_seq")
    @Column(name="id", nullable=false, unique=true, length=11)
    private Integer id;

    @Column(name="client")
    private String client;

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

    public Purchase(){

    }

    public Purchase(Item item, String client){
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

    public String getClient(){
        return client;
    }

    public void setClient(String client){
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
