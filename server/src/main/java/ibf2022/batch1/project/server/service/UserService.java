package ibf2022.batch1.project.server.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
        user.setName(obj.getString("name"));
        user.setContactNumber(obj.getString("contactNumber"));
        user.setEmail(obj.getString("email"));

        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
        user.setPassword(bcrypt.encode(obj.getString("password")));

        user.setStatus("inactive");
        user.setRole("user");

        return user;
    }

    public ResponseEntity<String> login(String payload) {
        // log.info(">>>> inside login {}", payload);

        try {
            JsonObject obj = CafeUtils.jsonStringToJsonObj(payload);

            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            obj.getString("email"), obj.getString("password")));

            log.info(">>>> inside login , auth: {} ", auth);

            if (auth.isAuthenticated()) {

                // if user is authenticated and is status is active
                if (customerUserDetailsService.getUserDetail().getStatus()
                        .equalsIgnoreCase("active")) {

                    // generate token and return in resp
                    ObjectMapper objectMapper = new ObjectMapper();
                    ObjectNode responseBody = objectMapper.createObjectNode();

                    responseBody.put("token", jwtUtil.generateToken(
                            customerUserDetailsService.getUserDetail().getEmail(),
                            customerUserDetailsService.getUserDetail().getRole()));
                    responseBody.put("message", customerUserDetailsService.getUserDetail().getName());

                    return new ResponseEntity<>(responseBody.toString(), HttpStatus.OK);

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

                    emailUtils.sendEmailToAllAdmin(status, email, allAdminEmail);

                    return CafeUtils.getRespEntity(HttpStatus.OK, "Updated user status successfully");
                } else {
                    return CafeUtils.getRespEntity(HttpStatus.OK, "User email doesn't exist");
                }

            } else {
                return CafeUtils.getRespEntity(HttpStatus.UNAUTHORIZED, "Unauthorized access");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getRespEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong!");
    }

    // method to allow user to route relevant user-only page.
    public ResponseEntity<String> checkToken() {

        return CafeUtils.getRespEntity(HttpStatus.OK, "Token valid");
    }

    public ResponseEntity<String> changePassword(String payload) {

        try {
            JsonObject obj = CafeUtils.jsonStringToJsonObj(payload);
            // System.out.println(">>>>> changePassword - payload: " + payload);

            Optional<User> opt = userRepo.findByEmail(jwtFilter.getCurrentUser());
            // log.info(">>>> Inside chaangePassword - jetFiler: {}", jwtFilter.getCurrentUser());

            if (opt.isPresent()) {

                User user = opt.get();

                String dbPwd = user.getPassword();

                BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

                if (bcrypt.matches(obj.getString("oldPassword"), dbPwd)) {

                    user.setPassword(bcrypt.encode(obj.getString("newPassword")));

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

    public ResponseEntity<String> updateResetPasswordToken(String payload) {

        try {
            JsonObject obj = CafeUtils.jsonStringToJsonObj(payload);

            Optional<User> opt = userRepo.findByEmail(obj.getString("email"));

            if (opt.isPresent()) {
                User user = opt.get();
                log.info(">>>> Inside updateUserResetPasswordToken - user: {}", user);

                // generate and set a resetPasswordToken
                String token = UUID.randomUUID().toString().substring(0, 8);
                user.setResetPasswordToken(token);

                // save resetPasswordToken to user db
                userRepo.updateResetPasswordToken(user);

                if (Strings.hasText(obj.getString("email"))) {
                    emailUtils.resetPasswordMail(user.getEmail(), "Reset Password",
                            token);
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

    public ResponseEntity<String> resetPassword(String token, String payload) {

        try {
            JsonObject obj = CafeUtils.jsonStringToJsonObj(payload);

            Optional<User> opt = userRepo.findByResetPasswordToken(token);

            if (opt.isPresent()) {
                User user = opt.get();
                log.info(">>>> Inside resetPassword - user: {}", user);

                BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
                user.setPassword(bcrypt.encode(obj.getString("newPassword")));
                user.setResetPasswordToken(null);

                userRepo.resetUserPassword(user);

                return CafeUtils.getRespEntity(HttpStatus.OK, "Reset password successfully");

            } else {
                return CafeUtils.getRespEntity(HttpStatus.BAD_REQUEST, "Token not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getRespEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong!");
    }

}
