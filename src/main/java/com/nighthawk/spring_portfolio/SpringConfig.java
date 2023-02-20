package com.nighthawk.spring_portfolio;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.nighthawk.spring_portfolio.mvc.betting.Bet;
import com.nighthawk.spring_portfolio.mvc.betting.BetJpaRepository;
import com.nighthawk.spring_portfolio.mvc.race.Race;
import com.nighthawk.spring_portfolio.mvc.race.RaceJpaRepository;
import com.nighthawk.spring_portfolio.mvc.team.Team;
import com.nighthawk.spring_portfolio.mvc.user.User;
import com.nighthawk.spring_portfolio.mvc.user.UserJpaRepository;

@Configuration
@EnableScheduling
public class SpringConfig {

    // // testing scheduled annotation
    // @Scheduled(fixedRate = 60000)
    // public void scheduleFixedRateTask() {
    // System.out.println(
    // "Fixed rate task - " + System.currentTimeMillis() / 1000);
    // }

    @Autowired
    private RaceJpaRepository raceRepository;

    @Autowired
    private BetJpaRepository betRepository;

    @Autowired
    private UserJpaRepository userRepository;

    @Scheduled(fixedRate = 60000)
    public void periodic() throws Exception {
        ZoneId defaultZoneId = ZoneId.systemDefault();
        Date date = Date.from(LocalDate.now().atStartOfDay(defaultZoneId).toInstant());

        Race race = raceRepository.findByDate(date);

        if (race == null) {
            System.out.println("Checked for Race Results: Race Not Found (test that runs periodically dw abt it)");
            return;
        }

        raceResults(date, race);
        processBets(race);

        System.out.println("Checked for Race Results: Updated Races And Bets (test that runs periodically dw abt it)");
    }

    public void raceResults(Date date, Race race) throws Exception {
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
            return;
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
    }

    public void processBets(Race race) {
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
    }
}
