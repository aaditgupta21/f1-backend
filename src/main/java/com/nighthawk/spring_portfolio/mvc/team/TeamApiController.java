package com.nighthawk.spring_portfolio.mvc.team;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.nighthawk.spring_portfolio.mvc.user.UserJpaRepository;
import com.nighthawk.spring_portfolio.mvc.role.RoleJpaRepository;
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
    private RoleJpaRepository roleRepository;

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
    public ResponseEntity<Object> newTeam(@RequestParam("name") String name,
            @RequestParam("location") String location) {

        Team team = new Team(name, location);
        teamRepository.save(team);
        return new ResponseEntity<>(name + " team has been successfully created", HttpStatus.CREATED);
    }

    @Transactional
    @PostMapping(value = "/setDriverLog", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> driverLog(@RequestParam("teamName") String teamName,
            @RequestParam("date") String dateString,
            @RequestParam("miles") double miles,
            @RequestParam("time") double time) {

        // find the person by ID
        Team team = teamRepository.findByName(teamName);
        if (team != null) { // Good ID
            Date date;
            try {
                date = new SimpleDateFormat("MM-dd-yyyy").parse(dateString);
            } catch (Exception e) {
                return new ResponseEntity<>(dateString + " error; try MM-dd-yyyy", HttpStatus.BAD_REQUEST);
            }

            DriveLog driverLog = new DriveLog(date, miles, time);
            team.getDrivelogs().add(driverLog);

            teamRepository.save(team); // conclude by writing the stats updates

            // return Person with update Stats
            return new ResponseEntity<>(team, HttpStatus.OK);
        }
        // return Bad ID
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }
}
