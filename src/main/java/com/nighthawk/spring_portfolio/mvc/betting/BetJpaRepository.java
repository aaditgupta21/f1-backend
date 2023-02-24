package com.nighthawk.spring_portfolio.mvc.betting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nighthawk.spring_portfolio.mvc.race.Race;
import com.nighthawk.spring_portfolio.mvc.user.User;

import java.util.Date;
import java.util.List;

/*
Extends the JpaRepository interface from Spring Data JPA.
-- Java Persistent API (JPA) - Hibernate: map, store, update and retrieve database
-- JpaRepository defines standard CRUD methods
-- Via JPA the developer can retrieve database from relational databases to Java objects and vice versa.
 */

public interface BetJpaRepository extends JpaRepository<Bet, Long> {
    List<Bet> findAllByOrderByIdAsc();

    List<Bet> findAllByRace(Race race);

    List<Bet> findAllById(Long id);

    Bet findByRaceAndUserAndBetActive(Race race, User user, Boolean betActive);

    // Custom JPA query
    @Query(value = "SELECT * FROM Person p WHERE p.name LIKE ?1 or p.email LIKE ?1", nativeQuery = true)
    List<Bet> findByLikeTermNative(String term);
}