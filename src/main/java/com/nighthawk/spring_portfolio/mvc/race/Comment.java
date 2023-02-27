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
import com.nighthawk.spring_portfolio.mvc.user.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String comment;

    @JoinColumn(name = "user_id")
    @ManyToOne(cascade = CascadeType.MERGE)
    private User user;

    @JoinColumn(name = "race_id")
    @ManyToOne(cascade = CascadeType.MERGE)
    private Race race;

    public Comment(String comment, User user, Race race) {
        this.comment = comment;
        this.user = user;
        this.race = race;
    }

    public String toString() {
        return ("{ \"raceName\": " + this.race.getName() + ", " + "\"season\": " + this.race.getSeason() + ", " + "\"comment\": " + this.comment + "}");
    }

}