package com.nighthawk.spring_portfolio.mvc.drivelog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Positive;
import java.util.Date;

// import javax.management.relation.Role;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.format.annotation.DateTimeFormat;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DriveLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @DateTimeFormat(pattern = "MM-dd-yyyy")
    private Date date;

    @Positive
    private double miles;

    @Positive
    private double time;

    public DriveLog(Date date, double miles, double time) {
        this.date = date;
        this.miles = miles;
        this.time = time;
    }
}
