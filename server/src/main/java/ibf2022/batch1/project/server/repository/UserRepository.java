package ibf2022.batch1.project.server.repository;

import static ibf2022.batch1.project.server.repository.Queries.SQL_GET_ALL_ADMIN;
import static ibf2022.batch1.project.server.repository.Queries.SQL_GET_ALL_WHERE_ROLE_USER;
import static ibf2022.batch1.project.server.repository.Queries.SQL_GET_USER_BY_EMAIL;
import static ibf2022.batch1.project.server.repository.Queries.SQL_GET_USER_BY_ID;
import static ibf2022.batch1.project.server.repository.Queries.SQL_GET_USER_BY_RESET_PASSWORD_TOKEN;
import static ibf2022.batch1.project.server.repository.Queries.SQL_INSERT_TO_USER_TABLE;
import static ibf2022.batch1.project.server.repository.Queries.SQL_RESET_USER_PASSWORD;
import static ibf2022.batch1.project.server.repository.Queries.SQL_UPDATE_USER_PASSWORD;
import static ibf2022.batch1.project.server.repository.Queries.SQL_UPDATE_USER_RESET_PASSWORD_TOKEN_BY_EMAIL;
import static ibf2022.batch1.project.server.repository.Queries.SQL_UPDATE_USER_STATUS_BY_EMAIL;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import ibf2022.batch1.project.server.model.User;
import ibf2022.batch1.project.server.model.UserWrapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Optional<User> findByEmail(String email) {

        SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_GET_USER_BY_EMAIL, email);

        if (!rs.next())
            return Optional.empty();

        return Optional.of(User.create(rs));
    }

    public int saveUser(User user) {
        log.info(">>>>> Inside saveUser: {} ", user);

        Integer saved = 0;

        System.out.println(">>>> insert user");
        saved = jdbcTemplate.update(SQL_INSERT_TO_USER_TABLE, new PreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, user.getName());
                ps.setString(2, user.getContactNumber());
                ps.setString(3, user.getEmail());
                ps.setString(4, user.getPassword());
                ps.setString(5, user.getStatus());
                ps.setString(6, user.getRole());
            }
        });

        log.info(">>>>> inside save - isSaved? {} ", saved);

        return saved;
    }

    public List<UserWrapper> getAllUserByRole() {

        List<UserWrapper> users = jdbcTemplate.query(
                SQL_GET_ALL_WHERE_ROLE_USER, BeanPropertyRowMapper.newInstance(UserWrapper.class));

        log.info(">>>> Inside userRepo  - getAllUser size: {}", users.size());

        return users;

    }

    public int updateUserStatus(String status, String email) {
        log.info(">>>>> inside updateUserStatus- status,email : {} : {} ", status, email);

        Integer updated = 0;

        updated = jdbcTemplate.update(SQL_UPDATE_USER_STATUS_BY_EMAIL, new PreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, status);
                ps.setString(2, email);
            }

        });
        log.info(">>>>> inside updateUserStatus - isUpdated? {} ", updated);

        return updated;
    }

    public Optional<User> findById(Integer id) {
        log.info(">>>>> inside findById: {} ", id);

        SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_GET_USER_BY_ID, id);

        if (!rs.next())
            return Optional.empty();

        return Optional.of(User.create(rs));

    }
    
    public List<UserWrapper> getAllAdmin() {

        List<UserWrapper> admins = jdbcTemplate.query(
                SQL_GET_ALL_ADMIN, BeanPropertyRowMapper.newInstance(UserWrapper.class));

        log.info(">>>> Inside userRepo  - getAllUser size: {}", admins.size());

        return admins;

    }

    public int updateUserPassword(User user) {
        log.info(">>>>> Inside updateUserPassword: {} ", user);

        Integer updated = 0;

        updated = jdbcTemplate.update(SQL_UPDATE_USER_PASSWORD, new PreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, user.getPassword());
                ps.setString(2, user.getEmail());

            }
        });

        log.info(">>>>> inside save - isUpdated? {} ", updated);

        return updated;
    }

    

    public Optional<User> findByResetPasswordToken(String token) {
        log.info(">>>>> Inside findByResetPasswordToken: {} ", token);

        SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_GET_USER_BY_RESET_PASSWORD_TOKEN, token);

        if (!rs.next())
            return Optional.empty();

        return Optional.of(User.createForResetPassword(rs));
    }

    public int updateResetPasswordToken(User user) {
        log.info(">>>>> inside updateUserResetPasswordToken - user: {} ", user);
        // log.info(">>>>> inside updateUserResetPasswordToken - token: {} ", token);

        Integer updated = 0;

        updated = jdbcTemplate.update(SQL_UPDATE_USER_RESET_PASSWORD_TOKEN_BY_EMAIL, new PreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, user.getResetPasswordToken());
                ps.setString(2, user.getEmail());
          
            }
        });
        log.info(">>>>> inside updateUserResetPasswordToken - isUpdated? {} ", updated);

        return updated;
    }

    public int resetUserPassword(User user) {

        log.info(">>>>> Inside resetUserPassword: {} ", user);

        Integer updated = 0;

        updated = jdbcTemplate.update(SQL_RESET_USER_PASSWORD, new PreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, user.getPassword());
                ps.setString(2, user.getResetPasswordToken());
                ps.setString(3, user.getEmail());
            }
        });

        log.info(">>>>> inside resetUserPassword - isUpdated? {} ", updated);

        return updated;
    }
}
