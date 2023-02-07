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
import com.nighthawk.spring_portfolio.mvc.race.Race;
import com.nighthawk.spring_portfolio.mvc.race.RaceJpaRepository;
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
    private RaceJpaRepository raceRepository;

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

    @GetMapping("/bets")
    public ResponseEntity<List<Bet>> getBets() {
        return new ResponseEntity<>(betRepository.findAllByOrderByIdAsc(), HttpStatus.OK);
    }

    // creates new user
    @PostMapping(value = "/newUser", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> newUser(@RequestBody final Map<String, Object> map) {

        // Create DOB
        String dobString = (String) map.get("dob");
        String teamName = (String) map.get("teamName");
        String email = (String) map.get("email");
        String password = (String) map.get("password");
        String gender = (String) map.get("gender");
        String name = (String) map.get("name");
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

    @PostMapping("/makeBet")
    public ResponseEntity<Object> makeBet(@RequestParam("race") String raceName,
            @RequestParam("raceSeason") String raceYear,
            @RequestParam("team") String teamString,
            @RequestParam("user") String userString,
            @RequestParam("f1coins") double f1coins,
            @RequestParam("date") String dateString) {

        Race race = raceRepository.findByNameIgnoreCaseAndSeason(raceName, raceYear);
        Team team = teamRepository.findByName(teamString);
        User user = userRepository.findByName(userString);

        Date date;

        try {
            date = new SimpleDateFormat("MM-dd-yyyy").parse(dateString);
        } catch (Exception e) {
            return new ResponseEntity<>(dateString + " error; try MM-dd-yyyy",
                    HttpStatus.BAD_REQUEST);
        }

        if (user != null && team != null & race != null) {
            Bet bet = new Bet(f1coins, date);

            // add bets to array list
            race.getBets().add(bet);
            team.getBets().add(bet);
            user.getBets().add(bet);

            betRepository.save(bet);
            return new ResponseEntity<>(
                    userString + " has made a bet for " + teamString + " for " + String.valueOf(f1coins) + "f1Coins.",
                    HttpStatus.OK);
        }
        return new ResponseEntity<>("user, team, or race not found", HttpStatus.BAD_REQUEST);
    }

    // TODO: need to make periodic checks on race dates to get bets in
    @PostMapping
    public ResponseEntity<Object> processBet(@RequestParam("date") String dateString) {
        Date date;

        try {
            date = new SimpleDateFormat("MM-dd-yyyy").parse(dateString);
        } catch (Exception e) {
            return new ResponseEntity<>(dateString + " error; try MM-dd-yyyy",
                    HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("all bets updated", HttpStatus.OK);
    }

}
