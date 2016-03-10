package com.tatiana.inventory.entity;


import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.money.MonetaryAmount;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@Entity
@Table(name="service")
public class Service extends BasicEntity  implements Serializable {
    @Id
    @GeneratedValue
    @GenericGenerator(name = "generator", strategy = "increment")
    @Column(name="id", nullable=false, unique=true, length=11)
    private Integer id;

    @Column(name="name", nullable=false)
    private String name;

    @Column(name="description")
    private String description;

    @Column(name="length")
    @Enumerated(EnumType.STRING)
    private Length length;

    @Column(name="price")
    @Type(type = "org.jadira.usertype.moneyandcurrency.moneta.PersistentMoneyAmount")
    private MonetaryAmount price;

    @Column(name="is_continuous")
    private Boolean isContinuous;

    public enum Length {
        DAY(Calendar.DATE), MONTH(Calendar.MONTH);

        Length(int type) {
            this.type = type;
        }

        private int type;

        public int getType() {
            return this.type;
        }

        public static Date getNextDate(Date startDate, Length length){
            Calendar c = Calendar.getInstance();
            c.setTime( startDate );
            c.add( length.getType(), 1);
            return c.getTime();
        }
    }

    public Service(){}

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

    public Length getLength(){
        return length;
    }

    public void setLength(Length length){
        this.length = length;
    }

    public MonetaryAmount getPrice(){
        return price;
    }

    public void setPrice(MonetaryAmount price){
        this.price = price;
    }

    public Boolean getIsContinuous(){
        return isContinuous;
    }

    public void setIsContinuous(Boolean isContinuous){
        this.isContinuous = isContinuous;
    }

}




