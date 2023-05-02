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
public class Category implements Serializable{

    private Integer id;
    private String name;

    public static Category toCategory(String json) {

        JsonReader reader = Json.createReader(new StringReader(json));
        JsonObject obj = reader.readObject();

        Category cat = new Category();
        cat.setId(Integer.parseInt(obj.getString("id")));
        cat.setName(obj.getString("name"));

        return cat;
    }

    public JsonObject toJsonObj() {

        return Json.createObjectBuilder()
                .add("id", id)
                .add("name", name)
                .build();
    }

    public static Category create(SqlRowSet rs) {
        Category cat = new Category();
        cat.setId(rs.getInt("id"));
        cat.setName(rs.getString("name"));

        return cat;
    }

}
