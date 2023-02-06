package com.nighthawk.spring_portfolio.mvc.race;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

import org.springframework.format.annotation.DateTimeFormat;

import com.mongodb.lang.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Race {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String circuit;

    @NonNull
    @DateTimeFormat(pattern = "MM-dd-yyyy")
    private Date date;

    @Positive
    private int round;

    @NotEmpty
    private String location;

    @NotEmpty
    private String season;

    public Race(String name, String circuit, Date date, int round, String location, String season) {
        this.name = name;
        this.circuit = circuit;
        this.date = date;
        this.round = round;
        this.location = location;
        this.season = season;
    }

    // TODO: add season
    public String toString() {
        return ("{ \"raceName\": " + this.name + ", " + "\"date\": " + this.date + ", " + "\"round\": "
                + this.round + ", " + "\"location\": " + this.location + " }");
    }
}