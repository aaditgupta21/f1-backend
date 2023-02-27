package com.nighthawk.spring_portfolio.mvc.race;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

import org.springframework.format.annotation.DateTimeFormat;

import com.mongodb.lang.NonNull;
import com.nighthawk.spring_portfolio.mvc.betting.Bet;

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
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    private int round;

    @NotEmpty
    private String location;

    @NotEmpty
    private String season;

    @Column(unique = true)
    private String year;
    private String comment;

    // TODO: fix this since idk how we want to handle results of a race
    // api should copy bets ig
    private String raceResultWinner;

    // @JoinColumn(name = "race_id")
    // @OneToMany(cascade = CascadeType.ALL)
    // private List<Bet> bets = new ArrayList<>();

    public Race(String name, String circuit, Date date, int round, String location, String season) {
        this.name = name;
        this.circuit = circuit;
        this.date = date;
        this.round = round;
        this.location = location;
        this.season = season;
        this.raceResultWinner = null;
    }

    // TODO: add season
    public String toString() {
        return ("{ \"raceName\": " + this.name + ", " + "\"date\": " + this.date + ", " + "\"round\": "
                + this.round + ", " + "\"location\": " + this.location + " }");
    }
}