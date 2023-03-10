package com.nighthawk.spring_portfolio.mvc.race;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/*
Extends the JpaRepository interface from Spring Data JPA.
-- Java Persistent API (JPA) - Hibernate: map, store, update and retrieve database
-- JpaRepository defines standard CRUD methods
-- Via JPA the developer can retrieve database from relational databases to Java objects and vice versa.
 */

public interface RaceJpaRepository extends JpaRepository<Race, Long> {
    Race findByDate(Date date);

    Race findBySeason(String season);

    Race findByNameIgnoreCaseAndSeason(String name, String season);

    List<Race> findAllByOrderByIdAsc();

    List<Race> findAllByNameIgnoreCaseAndSeason(String name, String season);

    // Custom JPA query
    @Query(value = "SELECT * FROM Person p WHERE p.name LIKE ?1 or p.email LIKE ?1", nativeQuery = true)
    List<Race> findByLikeTermNative(String term);
}



// public interface CalendarJpaRepository extends JpaRepository<Calendar, Long> {
//     List<Calendar> findAllByOrderByIdAsc();

// }

// public interface RaceJpaRepository extends JpaRepository<Race, Long> {
//     Race findByYear(String Year);

//     List<Race> findAllByOrderByIdAsc();

//     // Custom JPA query
//     @Query(value = "SELECT * FROM Person p WHERE p.name LIKE ?1 or p.email LIKE ?1", nativeQuery = true)
//     List<Race> findByLikeTermNative(String term);

//     // Team mercedes = new Team("Mercedes", "London");
// }