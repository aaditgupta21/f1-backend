package com.nighthawk.spring_portfolio.mvc.calendar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;


@RestController // annotation to create a RESTful web services
@RequestMapping("/api/calendar") // prefix of API
public class CalendarApiController {

    @Autowired
    private CalendarJpaRepository calendarJpaRepository;

    // GET schedule data
    @PostMapping(value = "/newCalendar", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> newTeam(@RequestBody final Map<String, Object> map) {
        String event = (String) map.get("event");
        String dateOfEvent = (String) map.get("dateOfEvent");
        String note = (String) map.get("note");
        Date date;
        try {
            date = new SimpleDateFormat("MM-dd-yyyy").parse(dateOfEvent);
        } catch (Exception e) {
            return new ResponseEntity<>(dateOfEvent + " error; try MM-dd-yyyy",
                    HttpStatus.BAD_REQUEST);
        }
        Calendar calendar2 = new Calendar(event, note, date);
        calendarJpaRepository.save(calendar2);
        return new ResponseEntity<>(event + " listed successfully!", HttpStatus.CREATED);
    }


    @PostMapping("/updateCalendar")
    public ResponseEntity<Object> updateUser(@RequestBody final Map<String, Object> map) {

        // Create date
        String event = (String) map.get("event");
        String dateOfEvent = (String) map.get("dateOfEvent");
        String note = (String) map.get("note");
        Date date;

        Calendar calendar = calendarJpaRepository.findByEvent(event);

        try {
            date = new SimpleDateFormat("MM-dd-yyyy").parse(dateOfEvent);
        } catch (Exception e) {
            return new ResponseEntity<>(dateOfEvent + " error; try MM-dd-yyyy",
                    HttpStatus.BAD_REQUEST);
        }

        if (calendar != null) {
            calendar.setEvent(event);
            calendar.setDateOfEvent(date);
            calendar.setNote(note);


            calendarJpaRepository.save(calendar);

            return new ResponseEntity<>(event + " event updated", HttpStatus.OK);
        }
        return new ResponseEntity<>("user not found", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteUser(@RequestBody final Map<String, Object> map) {
        String event = (String) map.get("event");
        Calendar calendar = calendarJpaRepository.findByEvent(event);

        if (calendar != null) {
            calendarJpaRepository.delete(calendar);
            return new ResponseEntity<>(event + " user deleted", HttpStatus.OK);
        }

        return new ResponseEntity<>("user not found", HttpStatus.BAD_REQUEST);
    }

    /*
     * GET List of Races
     */
    @GetMapping("/")
    public ResponseEntity<List<Calendar>> getCalendar() {
        return new ResponseEntity<>(calendarJpaRepository.findAllByOrderByIdAsc(), HttpStatus.OK);
    }

}

