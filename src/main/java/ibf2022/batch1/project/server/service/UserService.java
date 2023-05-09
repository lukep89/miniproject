package ibf2022.batch1.project.server.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ibf2022.batch1.project.server.JWT.CustomerUserDetailsService;
import ibf2022.batch1.project.server.JWT.JwtFilter;
import ibf2022.batch1.project.server.JWT.JwtUtil;
import ibf2022.batch1.project.server.model.User;
import ibf2022.batch1.project.server.model.UserWrapper;
import ibf2022.batch1.project.server.repository.UserRepository;
import ibf2022.batch1.project.server.utils.CafeUtils;
import ibf2022.batch1.project.server.utils.EmailUtils;
import io.jsonwebtoken.lang.Strings;
import jakarta.json.JsonObject;
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

    @Autowired
    EmailUtils emailUtils;

    public ResponseEntity<String> signup(String payload) {
        // log.info(">>>> inside signUp{} ", payload);

        try {
            if (validateSignUpPayload(payload)) {
                JsonObject obj = CafeUtils.jsonStringToJsonObj(payload);

                // check if user email exist in db. not regist user
                String email = obj.getString("email");
                // System.out.println(">>>> email: " + email);

                Optional<User> opt = userRepo.findByEmail(email);

                if (opt.isPresent()) {
                    return CafeUtils.getRespEntity(HttpStatus.BAD_REQUEST, "Email already exist");
                }

                User newUser = toNewUser(payload);

                userRepo.saveUser(newUser);

                return CafeUtils.getRespEntity(HttpStatus.OK, "Successfully registered");

            } else {
                return CafeUtils.getRespEntity(HttpStatus.BAD_REQUEST, "Invalid data. Missing fields");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getRespEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong!");

    }

    private boolean validateSignUpPayload(String payload) {

        JsonObject obj = CafeUtils.jsonStringToJsonObj(payload);

        // check if the Json has important keys for signup
        if (obj.containsKey("name") && obj.containsKey("contactNumber")
                && obj.containsKey("email") && obj.containsKey("password")) {
            return true;
        } else {
            return false;
        }

    }

    private User toNewUser(String json) {

        JsonObject obj = CafeUtils.jsonStringToJsonObj(json);

        User user = new User();
        // TODO: use UUID for id? if yes need to update your db datatype

        user.setName(obj.getString("name"));
        user.setContactNumber(obj.getString("contactNumber"));
        user.setEmail(obj.getString("email"));
        user.setPassword(obj.getString("password"));
        user.setStatus("inactive");
        user.setRole("user");

        return user;
    }

    public ResponseEntity<String> login(String payload) {
        log.info(">>>> inside login {}", payload);

        try {
            JsonObject obj = CafeUtils.jsonStringToJsonObj(payload);
            // from the payload get the email and password for authentication
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            obj.getString("email"), obj.getString("password")));

            log.info(">>>> inside login , auth: {} ", auth);

            if (auth.isAuthenticated()) {
                // if user is authenticated and is status is active
                if (customerUserDetailsService.getUserDetail().getStatus()
                        .equalsIgnoreCase("active")) {

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

    public ResponseEntity<List<UserWrapper>> getAllUserByRole() {
        log.info(">>>> inside getAllUser {} ");

        try {
            if (jwtFilter.isAdmin()) {
                return new ResponseEntity<List<UserWrapper>>(userRepo.getAllUserByRole(), HttpStatus.OK);

            } else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<List<UserWrapper>>(
                new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Transactional
    public ResponseEntity<String> updateUserStatus(String payload) {

        try {
            if (jwtFilter.isAdmin()) {
                JsonObject obj = CafeUtils.jsonStringToJsonObj(payload);

                // // IF USE FIND BY EMAIL
                Optional<User> opt = userRepo.findByEmail(obj.getString("email"));
                if (opt.isPresent()) {
                    String status = obj.getString("status");
                    String email = obj.getString("email");
                    userRepo.updateUserStatus(status, email);

                    List<String> allAdminEmail = userRepo.getAllAdmin().stream()
                            .map(u -> u.getEmail())
                            .collect(Collectors.toList());

                    log.info(">>>> allAdminEmail: {}", allAdminEmail.size());

                    sendEmailToAllAdmin(status, email, allAdminEmail);

                    return CafeUtils.getRespEntity(HttpStatus.OK, "Updated user status successfully");
                } else {
                    return CafeUtils.getRespEntity(HttpStatus.OK, "User email doesn't exist");
                }

                // // TODO: DECIDE TO USE UPDATEUSERSTATUSBYEMAIL OR BYID
                // // IF USE FIND BY ID
                // Optional<User> opt =
                // userRepo.findById(Integer.parseInt(obj.getString("id")));
                // if (opt.isPresent()) {
                // String status = obj.getString("status");
                // Integer id = Integer.parseInt(obj.getString("id"));
                // userRepo.updateUserStatus(status, id);
                // List<String> allAdminEmail = userRepo.getAllAdmin().stream()
                // .map(u -> u.getEmail())
                // .collect(Collectors.toList());

                // System.out.println(">>>> allAdminEmail: " + allAdminEmail.size());

                // String email = obj.getString("email");

                // sendEmailToAllAdmin(status, email, allAdminEmail);

                // return ResponseEntity
                // .status(HttpStatus.OK)
                // .body(">>>> Updated user status successfully");
                // } else {
                // return ResponseEntity
                // .status(HttpStatus.OK)
                // .body(">>>> User email doesn't exist");
                // }

            } else {
                return CafeUtils.getRespEntity(HttpStatus.UNAUTHORIZED, "Unauthorized access");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getRespEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong!");
    }

    private void sendEmailToAllAdmin(String status, String userEmail, List<String> allAdminEmail) {
        allAdminEmail.remove(jwtFilter.getCurrentUser());

        if (status != null && status.equalsIgnoreCase("active")) {
            emailUtils.sendSimpleMessgae(
                    jwtFilter.getCurrentUser(),
                    "Account Approved",
                    "USER: - " + userEmail + "\n has been approved by \n" + "ADMIN: - " + jwtFilter.getCurrentUser(),
                    allAdminEmail);
        } else {
            emailUtils.sendSimpleMessgae(
                    jwtFilter.getCurrentUser(),
                    "Account Disabled  ",
                    "USER: - " + userEmail + "\n has been disabled by \n" + "ADMIN: - " + jwtFilter.getCurrentUser(),
                    allAdminEmail);
        }
    }

    // method to allow user to route relevant user-only page.
    public ResponseEntity<String> checkToken() {

        return CafeUtils.getRespEntity(HttpStatus.OK, "Token valid");
    }

    public ResponseEntity<String> changePassword(String payload) {

        try {
            JsonObject obj = CafeUtils.jsonStringToJsonObj(payload);

            Optional<User> opt = userRepo.findByEmail(jwtFilter.getCurrentUser());
            log.info(">>>> Inside chaangePassword - jetFiler: {}", jwtFilter.getCurrentUser());

            if (opt.isPresent()) {

                User user = opt.get();

                if (user.getPassword().equals(obj.getString("oldPassword"))) {

                    user.setPassword(obj.getString("newPassword"));
                    userRepo.updateUserPassword(user);

                    return CafeUtils.getRespEntity(HttpStatus.OK, "Updated password successfully");

                } else {
                    return CafeUtils.getRespEntity(HttpStatus.BAD_REQUEST, "Incorrect old password");
                }
            } else {
                return CafeUtils.getRespEntity(HttpStatus.BAD_REQUEST, "User not found");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getRespEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong!");
    }

    public ResponseEntity<String> forgotPassword(String payload) {

        try {
            JsonObject obj = CafeUtils.jsonStringToJsonObj(payload);

            Optional<User> opt = userRepo.findByEmail(obj.getString("email"));
            if (opt.isPresent()) {
                User user = opt.get();
                log.info(">>>> Inside forgotPassword - user: {}", user);

                if (Strings.hasText(obj.getString("email"))) {
                    emailUtils.forgotPasswordMail(user.getEmail(), "Credentials by Cafe Management System",
                            user.getPassword());
                }

                return CafeUtils.getRespEntity(HttpStatus.OK, "Check your email for credentials");

            } else {
                return CafeUtils.getRespEntity(HttpStatus.OK, "Check your email for credentials...");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getRespEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong!");
    }

}
