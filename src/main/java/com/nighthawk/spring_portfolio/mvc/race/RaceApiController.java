package com.nighthawk.spring_portfolio.mvc.race;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private UserJpaRepository userRepository;
    @Autowired
    private CommentJpaRepository commentRepository;

    private JSONObject body; // last run result
    private HttpStatus status; // last run status

    /*
     * GET List of Races
     */
    @GetMapping("/")
    public ResponseEntity<List<Race>> getRaces() {
        return new ResponseEntity<>(raceRepository.findAllByOrderByIdAsc(), HttpStatus.OK);
    }

    @GetMapping("/getCommentsByUser")
    public ResponseEntity<Object> getCommentsByUser(@RequestBody final Map<String, Object> map) {
        String userId = (String) map.get("user");

        Long id = Long.parseLong(userId);
        User user = userRepository.findById(id).orElse(null);

        return new ResponseEntity<>(commentRepository.findAllByUser(user),
                HttpStatus.OK);
    }

    @PostMapping("/makeComment")
    public ResponseEntity<Object> makeComment(@RequestBody final Map<String, Object> map) {
        String userId = (String) map.get("user");
        String season = (String) map.get("season");
        String comment = (String) map.get("comment");

        Long id = Long.parseLong(userId);
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return new ResponseEntity<>("user not found", HttpStatus.BAD_REQUEST);
        }

        Comment commentObj = new Comment(comment, season, user);
        commentRepository.save(commentObj);

        return new ResponseEntity<>("comment made teehee", HttpStatus.OK);
    }

    @GetMapping("/getComments")
    public ResponseEntity<List<Comment>> getComments() {
        return new ResponseEntity<>(commentRepository.findAllByOrderByIdAsc(), HttpStatus.OK);
    }

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

    @GetMapping("/races/winner/{year}") // added to end of prefix as endpoint
    public ResponseEntity<JSONObject> getRaceWinner(@PathVariable String year) {
        // calls API once a day, sets body and status properties
        try { // APIs can fail (ie Internet or Service down)

            // RapidAPI header
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            "https://ergast.com/api/f1/" + year + "/results.json"))
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

    // creates new user
    @PostMapping(value = "/customRace", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> newRace(@RequestBody final Map<String, Object> map) {

        // Create DOB
        String raceName = (String) map.get("raceName");
        String circuit = (String) map.get("circuit");
        String dateString = (String) map.get("date");
        String location = (String) map.get("location");
        String season = (String) map.get("season");

        int round = -1;
        Date date;

        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
        } catch (Exception e) {
            return new ResponseEntity<>(dateString + " error; try yyyy-MM-dd",
                    HttpStatus.BAD_REQUEST);
        }

        Race race = new Race(raceName, circuit, date, round, location, season);
        raceRepository.save(race);

        return new ResponseEntity<>(raceName + " successfully created",
                HttpStatus.OK);
    }

    @PostMapping("/raceResults")
    public ResponseEntity<Object> raceResults(@RequestParam("date") String dateString) throws Exception {
        Date date;

        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
        } catch (Exception e) {
            return new ResponseEntity<>(dateString + " error; try yyyy-MM-dd",
                    HttpStatus.BAD_REQUEST);
        }

        Race race = raceRepository.findByDate(date);

        if (race.equals(null)) {
            return new ResponseEntity<>("race does not exist",
                    HttpStatus.BAD_REQUEST);
        }

        JSONObject data;
        String year = String.valueOf(date.getYear() + 1900);
        String roundNumber = String.valueOf(race.getRound() - 1);

        try { // APIs can fail (ie Internet or Service down)
              // RapidAPI header
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            "http://ergast.com/api/f1/" + year + "/" + roundNumber + "/results.json"))
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            System.out.println("http://ergast.com/api/f1/" + year + "/" + roundNumber + "/results.json");
            // RapidAPI request and response
            HttpResponse<String> response = HttpClient.newHttpClient().send(request,
                    HttpResponse.BodyHandlers.ofString());

            // JSONParser extracts text body and parses to JSONObject
            data = (JSONObject) new JSONParser().parse(response.body());
        } catch (Exception e) { // capture failure info
            return new ResponseEntity<>("api failed",
                    HttpStatus.BAD_REQUEST);
        }

        JSONObject mrData = (JSONObject) data.get("MRData");
        JSONObject raceTable = (JSONObject) mrData.get("RaceTable");
        JSONArray racesData = (JSONArray) raceTable.get("Races");
        JSONObject raceIndex = (JSONObject) racesData.get(0);
        JSONArray results = (JSONArray) raceIndex.get("Results");
        JSONObject result = (JSONObject) results.get(0);
        JSONObject constructor = (JSONObject) result.get("Constructor");
        String constructorID = (String) constructor.get("constructorId");

        race.setRaceResultWinner(constructorID);
        raceRepository.save(race);

        return new ResponseEntity<>("race results updated", HttpStatus.OK);
    }

    // creates new user
    @PostMapping(value = "/declareWinner", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> declareWinner(@RequestBody final Map<String, Object> map) {

        // Create DOB
        String raceName = (String) map.get("raceName");
        String season = (String) map.get("season");
        String winner = (String) map.get("winner");

        Race race = raceRepository.findByNameIgnoreCaseAndSeason(raceName, season);
        race.setRaceResultWinner(winner);
        raceRepository.save(race);

        return new ResponseEntity<>(winner + " declared as the winner for " + raceName + " " + season + " season!",
                HttpStatus.OK);
    }

    @GetMapping("/{year}")
    public ResponseEntity<Race> getRacesByYear(@PathVariable String year) {
        return new ResponseEntity<>(raceRepository.findBySeason(year), HttpStatus.OK);
    }

    @GetMapping("/raceDates")
    public ResponseEntity<Object> raceDates() {
        List<Race> races = raceRepository.findAllByOrderByIdAsc();
        List<Race> recentRaces = races.subList(races.size() - 13, races.size() - 1);

        String json = "[";

        for (Race race : recentRaces) {
            json += "{ " + "\"raceName\": " + race.getName() + ", " + "\"date\": " + race.getDate().toString() + " }";
            if (!race.equals(recentRaces.get(recentRaces.size() - 1))) {
                json += ", ";
            }
        }

        json += " ]";

        return new ResponseEntity<>(json, HttpStatus.OK);
    }
}
