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
public class User implements Serializable{

    private Integer id;
    private String name;
    private String contactNumber;
    private String email;
    private String password;
    private String status;
    private String role;

    private String resetPasswordToken;

    // For setting date incase if needed
    // @DateTimeFormat(pattern = "yyyy-MM-dd")
    // private LocalDate confirmationDate; // to auto generated when save to db

    
    public static User toUser(String json) {

        JsonReader reader = Json.createReader(new StringReader(json));
        JsonObject obj = reader.readObject();

        User user = new User();
        user.setId(obj.getInt("id"));
        user.setName(obj.getString("name"));
        user.setContactNumber(obj.getString("contactNumber"));
        user.setEmail(obj.getString("email"));
        user.setPassword(obj.getString("password"));
        user.setStatus(obj.getString("status"));
        user.setRole(obj.getString("role"));

        return user;
    }

    public JsonObject toJsonObj() {

        return Json.createObjectBuilder()
                .add("id", id)
                .add("name", name)
                .add("contactNumber", contactNumber)
                .add("email", email)
                .add("password", password)
                .add("status", status)
                .add("role", role)
                .build();
    }

    public static User create(SqlRowSet rs) {
		User user = new User();
		user.setId(rs.getInt("id"));
		user.setName(rs.getString("name"));
		user.setContactNumber(rs.getString("contact_number"));
		user.setEmail(rs.getString("email"));
		user.setPassword(rs.getString("password"));
		user.setStatus(rs.getString("status"));
		user.setRole(rs.getString("role"));

		return user;
	}

    public static User createForResetPassword(SqlRowSet rs) {
        User user = new User();

        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setResetPasswordToken(rs.getString("reset_password_token"));

        return user;
    }
}

  

    

