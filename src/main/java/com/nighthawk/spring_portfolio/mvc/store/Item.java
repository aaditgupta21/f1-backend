package com.nighthawk.spring_portfolio.mvc.store;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    private String partType;

    @NotEmpty
    private String description;

    @Positive
    private double cost;

    @Positive
    private double weight;

    public Item(String partType, String description, double cost, double weight) {
        this.partType = partType;
        this.description = description;
        this.cost = cost;
        this.weight = weight;
    }
}