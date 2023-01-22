package com.nighthawk.spring_portfolio.mvc.team;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nighthawk.spring_portfolio.mvc.user.User;
import com.nighthawk.spring_portfolio.mvc.user.UserJpaRepository;

import java.util.*;

import java.text.SimpleDateFormat;

@RestController
@RequestMapping("/api/auth")
public class TeamApiController {
    // Autowired enables Control to connect POJO Object through JPA
    @Autowired
    private TeamJpaRepository teamRepository;

    @Autowired
    private UserJpaRepository userRepository;

    Map<String, Integer> teamToUser = new HashMap<String, Integer>();

    /*
     * GET List of Teams
     */
    @GetMapping("/teamsList")
    public ResponseEntity<List<Team>> getTeams() {
        return new ResponseEntity<>(teamRepository.findAllByOrderByNameAsc(), HttpStatus.OK);
    }

    /*
     * GET List of users
     */
    @GetMapping("/usersList")
    public ResponseEntity<List<User>> getUsers() {
        return new ResponseEntity<>(userRepository.findAllByOrderByNameAsc(), HttpStatus.OK);
    }

    // TODO: needs security access since we only want admins to create a new team
    @PostMapping(value = "/newTeam", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> newTeam(@RequestParam("name") String name,
            @RequestParam("location") String location) {

        Team team = new Team(name, location);
        teamRepository.save(team);
        return new ResponseEntity<>(name + " team has been successfully created", HttpStatus.CREATED);
    }

    // creates new user
    @PostMapping(value = "/newUser", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> newUser(@RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("name") String name,
            @RequestParam("dob") String dobString,
            @RequestParam("gender") char gender,
            @RequestParam("teamID") long teamID) { // preferably we use team name instead (TODO: make a map for id &
                                                   // name?)

        // Create DOB
        Date dob;

        try {
            dob = new SimpleDateFormat("MM-dd-yyyy").parse(dobString);
        } catch (Exception e) {
            return new ResponseEntity<>(dobString + " error; try MM-dd-yyyy",
                    HttpStatus.BAD_REQUEST);
        }

        // find team by ID
        Optional<Team> optional = teamRepository.findById(teamID);
        if (optional.isPresent()) { // Good ID
            Team team = optional.get(); // value from findByID
            User user = new User(email, password, gender, name, dob);

            team.getUsers().add(user);
            teamRepository.save(team); // conclude by writing the user updates

            // return user (or return w message of successfully created user)
            return new ResponseEntity<>(email + " user created successfully", HttpStatus.OK);
        }

        // return Bad ID
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
