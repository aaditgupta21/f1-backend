package com.nighthawk.spring_portfolio.mvc.drivelog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Positive;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DriveLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DateTimeFormat(pattern = "MM-dd-yyyy")
    private Date date;

    @Positive
    private double miles;

    @Positive
    private double time;

    private String raceName;

    public DriveLog(Date date, double miles, double time, String raceName) {
        this.date = date;
        this.miles = miles;
        this.time = time;
        this.raceName = raceName;
    }

    public static void main(String[] args) {
        DriveLog driveLog = new DriveLog();
        Date date = new GregorianCalendar(2006, 4, 2).getTime();
        driveLog.setDate(date);
        driveLog.setMiles(12312);
        driveLog.setTime(999);
        driveLog.setRaceName("Grandy");
    }
}
