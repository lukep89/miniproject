package ibf2022.batch1.project.server.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductWrapper implements Serializable{

    private Integer id;
    private String name;
    private String description;
    private Float price;
    private String status;
    private Integer categoryId;
    private String categoryName;

}
