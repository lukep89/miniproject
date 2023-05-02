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
public class Product implements Serializable {

    private Integer id;
    private String name;
    private String description;
    private Float price;
    private String status;
    private Integer categoryId;


    public static Product toProduct(String json) {
        JsonReader reader = Json.createReader(new StringReader(json));
        JsonObject obj = reader.readObject();

        Product pro = new Product();
        pro.setId(Integer.parseInt(obj.getString("id")));
        pro.setName(obj.getString("name"));
        pro.setDescription(obj.getString("description"));
        pro.setPrice(Float.parseFloat(obj.getString("price")));
        pro.setStatus(obj.getString("status"));
        pro.setCategoryId(Integer.parseInt(obj.getString("categoryId")));

        return pro;
    }

    public JsonObject toJsonObj() {

        return Json.createObjectBuilder()
                .add("id", id)
                .add("name", name)
                .add("description", description)
                .add("price", price)
                .add("status", status)
                .add("categoryId", categoryId)
                .build();
    }

    public static Product create(SqlRowSet rs) {
        Product pro = new Product();
        pro.setId(rs.getInt("id"));
        pro.setName(rs.getString("name"));
        pro.setDescription(rs.getString("description"));
        pro.setPrice(rs.getFloat("price"));
        pro.setStatus(rs.getString("status"));
        pro.setCategoryId(rs.getInt("category_id"));

        return pro;
    }
}
