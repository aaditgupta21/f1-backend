package com.nighthawk.spring_portfolio.mvc.calendar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // annotation to create a RESTful web services
@RequestMapping("/api/calendar") // prefix of API
public class CalendarApiController {

    @Autowired
    private CalendarJpaRepository calendarJpaRepository;

    // GET schedule data
    @PostMapping(value = "/newCalendar", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> newTeam(@RequestParam("entry") String entry,
            @RequestParam("event") String event,
            @RequestParam("note") String note,
            @RequestParam("dateOfEvent") String dateOfEvent) {
                Date date;
                try {
                    date = new SimpleDateFormat("MM-dd-yyyy").parse(dateOfEvent);
                } catch (Exception e) {
                    return new ResponseEntity<>(date + " error; try MM-dd-yyyy",
                            HttpStatus.BAD_REQUEST);
                }
        Calendar calendar = new Calendar(event, note, dateOfEvent);
        calendarJpaRepository.save(calendar);
    }

    /*
     * GET List of Races
     */
    @GetMapping("/")
    public ResponseEntity<List<Calendar>> getCalendar() {
        return new ResponseEntity<>(calendarJpaRepository.findAllByOrderByIdAsc(), HttpStatus.OK);
    }
}

