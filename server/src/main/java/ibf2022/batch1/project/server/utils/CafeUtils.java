package ibf2022.batch1.project.server.utils;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;



public class CafeUtils {

    // common return for error in response
    public static ResponseEntity<String> getRespEntity(HttpStatus httpstatus, String respMessage) {

        return ResponseEntity
                .status(httpstatus)
                .body("{\"message\":\"" + respMessage + "\"}");
    }

    public static JsonObject jsonStringToJsonObj(String json) {

        JsonReader reader = Json.createReader(new StringReader(json));
        return reader.readObject();
    }

    public static String getUuid() {
        Date date = new Date();
        long time = date.getTime();

        return "BILL-" + time;
    }

    public static String getDate(){
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = dateFormat.format(currentDate);

        return formattedDate;
    }

    
    public static JsonArray getJsonArrayFromString(String data) {
        
        JsonReader reader = Json.createReader(new StringReader(data));
        JsonArray jsonArray = reader.readArray();
        reader.close();
        
        return jsonArray;
    }

   

}
