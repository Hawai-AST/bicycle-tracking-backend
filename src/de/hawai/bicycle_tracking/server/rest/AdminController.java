package de.hawai.bicycle_tracking.server.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AdminController {

    @RequestMapping(value = "/v1/admin", method = RequestMethod.GET)
    public ResponseEntity<String> getAdmin() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
