package com.nighthawk.spring_portfolio.mvc.race;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RaceJpaRepository extends JpaRepository<Race, Long> {
    Race findByName(String name);
}