package com.nighthawk.spring_portfolio.mvc.betting;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.PositiveOrZero;

import org.springframework.format.annotation.DateTimeFormat;

import com.nighthawk.spring_portfolio.mvc.team.Team;
import com.nighthawk.spring_portfolio.mvc.user.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Bet {
    // automatic unique identifier for Person record
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DateTimeFormat(pattern = "MM-dd-yyyy")
    private Date date;

    @PositiveOrZero
    private double fCoinBet;

    private boolean betActive;

    @JoinColumn(name = "user_id")
    @ManyToOne(cascade = CascadeType.ALL)
    private User user;

    @JoinColumn(name = "team_id")
    @ManyToOne(cascade = CascadeType.ALL)
    private Team team;

    public Bet(double fCoinBet, Date date) {
        this.fCoinBet = fCoinBet;
        this.date = date;

        // TODO: gotta do check for current day and make false if date passed
        this.betActive = true;
    }

    public boolean getBetActive() {
        return this.betActive;
    }

    // public String toString() {
    // return ("{ \"raceName\": " + this.race.getName() + ", " + "\"team\": " +
    // this.team.getName() + ", "
    // + "\"fCoinBet\": " + this.fCoinBet + " }");
    // }
}