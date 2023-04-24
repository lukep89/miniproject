package ibf2022.batch1.project.server.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ibf2022.batch1.project.server.model.UserWrapper;
import ibf2022.batch1.project.server.service.UserService;

@RestController
@RequestMapping(path = "/api")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userSvc;

    @PostMapping(path = "/user/signup")
    public ResponseEntity<String> signUp(@RequestBody String payload) {
        // System.out.println(">>>> inside signup{} payload: " + payload);

        try {
            return userSvc.signUp(payload);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(">>>> Something went wrong!");
    }

    @PostMapping(path = "/user/login")
    public ResponseEntity<String> login(@RequestBody String payload) {
        // System.out.println(">>>> inside login{} payload: " + payload);

        try {
            return userSvc.login(payload);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(">>>> Something went wrong!");

    }

    @GetMapping(path = "/user/get")
    public ResponseEntity<List<UserWrapper>> listOfUsers() {

        try {

            // return userSvc.getAllUSer();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<List<UserWrapper>>(
                new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);

    }
}