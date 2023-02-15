package com.nighthawk.spring_portfolio.mvc.calendar;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import java.util.GregorianCalendar;

import org.hibernate.annotations.TypeDef;
import org.springframework.format.annotation.DateTimeFormat;

import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@TypeDef(name = "json", typeClass = JsonType.class)
public class Calendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    private String event;

    private String note;

    @DateTimeFormat(pattern = "MM-dd-yyyy")
    private Date dateOfEvent;

    public String toString() {
        return ("{ \"event\": " + this.event + ", " + "\"Note\": " + this.note + ", " + "\"date\": " + this.dateOfEvent + "}");
    }


    public Calendar(String event, String note, Date dateOfEvent) {
        this.event = event;
        this.note = note;
        this.dateOfEvent = dateOfEvent;
    }

    public static void main(String[] args) {
        Calendar cal = new Calendar();
        Date date = new GregorianCalendar(2006, 4, 2).getTime();
        cal.setDateOfEvent(date);
        cal.setEvent("test");
        cal.setNote("test");
        System.out.println(cal.toString());
    }

}
