package com.nighthawk.spring_portfolio.mvc.calendar;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/*
Extends the JpaRepository interface from Spring Data JPA.
-- Java Persistent API (JPA) - Hibernate: map, store, update and retrieve database
-- JpaRepository defines standard CRUD methods
-- Via JPA the developer can retrieve database from relational databases to Java objects and vice versa.
 */

public interface CalendarJpaRepository extends JpaRepository<Calendar, Long> {
    List<Calendar> findAllByOrderByIdAsc();

    Calendar findByEvent(String event);
}