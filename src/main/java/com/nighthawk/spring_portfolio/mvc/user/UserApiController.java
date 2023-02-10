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
        User userBruh = userRepository.findByEmail(email);

        // if team is not null then it adds user to db, if not it sends bad request
        if (team != null) {
            if (userBruh != null) {
                return new ResponseEntity<>("user already exists", HttpStatus.BAD_REQUEST);
            }
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
    public ResponseEntity<Object> updateRole(@RequestBody final Map<String, Object> map) {
        String email = (String) map.get("email");
        String roleName = (String) map.get("roleName");
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
    public ResponseEntity<Object> makeBet(@RequestBody final Map<String, Object> map) {
        String raceName = (String) map.get("race");
        String raceYear = (String) map.get("raceSeason");
        String teamString = (String) map.get("team");
        String userString = (String) map.get("user");
        Double f1coins = (Double) map.get("f1coins");
        String dateString = (String) map.get("date");
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
            bet.setTeam(team);
            bet.setUser(user);

            user.addF1Coin(-1 * f1coins);

            betRepository.save(bet);
            return new ResponseEntity<>(
                    userString + " has made a bet for " + teamString + " for " + String.valueOf(f1coins) + "f1Coins.",
                    HttpStatus.OK);
        }
        return new ResponseEntity<>("user, team, or race not found", HttpStatus.BAD_REQUEST);
    }

    // TODO: need to make periodic checks on race dates to get bets in
    // make this a check for teams as well???
    @PostMapping
    public ResponseEntity<Object> processBet(@RequestBody final Map<String, Object> map) {
        String dateString = (String) map.get("date");
        Date date;

        try {
            date = new SimpleDateFormat("MM-dd-yyyy").parse(dateString);
        } catch (Exception e) {
            return new ResponseEntity<>(dateString + " error; try MM-dd-yyyy",
                    HttpStatus.BAD_REQUEST);
        }

        Race race = raceRepository.findByDate(date);
        String raceResultWinner = race.getRaceResultWinner();

        if (race != null) {
            return new ResponseEntity<>("race does not exist",
                    HttpStatus.BAD_REQUEST);
        }

        List<Bet> bets = race.getBets();

        for (Bet bet : bets) {
            // TODO: need to pull from bets columns??
            Team team = bet.getTeam();
            if (raceResultWinner.equals(team.getName()) && bet.getBetActive()) {
                User user = bet.getUser();
                user.addF1Coin(2 * bet.getFCoinBet());

                userRepository.save(user);
            }

            bet.setBetActive(false);
            betRepository.save(bet);
        }

        return new ResponseEntity<>("all bets updated", HttpStatus.OK);
    }
}
