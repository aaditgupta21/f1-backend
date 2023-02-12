package com.nighthawk.spring_portfolio.mvc.race;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nighthawk.spring_portfolio.mvc.betting.Bet;
import com.nighthawk.spring_portfolio.mvc.betting.BetJpaRepository;
import com.nighthawk.spring_portfolio.mvc.team.Team;
import com.nighthawk.spring_portfolio.mvc.user.User;
import com.nighthawk.spring_portfolio.mvc.user.UserJpaRepository;

@RestController // annotation to create a RESTful web services
@RequestMapping("/api/race") // prefix of API
public class RaceApiController {

    @Autowired
    private RaceJpaRepository raceRepository;

    @Autowired
    private BetJpaRepository betRepository;

    @Autowired
    private UserJpaRepository userRepository;

    private JSONObject body; // last run result
    private HttpStatus status; // last run status

    // GET schedule data
    @GetMapping("/races/{year}") // added to end of prefix as endpoint
    public ResponseEntity<JSONObject> getRaces(@PathVariable String year) {
        // calls API once a day, sets body and status properties
        try { // APIs can fail (ie Internet or Service down)

            // RapidAPI header
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            "http://ergast.com/api/f1/" + year + "/races.json"))
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            // RapidAPI request and response
            HttpResponse<String> response = HttpClient.newHttpClient().send(request,
                    HttpResponse.BodyHandlers.ofString());

            // JSONParser extracts text body and parses to JSONObject
            this.body = (JSONObject) new JSONParser().parse(response.body());
            this.status = HttpStatus.OK; // 200 success
        } catch (Exception e) { // capture failure info
            HashMap<String, String> status = new HashMap<>();
            status.put("status", "RapidApi failure: " + e);

            // Setup object for error
            this.body = (JSONObject) status;
            this.status = HttpStatus.INTERNAL_SERVER_ERROR; // 500 error
        }

        // return JSONObject in RESTful style
        return new ResponseEntity<>(body, status);
    }

    /*
     * GET List of Races
     */
    @GetMapping("/")
    public ResponseEntity<List<Race>> getTeams() {
        return new ResponseEntity<>(raceRepository.findAllByOrderByIdAsc(), HttpStatus.OK);
    }

    @PostMapping("/raceResults")
    public ResponseEntity<Object> raceResults(@RequestParam("date") String dateString) {
        Date date;

        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
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
