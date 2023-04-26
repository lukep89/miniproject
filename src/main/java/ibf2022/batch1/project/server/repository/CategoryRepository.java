package ibf2022.batch1.project.server.repository;

import static ibf2022.batch1.project.server.repository.Queries.SQL_GET_ALL_CATEGORY;
import static ibf2022.batch1.project.server.repository.Queries.SQL_GET_CATEGORY_BY_ID;
import static ibf2022.batch1.project.server.repository.Queries.*;

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

import ibf2022.batch1.project.server.model.Category;
import jakarta.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class CategoryRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Category> getCategories() {

        List<Category> categories = jdbcTemplate.query(
                SQL_GET_ALL_CATEGORY, BeanPropertyRowMapper.newInstance(Category.class));

        log.info(">>>> Inside getCategories  size: {}", categories.size());

        return categories;

    }

    public int saveCategory(String name) {
        log.info(">>>>> Inside saveCategory: {} ", name);

        Integer saved = 0;

        saved = jdbcTemplate.update(SQL_INSERT_TO_CATEGORY_TABLE, new PreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, name);
            }

        });

        log.info(">>>>> inside save - isCategorySaved? {} ", saved);
        return saved;
    }

    public Optional<Category> findById(int id) {
        // log.info(">>>>> Inside category - findById: {} ", id);

        SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_GET_CATEGORY_BY_ID, id);

        if (!rs.next())
            return Optional.empty();

        return Optional.of(Category.create(rs));
    }

    public int updateCategory(Category cat) {
        // log.info(">>>>> Inside categoryRepo - updateCategory: {} ", cat);

        Integer updated = 0;

        updated = jdbcTemplate.update(SQL_UPDATE_CATEGORY, new PreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, cat.getName());
                ps.setInt(2, cat.getId());
            }
        });

        log.info(">>>>> inside update - isUpdated? {} ", updated);

        return updated;
    }

}
