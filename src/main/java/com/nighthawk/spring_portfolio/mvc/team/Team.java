package com.nighthawk.spring_portfolio.mvc.team;

import java.util.ArrayList;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.validation.constraints.Size;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.annotations.TypeDef;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.userdetails.memory.UserAttribute;

import com.nighthawk.spring_portfolio.mvc.user.User;
import com.vladmihalcea.hibernate.type.json.JsonType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/*
Person is a POJO, Plain Old Java Object.
First set of annotations add functionality to POJO
--- @Setter @Getter @ToString @NoArgsConstructor @RequiredArgsConstructor
The last annotation connect to database
--- @Entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Team {

    // automatic unique identifier for Person record
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // @NonNull, etc placed in params of constructor: "@NonNull @Size(min = 2, max =
    // 30, message = "Name (2 to 30 chars)") String name"
    @NonNull
    @Size(min = 2, max = 30, message = "Name (2 to 30 chars)")
    private String name;

    @NonNull
    @Size(min = 2, max = 30, message = "Name (2 to 30 chars)")
    private String location;

    // one team has many users (relationship)
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users = new ArrayList<>();

    public Team(String name, String location) {
        this.name = name;
        this.location = location;
    }

    public String toString() {
        return ("{ \"name\": " + this.name + ", " + "\"location\": " + this.location + " }");
    }

    public static void main(String[] args) {
        Team newTeam = new Team();
        newTeam.setName("John");
        newTeam.setLocation("United Kingdom");
        System.out.println(newTeam.toString());
    }

}
