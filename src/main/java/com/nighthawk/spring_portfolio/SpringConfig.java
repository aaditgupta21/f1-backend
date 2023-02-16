package com.nighthawk.spring_portfolio;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.nighthawk.spring_portfolio.mvc.race.Race;
import com.nighthawk.spring_portfolio.mvc.race.RaceJpaRepository;

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

    @Scheduled(fixedRate = 60000)
    public void raceResults() throws Exception {
        ZoneId defaultZoneId = ZoneId.systemDefault();
        Date date = Date.from(LocalDate.now().atStartOfDay(defaultZoneId).toInstant());

        Race race = raceRepository.findByDate(date);

        if (race == null) {
            System.out.println("Checked for Race Results: Race Not Found");
            return;
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

        System.out.println("Checked for Race Results: Race Winner Updated");
    }

}
