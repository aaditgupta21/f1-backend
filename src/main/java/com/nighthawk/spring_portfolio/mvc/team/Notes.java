package com.nighthawk.spring_portfolio.mvc.team;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class Notes {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @DateTimeFormat(pattern = "MM-dd-yyyy")
    private Date date;

    @Positive
    private String note;

    @Positive
    private double time;

    public Notes(Date date, String note, double time) {
        this.date = date;
        this.note = note;
        this.time = time;
    }
}
