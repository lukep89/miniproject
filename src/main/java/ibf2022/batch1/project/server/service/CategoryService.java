package ibf2022.batch1.project.server.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ibf2022.batch1.project.server.JWT.JwtFilter;
import ibf2022.batch1.project.server.model.Category;
import ibf2022.batch1.project.server.repository.CategoryRepository;
import ibf2022.batch1.project.server.utils.CafeUtils;
import io.jsonwebtoken.lang.Strings;
import jakarta.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CategoryService {

    @Autowired
    CategoryRepository categoryRepo;

    @Autowired
    JwtFilter jwtFilter;

    public ResponseEntity<String> addNewCategory(String payload) {
        log.info(">>>> inside addNewCategory {}", payload);

        JsonObject obj = CafeUtils.jsonStringToJsonObj(payload);

        log.info(">>>> inside addNewCategory - obj: {}", obj);

        try {
            if (jwtFilter.isAdmin()) {

                // if (validateCategoryJsonObj(obj, false)) {
                // categoryRepo.saveCategory(getCategoryFromJsonObj(obj, false));

                // return ResponseEntity
                // .status(HttpStatus.OK)
                // .body(">>>> Successfully added Category");
                // }

                categoryRepo.saveCategory(obj.getString("name"));
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(">>>> Successfully added Category");

            } else {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(">>>> Unauthorized access");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(">>>> Something went wrong!");

    }


    public ResponseEntity<List<Category>> getAllCategories(String filterValue) {

        try {

            if (Strings.hasText(filterValue) &&
                    filterValue.equalsIgnoreCase("true")) {

                log.info(">>>> inside getAllCategories - if block");
                return new ResponseEntity<List<Category>>(categoryRepo.getCategories(), HttpStatus.OK);
            } else {
                return new ResponseEntity<List<Category>>(categoryRepo.getCategories(), HttpStatus.OK);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<List<Category>>(
                new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<String> updateCategory(String payload) {
        log.info(">>>> inside updateCategory - payload: {}", payload);

        JsonObject obj = CafeUtils.jsonStringToJsonObj(payload);

        try {

            if (jwtFilter.isAdmin()) {
                Optional<Category> opt = categoryRepo.findById(Integer.parseInt(obj.getString("id")));

                if (opt.isPresent()) {
                    log.info(">>>> inside updateCategory - opt: {}", opt.get());

                    categoryRepo.updateCategory(Category.toCategory(payload));

                    return ResponseEntity
                            .status(HttpStatus.OK)
                            .body(">>>> Successfully updated Category");

                } else {
                    return ResponseEntity
                            .status(HttpStatus.OK)
                            .body(">>>> Category id does not exist");
                }
            } else {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(">>>> Unauthorized access");
            }

        } catch (

        Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(">>>> Something went wrong!");
    }

}
