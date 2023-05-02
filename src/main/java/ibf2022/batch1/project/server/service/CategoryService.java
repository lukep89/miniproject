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

        try {
            if (jwtFilter.isAdmin()) {
                JsonObject obj = CafeUtils.jsonStringToJsonObj(payload);

                // // TODO: frontend will list all the category when adding new category
                // Optional<Category> opt = categoryRepo.findByName(obj.getString("name"));
                // if (opt.isPresent()) {
                // return ResponseEntity
                // .status(HttpStatus.BAD_REQUEST)
                // .body(">>>> Category already exist");
                // }

                if (validateCategoryPayload(payload, false)) {
                    categoryRepo.saveCategory(getCategoryFromPayload(payload, false));

                    return ResponseEntity
                            .status(HttpStatus.OK)
                            .body(">>>> Added category successfully");

                } else {
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body(">>>> Invalid data");
                }

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

    private boolean validateCategoryPayload(String payload, boolean validiateId) {

        JsonObject obj = CafeUtils.jsonStringToJsonObj(payload);

        if (obj.containsKey("name")) {
            if (obj.containsKey("id") && validiateId) {
                return true;

            } else if (!validiateId) {
                return true;
            }
        }
        return false;
    }

    private Category getCategoryFromPayload(String payload, boolean isUpdate) {
        JsonObject obj = CafeUtils.jsonStringToJsonObj(payload);

        Category cat = new Category();

        if (isUpdate) {
            cat.setId(Integer.parseInt(obj.getString("id")));
        }
        cat.setName(obj.getString("name"));

        return cat;
    }

    public ResponseEntity<List<Category>> getAllCategory(String filterValue) {

        try {
            // filterVAlue is to determine Admni or User to get respective categories
            if (Strings.hasText(filterValue) &&
                    filterValue.equalsIgnoreCase("true")) {
                log.info(">>>> inside getAllCategories - if block");

                // for the User get category only with product where status is true
                return new ResponseEntity<List<Category>>(categoryRepo.getAllCategoryForUser(), HttpStatus.OK);
            } else {
                // for the Admin get all product regardless of status
                return new ResponseEntity<List<Category>>(categoryRepo.getAllCategory(), HttpStatus.OK);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<List<Category>>(
                new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<String> updateCategory(String payload) {
        log.info(">>>> inside updateCategory - payload: {}", payload);

        try {
            if (jwtFilter.isAdmin()) {

                if (validateCategoryPayload(payload, true)) {
                    JsonObject obj = CafeUtils.jsonStringToJsonObj(payload);
                    Optional<Category> opt = categoryRepo.findById(Integer.parseInt(obj.getString("id")));

                    if (opt.isPresent()) {
                        categoryRepo.updateCategory(getCategoryFromPayload(payload, true));

                        return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(">>>> Updated category successfully");

                    } else {
                        return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(">>>> Category id does not exist");
                    }
                } else {
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body(">>>> Invalid data");
                }

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

}
