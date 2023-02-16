package com.nighthawk.spring_portfolio.mvc.betting;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bets")
public class BetApiController {

    @Autowired
    private BetJpaRepository betRepository;


    @GetMapping("/")
    public ResponseEntity<List<Bet>> getBets() {
        return new ResponseEntity<>(betRepository.findAllByOrderByIdAsc(), HttpStatus.OK);
    }
}
