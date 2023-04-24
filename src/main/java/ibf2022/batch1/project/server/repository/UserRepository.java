package ibf2022.batch1.project.server.repository;

import static ibf2022.batch1.project.server.repository.Queries.SQL_GET_USER_BY_EMAIL;
import static ibf2022.batch1.project.server.repository.Queries.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;

import ibf2022.batch1.project.server.model.User;
import ibf2022.batch1.project.server.model.UserWrapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public User findByEmail(String email) {
        log.info(">>>>> inside findByEmail{} ", email);

        List<User> users = jdbcTemplate.query(SQL_GET_USER_BY_EMAIL,
                BeanPropertyRowMapper.newInstance(User.class), email);

        return users.isEmpty() ? null : users.get(0);

    }

    public int save(User newUser) {
        log.info(">>>>> inside save{} ", newUser);

        Integer saved = 0;

        saved = jdbcTemplate.update(SQL_INSERT_TO_USER_TABLE, new PreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, newUser.getName());
                ps.setString(2, newUser.getContactNumber());
                ps.setString(3, newUser.getEmail());
                ps.setString(4, newUser.getPassword());
                ps.setString(5, newUser.getStatus());
                ps.setString(6, newUser.getRole());
            }
        });

        System.out.println(">>>>> inside findByEmail , isSaved? " + saved);

        return saved;
    }

    public List<UserWrapper> getAllUser() {

        return jdbcTemplate.query(
                SQL_GET_ALL_WHERE_ROLE_USER, BeanPropertyRowMapper.newInstance(UserWrapper.class));

    }
}
