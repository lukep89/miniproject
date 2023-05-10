package ibf2022.batch1.project.server.repository;

import static ibf2022.batch1.project.server.repository.Queries.SQL_DELETE_PRODUCT_BY_ID;
import static ibf2022.batch1.project.server.repository.Queries.SQL_GET_ACTIVE_PRODUCT_BY_CATEGORY_ID;
import static ibf2022.batch1.project.server.repository.Queries.SQL_GET_ALL_PRODUCT;
import static ibf2022.batch1.project.server.repository.Queries.SQL_GET_PRODUCT_BY_ID;
import static ibf2022.batch1.project.server.repository.Queries.SQL_INSERT_TO_PRODUCT_TABLE;
import static ibf2022.batch1.project.server.repository.Queries.SQL_UPDATE_PRODUCT_BY_ID;
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
import org.springframework.transaction.annotation.Transactional;

import ibf2022.batch1.project.server.model.Product;
import ibf2022.batch1.project.server.model.ProductByCategoryWrapper;
import ibf2022.batch1.project.server.model.ProductByIdWrapper;
import ibf2022.batch1.project.server.model.ProductWrapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class ProductRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int saveNewProduct(Product product) {
        log.info(">>>>> Inside saveNewProduct: {} ", product);

        Integer saved = 0;

        saved = jdbcTemplate.update(SQL_INSERT_TO_PRODUCT_TABLE, new PreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, product.getName());
                ps.setString(2, product.getDescription());
                ps.setFloat(3, product.getPrice());
                ps.setString(4, product.getStatus());
                ps.setInt(5, product.getCategoryId());
            }

        });
        log.info(">>>>> inside save - isProductSaved? {} ", saved);
        return saved;

    }

    public List<ProductWrapper> getAllProduct() {

        List<ProductWrapper> products = jdbcTemplate.query(
                SQL_GET_ALL_PRODUCT, BeanPropertyRowMapper.newInstance(ProductWrapper.class));

        log.info(">>>> Inside getAllProduct  size: {}", products.size());

        return products;
    }

    public Optional<Product> findById(int id) {

        SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_GET_PRODUCT_BY_ID, id);

        if (!rs.next())
            return Optional.empty();

        return Optional.of(Product.create(rs));
    }

    public int updateProduct(Product pro) {
        // log.info(">>>>> Inside productRepo - updateProduct: {} ", pro);

        Integer updated = 0;

        updated = jdbcTemplate.update(SQL_UPDATE_PRODUCT_BY_ID, new PreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, pro.getName());
                ps.setString(2, pro.getDescription());
                ps.setFloat(3, pro.getPrice());
                ps.setString(4, pro.getStatus());
                ps.setInt(5, pro.getCategoryId());
                ps.setInt(6, pro.getId());
            }
        });
        log.info(">>>>> inside update - isUpdated? {} ", updated);

        return updated;
    }

    public int deleteProductById(Integer id) {
        Integer deleted = 0;

        deleted = jdbcTemplate.update(SQL_DELETE_PRODUCT_BY_ID, id);
        log.info(">>>>> inside deleteProductById - isDeleted? {} ", deleted);

        return deleted;
    }

    @Transactional
    public int updateProductStatus(String status, Integer id) {
        Integer updated = 0;

        updated = jdbcTemplate.update(SQL_UPDATE_PRODUCT_STATUS_BY_ID, new PreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, status);
                ps.setInt(2, id);
            }
        });
        log.info(">>>>> inside updateStatus - isUpdated? {} ", updated);

        return updated;
    }

    public List<ProductByCategoryWrapper> getProductByCategory(Integer id) {

        List<ProductByCategoryWrapper> products = jdbcTemplate.query(
                SQL_GET_ACTIVE_PRODUCT_BY_CATEGORY_ID, BeanPropertyRowMapper.newInstance(ProductByCategoryWrapper.class), id);

        log.info(">>>> Inside getProductByCategory  size: {}", products.size());

        return products;
    }

    public Optional<ProductByIdWrapper> getProductById(Integer id) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_GET_PRODUCT_BY_ID, id);

        if (!rs.next())
            return Optional.empty();

        return Optional.of(ProductByIdWrapper.create(rs));
    }

    public int count() {
        return jdbcTemplate.queryForObject(SQL_COUNT_PRODUCT, Integer.class);
    }

}
