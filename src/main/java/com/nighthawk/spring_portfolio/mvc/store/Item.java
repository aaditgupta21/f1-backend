package com.nighthawk.spring_portfolio.mvc.store;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty
    private String partType;

    @NotEmpty
    private String description;

    @Positive
    private double initialCost;

    @Positive
    private double currentCost;

    @Positive
    private double weight;

    @DateTimeFormat(pattern = "MM-dd-yyyy")
    private Date endDate;

    @NotEmpty
    private String imageUrl;

    public Item(String partType, String description, double weight, Date endDate, double initialCost,
            double currentCost, String imageUrl) {
        this.partType = partType;
        this.description = description;
        this.currentCost = currentCost;
        this.initialCost = initialCost;
        this.weight = weight;
        this.endDate = endDate;
        this.imageUrl = imageUrl;
    }

    public static void main(String[] args) {
        Item engine = new Item();
        Date endDate = new GregorianCalendar(2006, 4, 2).getTime();
        engine.setPartType("Engine");
        engine.setDescription("John");
        engine.setCurrentCost(999);
    }
}