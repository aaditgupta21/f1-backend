package com.nighthawk.spring_portfolio.mvc.drivelog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // annotation to create a RESTful web services
@RequestMapping("/api/driveLog") // prefix of API
public class DriveLogApiController {

    @Autowired
    private DriveLogJpaRepository DriveLogJpaRepository;

    // GET schedule data
    @PostMapping(value = "/newDriveLog", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> newTeam(@RequestParam("raceName") String raceName,
            @RequestParam("date") String dateString,
            @RequestParam("time") double time,
            @RequestParam("miles") double miles) {
                Date date;
                try {
                    date = new SimpleDateFormat("MM-dd-yyyy").parse(dateString);
                } catch (Exception e) {
                    return new ResponseEntity<>(dateString + " error; try MM-dd-yyyy",
                            HttpStatus.BAD_REQUEST);
                }
        DriveLog driveLog = new DriveLog(date, miles, time, raceName);
        DriveLogJpaRepository.save(driveLog);
        return new ResponseEntity<>(date + " listed successfully!", HttpStatus.CREATED);
    }

    /*
     * GET List of Races
     */
    @GetMapping("/")
    public ResponseEntity<List<DriveLog>> getDriveLogs() {
        return new ResponseEntity<>(DriveLogJpaRepository.findAllByOrderByIdAsc(), HttpStatus.OK);
    }
}

