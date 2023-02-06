package com.nighthawk.spring_portfolio.mvc.user;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.annotations.TypeDef;
import org.springframework.format.annotation.DateTimeFormat;

import com.nighthawk.spring_portfolio.mvc.betting.Bet;
import com.nighthawk.spring_portfolio.mvc.role.Role;
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
@TypeDef(name = "json", typeClass = JsonType.class)
public class User {

    // automatic unique identifier for user record
    // GenerationType.IDENTITY used to prevent db locking (could use join column in
    // Team.java i think but oh well)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // email, password, roles are key attributes to login and authentication
    @NotEmpty
    @Size(min = 5)
    @Column(unique = true)
    @Email
    private String email;

    @NotEmpty
    private String password;

    private String gender;

    // @NonNull, etc placed in params of constructor: "@NonNull @Size(min = 2, max =
    // 30, message = "Name (2 to 30 chars)") String name"
    @NonNull
    @Size(min = 2, max = 30, message = "Name (2 to 30 chars)")
    private String name;

    @DateTimeFormat(pattern = "MM-dd-yyyy")
    private Date dob;

    @Positive
    private double f1coin;

    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Role> roles = new ArrayList<>();

    // @JoinColumn(name = "user_id")
    // @OneToMany(cascade = CascadeType.ALL)
    // private List<Bet> bets = new ArrayList<>();

    // A custom getter to return age from dob attribute
    public int getAge() {
        if (this.dob != null) {
            LocalDate birthDay = this.dob.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            return Period.between(birthDay, LocalDate.now()).getYears();
        }
        return -1;
    }

    public String toString() {
        return ("{ \"email\": " + this.email + ", " + "\"password\": " + this.password + ", " + "\"name\": " + this.name
                + ", " + "\"dob\": " + this.dob + ", " + "\"gender\": " + this.gender + ", " + " \"f1coin\": "
                + this.f1coin + "}");
    }

    public String getAgeToString() {
        return ("{ \"name\": " + this.name + " ," + "\"age\": " + this.getAge() + " }");
    }

    public User(String email, String password, String gender, String name, Date dob, Role role, double f1coin) {
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.name = name;
        this.dob = dob;
        this.roles.add(role);
        this.f1coin = f1coin;
    }

    public static void main(String[] args) {
        User person = new User();
        Date dob2 = new GregorianCalendar(2006, 4, 2).getTime();
        person.setDob(dob2);
        person.setName("John");
        person.setGender("Male");
        person.setF1coin(10000);
        System.out.println(person.toString());
        System.out.println(person.getAge());
    }

}
