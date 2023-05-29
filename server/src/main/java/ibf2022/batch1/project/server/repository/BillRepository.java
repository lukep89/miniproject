package ibf2022.batch1.project.server.repository;

import static ibf2022.batch1.project.server.repository.Queries.SQL_COUNT_BILL;
import static ibf2022.batch1.project.server.repository.Queries.SQL_DELETE_BILL_BY_ID;
import static ibf2022.batch1.project.server.repository.Queries.SQL_GET_ALL_BILL;
import static ibf2022.batch1.project.server.repository.Queries.SQL_GET_ALL_BILL_BY_USERNAME;
import static ibf2022.batch1.project.server.repository.Queries.SQL_GET_BILL_BY_ID;
import static ibf2022.batch1.project.server.repository.Queries.SQL_INSERT_TO_BILL_TABLE;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.DeleteResult;

import ibf2022.batch1.project.server.model.Bill;
import ibf2022.batch1.project.server.utils.MongoUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class BillRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    public int save(Bill bill) {
        log.info("Inside save - bill: {} \n", bill);

        Integer saved = 0;

        saved = jdbcTemplate.update(SQL_INSERT_TO_BILL_TABLE, new PreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, bill.getUuid());
                ps.setString(2, bill.getName());
                ps.setString(3, bill.getEmail());
                ps.setString(4, bill.getContactNumber());
                ps.setString(5, bill.getPaymentMethod());
                ps.setFloat(6, bill.getTotalAmount());
                ps.setString(7, bill.getProductDetails());
                ps.setString(8, bill.getCreatedBy());

            }

        });
        log.info(">>>>> inside save - isBillSaved? {} ", saved);
        return saved;

    }

    public List<Bill> getAllBill() {
        List<Bill> bills = jdbcTemplate.query(
                SQL_GET_ALL_BILL, BeanPropertyRowMapper.newInstance(Bill.class));

        log.info(">>>> Inside getAllProduct  size: {}", bills.size());

        return bills;
    }

    public List<Bill> getBillByUsername(String currentUser) {
        List<Bill> bills = jdbcTemplate.query(
                SQL_GET_ALL_BILL_BY_USERNAME, BeanPropertyRowMapper.newInstance(Bill.class), currentUser);

        log.info(">>>> Inside getAllProduct  size: {}", bills.size());

        return bills;
    }

    public Optional<Bill> getBillById(Integer id) {

        SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_GET_BILL_BY_ID, id);

        if (!rs.next())
            return Optional.empty();

        return Optional.of(Bill.create(rs));
    }

    public int deleteBillById(Integer id) {
        Integer deleted = 0;

        deleted = jdbcTemplate.update(SQL_DELETE_BILL_BY_ID, id);
        log.info(">>>>> inside deleteBillById - isDeleted? {} ", deleted);

        return deleted;
    }

    public int count() {
        return jdbcTemplate.queryForObject(SQL_COUNT_BILL, Integer.class);
    }

    // MONGO PART
    public static String COLLECTION_PDF_BILL = "pdf_bill";

    public int savePdf(byte[] pdfBytes, String fileName) {
        Integer saved = 0;

        Document doc = MongoUtils.toDocument(fileName, pdfBytes);

        if (mongoTemplate.insert(doc, COLLECTION_PDF_BILL) != null) {
            return saved = 1;
        }

        log.info(">>>>> inside savePdf - isToMongo? {} ", saved);
        return saved;
    }

    public Optional<byte[]> getPdfDocument(String fileName) {
        log.info(">>>>> Inside BillRepo - getPdfDocument - fileName {} ", fileName);

        Query billIdQuery = new Query()
                .addCriteria(Criteria.where("bill").is(fileName));

        List<Document> docs = mongoTemplate.find(billIdQuery, Document.class, COLLECTION_PDF_BILL);

        if (docs.size() <= 0) {
            return Optional.empty();
        }
        byte[] result = MongoUtils.getPdfByte(docs.get(0));
        return Optional.of(result);

    }

    public int deletePdf(String fileName) {
        Query query = Query.query(Criteria.where("bill").is(fileName));
        DeleteResult result = mongoTemplate.remove(query, COLLECTION_PDF_BILL);

        if (result.getDeletedCount() > 0) {
            log.info(">>>> Record deleted successfully.");
            return 1;
        } else {
            log.info(">>>> Record not found.");
            return 0;
        }
    }

}
