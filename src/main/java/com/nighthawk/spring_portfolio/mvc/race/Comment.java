package com.nighthawk.spring_portfolio.mvc.race;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
    private String season;

    @JoinColumn(name = "user_id")
    @ManyToOne(cascade = CascadeType.MERGE)
    private User user;

    public Comment(String comment, String season, User user) {
        this.comment = comment;
        this.season = season;
        this.user = user;
    }

    // @JoinColumn(name = "race_id")
    // @ManyToOne(cascade = CascadeType.MERGE)
    // private Race race;

    // // public Comment(String comment, User user, Race race) {
    // //     this.comment = comment;
    // //     this.user = user;
    // //     this.race = race;
    // // }

    // public Comment(String comment) {
    //     this.comment = comment;
    // }
}