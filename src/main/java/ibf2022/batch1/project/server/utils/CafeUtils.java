package ibf2022.batch1.project.server.utils;

import java.io.StringReader;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

public class CafeUtils {

    // common return for error in response
    public static ResponseEntity<String> getRespEntity(HttpStatus httpstatus, String respMessage) {

        return ResponseEntity
                .status(httpstatus)
                .body(">>>> " + respMessage);
    }

    public static JsonObject jsonStringToJsonObj(String json) {

        JsonReader reader = Json.createReader(new StringReader(json));
        return reader.readObject();
    }
}
