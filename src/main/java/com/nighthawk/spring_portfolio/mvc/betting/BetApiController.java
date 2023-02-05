package com.nighthawk.spring_portfolio.mvc.betting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.nighthawk.spring_portfolio.mvc.user.UserJpaRepository;
import com.nighthawk.spring_portfolio.mvc.role.RoleJpaRepository;
import com.nighthawk.spring_portfolio.mvc.drivelog.DriveLog;

import java.util.*;

import org.springframework.transaction.annotation.Transactional;
import java.text.SimpleDateFormat;

@RestController
@RequestMapping("/api/team")
public class BetApiController {

}
