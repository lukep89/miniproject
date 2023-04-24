package ibf2022.batch1.project.server.JWT;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ibf2022.batch1.project.server.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    private ibf2022.batch1.project.server.model.User userDetail;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info(">>>> Inside loadUserByUsername{} ", username);

        userDetail = userRepo.findByEmail(username);
        log.info(">>>> Inside loadUserByUsername, userDetail: {} ", userDetail);

        if (userDetail != null) {

            return new org.springframework.security.core.userdetails.User(
                    userDetail.getEmail(), userDetail.getPassword(), new ArrayList<>());

        } else {
            throw new UsernameNotFoundException("User not found.");
        }
    }

    public ibf2022.batch1.project.server.model.User getUserDetail() {
        log.info("Inside getUserDetail: {}", userDetail);

        // ibf2022.batch1.project.server.model.User user = userDetail;
        // user.setPassword(null);
        // return user;

        return userDetail;
    }

}
