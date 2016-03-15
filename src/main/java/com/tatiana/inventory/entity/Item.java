package com.tatiana.inventory.entity;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.money.MonetaryAmount;
import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="items")
public class Item extends BasicEntity  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "items_seq_gen")
    @SequenceGenerator(name = "items_seq_gen", sequenceName = "items_id_seq")
    @Column(name="id", nullable=false, unique=true, length=11)
    private Integer id;

    @Column(name="name", nullable=false)
    private String name;

    @Column(name="description")
    private String description;

    @Columns(columns = {
            @Column(name="currency"),
            @Column(name="amount")
    })
    @Type(type = "org.jadira.usertype.moneyandcurrency.moneta.PersistentMoneyAmountAndCurrency")
    private MonetaryAmount price;

    public Item(){

    }

    public Integer getId(){
        return id;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public MonetaryAmount getPrice(){
        return price;
    }

    public void setPrice(MonetaryAmount price){
        this.price = price;
    }
}
