package com.nighthawk.spring_portfolio.mvc.race;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Race {
    // automatic unique identifier for Person record
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String circuit;

    @DateTimeFormat(pattern = "MM-dd-yyyy")
    private Date date;

    @Positive
    private int round;

    @NotEmpty
    private String location;

    public Race(String name, String circuit, Date date, int round, String location) {
        this.name = name;
        this.circuit = circuit;
        this.date = date;
        this.round = round;
        this.location = location;
    }

    public String toString() {
        return ("{ \"raceName\": " + this.name + ", " + "\"date\": " + this.date + ", " + "\"round\": "
                + this.round + ", " + "\"location\": " + this.location + " }");
    }
}
