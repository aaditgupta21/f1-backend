package com.nighthawk.spring_portfolio.mvc.team;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nighthawk.spring_portfolio.mvc.user.User;
import com.nighthawk.spring_portfolio.mvc.user.UserJpaRepository;
import com.nighthawk.spring_portfolio.mvc.drivelog.DriveLogJpaRepository;
import com.nighthawk.spring_portfolio.mvc.drivelog.DriveLog;

import java.util.*;

import org.springframework.transaction.annotation.Transactional;
import java.text.SimpleDateFormat;

@RestController
@RequestMapping("/api/team")
public class TeamApiController {
    // Autowired enables Control to connect POJO Object through JPA
    @Autowired
    private TeamJpaRepository teamRepository;

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private DriveLogJpaRepository driverLogJpaRepository;

    /*
     * GET List of Teams
     */
    // TODO: 500 error and idky
    @GetMapping("/")
    public ResponseEntity<List<Team>> getTeams() {
        return new ResponseEntity<>(teamRepository.findAllByOrderByNameAsc(), HttpStatus.OK);
    }

    /*
     * GET List of Teams
     */
    @GetMapping("/drivelogs")
    public ResponseEntity<List<DriveLog>> getDriveLogs() {
        return new ResponseEntity<>(driverLogJpaRepository.findAllByOrderByIdAsc(), HttpStatus.OK);
    }

    // TODO: needs security access since we only want admins to create a new team
    @PostMapping(value = "/newTeam", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> newTeam(@RequestBody final Map<String, Object> map) {

        String name = (String) map.get("name");
        String location = (String) map.get("location");
        Team team = new Team(name, location);
        teamRepository.save(team);
        return new ResponseEntity<>(name + " team has been successfully created", HttpStatus.CREATED);
    }


    @PostMapping("/updateCoins")
    public ResponseEntity<Object> updateCoins(@RequestBody final Map<String, Object> map) {
        String email = (String) map.get("email");
        Double f1coin = (Double) map.get("f1coin");
        User user = userRepository.findByName(email);
        if (user != null) {
            user.addF1Coin(f1coin);
            userRepository.save(user);
            return new ResponseEntity<>("Added " + f1coin + " F1Coins to " + email, HttpStatus.OK);
        }
        return new ResponseEntity<>("user not found", HttpStatus.BAD_REQUEST);
    }

    @Transactional
    @PostMapping(value = "/setDriverLog", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> driverLog(@RequestBody final Map<String, Object> map) {

        // find the person by ID
        String teamName = (String) map.get("teamName");
        String dateString = (String) map.get("date");
        Double miles = (Double) map.get("miles");
        Double time = (Double) map.get("time");
        String raceName = (String) map.get("raceName");
        Team team = teamRepository.findByName(teamName);
        if (team != null) { // Good ID
            Date date;
            try {
                date = new SimpleDateFormat("MM-dd-yyyy").parse(dateString);
            } catch (Exception e) {
                return new ResponseEntity<>(dateString + " error; try MM-dd-yyyy", HttpStatus.BAD_REQUEST);
            }

            DriveLog driverLog = new DriveLog(date, miles, time, raceName);
            team.getDrivelogs().add(driverLog);

            teamRepository.save(team); // conclude by writing the stats updates

            // return Person with update Stats
            return new ResponseEntity<>(team, HttpStatus.OK);
        }
        // return Bad ID
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }
    @DeleteMapping("/drivelog/delete")
    public ResponseEntity<DriveLog> deleteDriveLog(@RequestParam long id) {
        Optional<DriveLog> optional = driverLogJpaRepository.findById(id);
        if (optional.isPresent()) { // Good ID
            DriveLog driverLog = optional.get(); // value from findByID
            driverLogJpaRepository.deleteById(id); // value from findByID
            return new ResponseEntity<>(driverLog, HttpStatus.OK); // OK HTTP response: status code, headers, and body
        }
        // Bad ID
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
