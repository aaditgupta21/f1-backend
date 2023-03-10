package com.nighthawk.spring_portfolio.mvc.user;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
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

import javax.transaction.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

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

    // get coin for a user get request with id as pathvariable
    @GetMapping("/coins/{id}")
    public ResponseEntity<Object> getCoin(@PathVariable("id") final Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            return new ResponseEntity<>(user.getF1coin(), HttpStatus.OK);
        }
        return new ResponseEntity<>("user not found", HttpStatus.BAD_REQUEST);
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

            user.setTeam(team);
            teamRepository.save(team); // conclude by writing the user updates
            userRepository.save(user);
            // return email (or return w message of successfully created user)
            return new ResponseEntity<>(email + " user created successfully", HttpStatus.OK);
        } else {
            // returns team name could not be found and bad request
            return new ResponseEntity<>("team name is invalid", HttpStatus.BAD_REQUEST);
        }

    }

    // update role
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

    // update user
    @PostMapping("/updateUser")
    public ResponseEntity<Object> updateUser(@RequestBody final Map<String, Object> map) {

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

        User user = userRepository.findByName(name);
        Team team = teamRepository.findByName(teamName);

        // TODO: handle team change

        if (user != null && team != null) {
            user.setDob(dob);
            user.setEmail(email);
            user.setPassword(password);
            user.setGender(gender);
            user.setName(name);

            userRepository.save(user);

            return new ResponseEntity<>(name + " user updated", HttpStatus.OK);
        }
        return new ResponseEntity<>("user not found", HttpStatus.BAD_REQUEST);
    }

    // delete user
    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteUser(@RequestBody final Map<String, Object> map) {
        String name = (String) map.get("name");
        User user = userRepository.findByName(name);

        if (user != null) {
            userRepository.delete(user);
            return new ResponseEntity<>(name + " user deleted", HttpStatus.OK);
        }

        return new ResponseEntity<>("user not found", HttpStatus.BAD_REQUEST);
    }

    // makes a new bet (transactional to circumvent tranasction between multiple
    // relationships)
    @Transactional
    @PostMapping("/makeBet")
    public ResponseEntity<Object> makeBet(@RequestBody final Map<String, Object> map) {
        String raceName = (String) map.get("race");
        String raceYear = (String) map.get("raceSeason");
        String teamString = (String) map.get("team");
        String userIDString = (String) map.get("user");
        String f1coins = (String) (map.get("f1coins"));

        Boolean overwrite = (Boolean) (map.get("overwrite"));
        if (overwrite == null) {
            overwrite = false;
        }

        Long userID = Long.parseLong(userIDString);
        double f1coinValue = Double.valueOf(f1coins);

        Race race = raceRepository.findByNameIgnoreCaseAndSeason(raceName, raceYear);
        Team team = teamRepository.findByName(teamString);
        User user = userRepository.findById(userID).orElse(null);

        if (user != null && team != null & race != null) {
            ZoneId defaultZoneId = ZoneId.systemDefault();
            Date date = Date.from(LocalDate.now().atStartOfDay(defaultZoneId).toInstant());

            if (overwrite) {
            } else if (!date.before(race.getDate())) {
                return new ResponseEntity<>("invalid, already past race deadline",
                        HttpStatus.BAD_REQUEST);
            }

            if (f1coinValue > user.getF1coin()) {
                return new ResponseEntity<>("not enough f1 coins!", HttpStatus.BAD_REQUEST);
            }

            Bet bet = new Bet(f1coinValue, date);

            // add bets to array list
            bet.setRace(race);
            bet.setTeam(team);
            bet.setUser(user);

            user.addF1Coin(-1 * f1coinValue);

            betRepository.save(bet);
            userRepository.save(user);
            return new ResponseEntity<>(
                    "Bet for " + teamString + " of " + String.valueOf(f1coins)
                            + " f1Coins.",
                    HttpStatus.OK);
        }

        return new ResponseEntity<>("user, team, or race not found", HttpStatus.BAD_REQUEST);
    }

    // manual processing of bets (usually handled in SpringConfig file)
    @PostMapping("/processBets")
    public ResponseEntity<Object> processBets(@RequestBody final Map<String, Object> map) {
        String dateString = (String) map.get("date");

        Date date;

        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
        } catch (Exception e) {
            return new ResponseEntity<>(dateString + " error; try yyyy-MM-dd",
                    HttpStatus.BAD_REQUEST);
        }

        Race race = raceRepository.findByDate(date);

        if (race == null) {
            return new ResponseEntity<>("race does not exist",
                    HttpStatus.BAD_REQUEST);
        }

        String raceResultWinner = race.getRaceResultWinner();

        List<Bet> bets = betRepository.findAllByRace(race);

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

    // gets bet by userid
    @GetMapping("/getBets/{idString}") // fix
    public ResponseEntity<?> getBets(@PathVariable String idString) {
        Long userId = Long.valueOf(idString);
        User user = userRepository.findById(userId).orElse(null);

        List<Bet> bets = betRepository.findAllByUser(user);

        List<JSONObject> entities = new ArrayList<JSONObject>();
        for (Bet bet : bets) {
            JSONObject entity = new JSONObject();
            entity.put("id", bet.getId());
            entity.put("teamName", bet.getTeam().getName());
            entity.put("raceName", bet.getRace().getName());
            entity.put("raceDate", bet.getRace().getDate().toString());
            entity.put("f1Coins", String.valueOf(bet.getFCoinBet()));
            entity.put("betActive", String.valueOf(bet.getBetActive()));
            entities.add(entity);
        }

        return new ResponseEntity<>(entities, HttpStatus.OK);
    }

    // update bet by passing id and changing f1coin and team
    @PostMapping("/updateBet")
    public ResponseEntity<Object> updateBet(@RequestBody final Map<String, Object> map) {
        String betIdString = (String) map.get("betId");
        Long betId = Long.parseLong(betIdString);

        String teamString = (String) map.get("team");
        String f1coins = (String) (map.get("f1coins"));

        Team team = teamRepository.findByName(teamString);

        if (team == null) {
            return new ResponseEntity<>("team could not be found", HttpStatus.BAD_REQUEST);
        }

        Bet bet = betRepository.findById(betId).orElse(null);
        if (bet != null && bet.getBetActive() == true) {
            User user = bet.getUser();
            user.addF1Coin(bet.getFCoinBet());

            if (Double.valueOf(f1coins) > user.getF1coin()) {
                return new ResponseEntity<>("not enough f1Coins", HttpStatus.BAD_REQUEST);
            }

            user.addF1Coin(-Double.valueOf(f1coins));
            userRepository.save(user);

            bet.setFCoinBet(Double.valueOf(f1coins));
            if (!bet.getTeam().getId().equals(team.getId())) {
                bet.setTeam(team);
            }
            betRepository.save(bet);

            return new ResponseEntity<>("Successfully changed bet", HttpStatus.OK);
        }

        return new ResponseEntity<>("bet could not be found/not active", HttpStatus.BAD_REQUEST);
    }

    // delete bet
    @PostMapping("/deleteBet")
    public ResponseEntity<Object> deleteBet(@RequestBody final Map<String, Object> map) {
        String betIdString = (String) map.get("betId");
        Long betId = Long.parseLong(betIdString);

        Bet bet = betRepository.findById(betId).orElse(null);
        if (bet != null) {
            betRepository.delete(bet);
            return new ResponseEntity<>("successfully deleted bet", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("bet not found", HttpStatus.BAD_REQUEST);
        }
    }
}
