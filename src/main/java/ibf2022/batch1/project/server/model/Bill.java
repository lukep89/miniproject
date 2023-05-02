package ibf2022.batch1.project.server.model;

import java.io.Serializable;
import java.io.StringReader;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Bill implements Serializable {

    private Integer id;
    private String uuid;
    private String name;
    private String email;
    private String contactNumber;
    private String paymentMethod;
    private Float totalAmount;

    private String productDetails;

    private String createdBy;

    public static Bill toBill(String json) {

        JsonReader reader = Json.createReader(new StringReader(json));
        JsonObject obj = reader.readObject();

        Bill bill = new Bill();
        bill.setId(Integer.parseInt(obj.getString("id")));
        bill.setUuid(obj.getString("uuid"));
        bill.setName(obj.getString("name"));
        bill.setEmail(obj.getString("email"));
        bill.setContactNumber(obj.getString("contactNumber"));
        bill.setPaymentMethod(obj.getString("paymentMethod"));
        bill.setTotalAmount(Float.parseFloat(obj.getString("totalAmount")));
        bill.setProductDetails(obj.getString("productDetails"));
        bill.setCreatedBy(obj.getString("createdBy"));

        return bill;
    }

    public static Bill create(SqlRowSet rs) {
        Bill bill = new Bill();
        bill.setId(rs.getInt("id"));
        bill.setUuid(rs.getString("uuid"));
        bill.setName(rs.getString("name"));
        bill.setEmail(rs.getString("email"));
        bill.setContactNumber(rs.getString("contact_number"));
        bill.setPaymentMethod(rs.getString("payment_method"));
        bill.setTotalAmount(rs.getFloat("total_amount"));
        bill.setProductDetails(rs.getString("product_details"));
        bill.setCreatedBy(rs.getString("created_by"));

        return bill;
    }
}
