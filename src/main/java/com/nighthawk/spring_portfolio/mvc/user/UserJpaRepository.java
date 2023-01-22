package com.nighthawk.spring_portfolio.mvc.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;
import java.util.List;

/*
 * Extends the JpaRepository interface from Spring Data JPA.
 * -- Java Persistent API (JPA) - Hibernate: map, store, update and retrieve
 * database
 * -- JpaRepository defines standard CRUD methods
 * -- Via JPA the developer can retrieve database from relational databases to
 * Java objects and vice versa.
 */

public interface UserJpaRepository extends JpaRepository<User, Long> {
    User findByName(String name);

    List<User> findAllByOrderByNameAsc();
}