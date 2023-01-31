package com.nighthawk.spring_portfolio.mvc.betting;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.nighthawk.spring_portfolio.mvc.user.User;
import com.nighthawk.spring_portfolio.mvc.user.UserJpaRepository;
import com.nighthawk.spring_portfolio.mvc.team.Team;
import com.nighthawk.spring_portfolio.mvc.team.TeamJpaRepository;
import com.nighthawk.spring_portfolio.mvc.race.Race;
import com.nighthawk.spring_portfolio.mvc.race.RaceJpaRepository;

import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/bets")
public class BetApiController {
    // Autowired enables Control to connect POJO Object through JPA
    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private TeamJpaRepository teamRepository;

    @Autowired
    private RaceJpaRepository raceRepository;

    @Transactional
    @PostMapping(value = "/newBet", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> newBet(@RequestParam("raceName") String raceName,
            @RequestParam("teamName") String teamName, @RequestParam("fCoinBet") double fCoinBet,
            @RequestParam("userName") String userName, @RequestParam("date") String dateString) {
        Race race = raceRepository.findByName(raceName);
        Team team = teamRepository.findByName(teamName);
        User user = userRepository.findByName(userName);

        // Create bet date
        Date date;

        try {
            date = new SimpleDateFormat("MM-dd-yyyy").parse(dateString);
        } catch (Exception e) {
            return new ResponseEntity<>(dateString + " error; try MM-dd-yyyy",
                    HttpStatus.BAD_REQUEST);
        }

        if (race != null || team != null || user != null) {
            Bet bet = new Bet(race, team, fCoinBet, date);
            // user.getBets().add(bet);
            userRepository.save(user);
            return new ResponseEntity<>(userName + " has made a bet of " + fCoinBet + " fCoins for " + teamName,
                    HttpStatus.OK);
        }

        return new ResponseEntity<>("invalid bet, check teamName, userName, or raceName", HttpStatus.BAD_REQUEST);
    }
}
