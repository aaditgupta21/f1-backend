package com.nighthawk.spring_portfolio.mvc;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;

import com.nighthawk.spring_portfolio.mvc.role.RoleJpaRepository;
import com.nighthawk.spring_portfolio.mvc.role.Role;
import com.nighthawk.spring_portfolio.mvc.race.*;

@Component // Scans Application for ModelInit Bean, this detects CommandLineRunner
public class ModelInit {
    @Autowired
    RoleJpaRepository roleJpaRepository;

    @Autowired
    private RaceJpaRepository repository;

    private void getRaceByYear(String year) throws Exception {
        JSONObject data;
        try { // APIs can fail (ie Internet or Service down)

            // RapidAPI header
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            "http://ergast.com/api/f1/" + year + ".json"))
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            // RapidAPI request and response
            HttpResponse<String> response = HttpClient.newHttpClient().send(request,
                    HttpResponse.BodyHandlers.ofString());

            // JSONParser extracts text body and parses to JSONObject
            data = (JSONObject) new JSONParser().parse(response.body());
        } catch (Exception e) { // capture failure info
            System.out.println(e);
            return;
        }

        JSONObject mrData = (JSONObject) data.get("MRData");
        JSONObject raceTable = (JSONObject) mrData.get("RaceTable");
        JSONArray racesData = (JSONArray) raceTable.get("Races");

        String season = (String) raceTable.get("season");

        // make sure people array database is populated with starting values for members
        for (Object race : racesData) {
            JSONObject raceJSON = (JSONObject) race;

            String name = (String) raceJSON.get("raceName");
            String circuit = (String) ((JSONObject) raceJSON.get("Circuit")).get("circuitName");
            String dateString = (String) raceJSON.get("date");
            String roundString = (String) raceJSON.get("round");

            JSONObject location = (JSONObject) ((JSONObject) raceJSON.get("Circuit")).get("Location");
            String locality = (String) location.get("locality");

            List<Race> test = repository.findAllByNameIgnoreCaseAndSeason(name, season);

            Date date;

            if (test.size() == 0) {
                try {
                    date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
                    repository.save(new Race(name, circuit, date, Integer.parseInt(roundString), locality, season)); // JPA
                    // save
                } catch (Exception e) {
                    throw e;
                }
            }
        }
    }

    private void allRaceResults() throws Exception {
        List<Race> races = repository.findAllByOrderByIdAsc();

        for (Race race : races) {
            JSONObject data;
            String year = String.valueOf(race.getDate().getYear() + 1900);
            String roundNumber = String.valueOf(race.getRound() - 1);

            if (race.getRound() == -1) {
                return;
            }

            try { // APIs can fail (ie Internet or Service down)
                  // RapidAPI header
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(
                                "http://ergast.com/api/f1/" + year + "/" + roundNumber + "/results.json"))
                        .method("GET", HttpRequest.BodyPublishers.noBody())
                        .build();

                // System.out.println("http://ergast.com/api/f1/" + year + "/" + roundNumber +
                // "/results.json");
                // RapidAPI request and response
                HttpResponse<String> response = HttpClient.newHttpClient().send(request,
                        HttpResponse.BodyHandlers.ofString());

                // JSONParser extracts text body and parses to JSONObject
                data = (JSONObject) new JSONParser().parse(response.body());
            } catch (Exception e) { // capture failure info
                System.out.println(e);
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
            repository.save(race);
        }
    }

    @Bean
    CommandLineRunner run() { // The run() method will be executed after the application starts
        return args -> {

            String[] roles = { "User", "Admin", "Coach", "Pit", "Driver" };
            for (String role : roles) {
                if (roleJpaRepository.findByName(role) == null)
                    roleJpaRepository.save(new Role(null, role));
            }

            for (int year = 1950; year <= 2022; year++) {
                getRaceByYear(String.valueOf(year));
            }

            allRaceResults();

        };
    }
}
