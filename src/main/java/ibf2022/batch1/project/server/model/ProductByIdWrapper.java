package ibf2022.batch1.project.server.model;

import java.io.Serializable;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductByIdWrapper implements Serializable{

    private Integer id;
    private String name;
    private String description;
    private Float price;

    public static ProductByIdWrapper create(SqlRowSet rs) {
        ProductByIdWrapper pro = new ProductByIdWrapper();
        pro.setId(rs.getInt("id"));
        pro.setName(rs.getString("name"));
        pro.setDescription(rs.getString("description"));
        pro.setPrice(rs.getFloat("price"));

        return pro;
    }

}
