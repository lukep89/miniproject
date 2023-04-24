package ibf2022.batch1.project.server.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import ibf2022.batch1.project.server.JWT.CustomerUserDetailsService;
import ibf2022.batch1.project.server.JWT.JwtFilter;
import ibf2022.batch1.project.server.JWT.JwtUtil;
import ibf2022.batch1.project.server.model.User;
import ibf2022.batch1.project.server.model.UserWrapper;
import ibf2022.batch1.project.server.repository.UserRepository;
import ibf2022.batch1.project.server.utils.CafeUtils;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

    @Autowired
    UserRepository userRepo;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    JwtFilter jwtFilter;

    public ResponseEntity<String> signUp(String payload) {
        // log.info(">>>> inside signUp{} ", payload);

        try {
            if (validateSignUp(payload)) {
                JsonObject obj = CafeUtils.jsonStringToJsonObj(payload);

                // check if user email exist in db. not regist user
                String email = obj.getString("email");
                // System.out.println(">>>> email: " + email);

                User existingUser = userRepo.findByEmail(email);

                if (existingUser != null) {
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body(">>>> Email already exist");
                }

                User newUser = toNewUser(payload);

                userRepo.save(newUser);

                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(">>>> Successfully Registered");

            } else {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(">>>> Invalid data. Missing fields.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(">>>> Something went wrong");
        }

    }

    private boolean validateSignUp(String payload) {

        JsonObject obj = CafeUtils.jsonStringToJsonObj(payload);
        // System.out.println(">>>> inside validateSignUp()" + obj);

        // check if the Json has important keys for signup
        if (obj.containsKey("name") && obj.containsKey("contactNumber")
                && obj.containsKey("email") && obj.containsKey("password")) {
            return true;
        } else {
            return false;
        }

    }

    private User toNewUser(String json) {

        JsonReader reader = Json.createReader(new StringReader(json));
        JsonObject obj = reader.readObject();

        User user = new User();
        // TDOO: use UUID for id? if yes need to update your db datatype
        user.setName(obj.getString("name"));
        user.setContactNumber(obj.getString("contactNumber"));
        user.setEmail(obj.getString("email"));
        user.setPassword(obj.getString("password"));
        user.setStatus("false");
        user.setRole("user");

        return user;
    }

    public ResponseEntity<String> login(String payload) {
        log.info(">>>> inside login {}", payload);

        JsonObject obj = CafeUtils.jsonStringToJsonObj(payload);

        try {
            // from the payload get the email and password for authentication
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            obj.getString("email"), obj.getString("password")));

            log.info(">>>> inside login , auth: {} ", auth);

            if (auth.isAuthenticated()) {
                // if user is authenticated and is status is true
                // TODO: change status from true to active
                if (customerUserDetailsService.getUserDetail().getStatus()
                        .equalsIgnoreCase("true")) {

                    // generate token and return in resp
                    return new ResponseEntity<String>("{\"token\":\"" +
                            jwtUtil.generateToken(
                                    customerUserDetailsService.getUserDetail().getEmail(),
                                    customerUserDetailsService.getUserDetail().getRole())
                            + "\"}",
                            HttpStatus.OK);

                } else {
                    return new ResponseEntity<String>("{\"message\":\"" +
                            "Wait for admin approval." + "\"}",
                            HttpStatus.BAD_REQUEST);
                }
            }
        } catch (Exception e) {
            log.error("{}", e);
        }

        return new ResponseEntity<String>("{\"message\":\"" +
                "Bad Credentials." + "\"}",
                HttpStatus.BAD_REQUEST);

    }

    ResponseEntity<List<UserWrapper>> getAllUser() {

        try {
            if(jwtFilter.isAdmin()){

            }else{
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }
            // List<UserWrapper> users = userRepo.getAllUser();

            return null;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<List<UserWrapper>>(
                new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
