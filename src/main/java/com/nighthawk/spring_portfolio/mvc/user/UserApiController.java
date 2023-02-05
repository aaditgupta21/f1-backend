package com.nighthawk.spring_portfolio.mvc.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCrypt;
import com.nighthawk.spring_portfolio.mvc.user.User;
import com.nighthawk.spring_portfolio.mvc.user.UserJpaRepository;
import com.nighthawk.spring_portfolio.mvc.betting.Bet;
import com.nighthawk.spring_portfolio.mvc.betting.BetJpaRepository;
import com.nighthawk.spring_portfolio.mvc.role.Role;
import com.nighthawk.spring_portfolio.mvc.role.RoleJpaRepository;
import com.nighthawk.spring_portfolio.mvc.team.Team;
import com.nighthawk.spring_portfolio.mvc.team.TeamJpaRepository;

import java.util.*;

import java.text.SimpleDateFormat;

@RestController
@RequestMapping("/api/user")
public class UserApiController {
    @Autowired
    private TeamJpaRepository teamRepository;

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private RoleJpaRepository roleRepository;

    @Autowired
    private BetJpaRepository betRepository;

    /*
     * GET List of users
     */

    @GetMapping("/")
    public ResponseEntity<List<User>> getUsers() {
        return new ResponseEntity<>(userRepository.findAllByOrderByNameAsc(), HttpStatus.OK);
    }

    /*
     * GET List of bets
     */

    @GetMapping("/")
    public ResponseEntity<List<Bet>> getBets() {
        return new ResponseEntity<>(betRepository.findAllByOrderByIdAsc(), HttpStatus.OK);
    }

    // creates new user
    @PostMapping(value = "/newUser", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> newUser(@RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("name") String name,
            @RequestParam("dob") String dobString,
            @RequestParam("gender") char gender,
            @RequestParam("teamName") String teamName) {

        // Create DOB
        Date dob;

        try {
            dob = new SimpleDateFormat("MM-dd-yyyy").parse(dobString);
        } catch (Exception e) {
            return new ResponseEntity<>(dobString + " error; try MM-dd-yyyy",
                    HttpStatus.BAD_REQUEST);
        }

        // find team by name
        Team team = teamRepository.findByName(teamName);

        // if team is not null then it adds user to db, if not it sends bad request
        if (team != null) {
            Role roleStudent = roleRepository.findByName("User");
            password = BCrypt.hashpw(password, BCrypt.gensalt());
            User user = new User(email, password, gender, name, dob, roleStudent, 100.0);

            team.getUsers().add(user);
            teamRepository.save(team); // conclude by writing the user updates

            // return email (or return w message of successfully created user)
            return new ResponseEntity<>(email + " user created successfully", HttpStatus.OK);
        } else {
            // returns team name could not be found and bad request
            return new ResponseEntity<>("team name is invalid", HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("/updateRole")
    public ResponseEntity<Object> updateRole(@RequestParam("email") String email,
            @RequestParam("roleName") String roleName) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            Role role = roleRepository.findByName(roleName);
            user.getRoles().add(role);
            userRepository.save(user);
            return new ResponseEntity<>(email + " role updated", HttpStatus.OK);
        }
        return new ResponseEntity<>("user not found", HttpStatus.BAD_REQUEST);
    }
}
