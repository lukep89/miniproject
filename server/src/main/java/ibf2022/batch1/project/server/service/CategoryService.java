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

                if (validateCategoryPayload(payload, false)) {
                    categoryRepo.saveCategory(getCategoryFromPayload(payload, false));

                    return CafeUtils.getRespEntity(HttpStatus.OK, "Added category successfully");

                } else {
                    return CafeUtils.getRespEntity(HttpStatus.BAD_REQUEST, "Invalid data");
                }

            } else {
                return CafeUtils.getRespEntity(HttpStatus.UNAUTHORIZED, "Unauthorized access");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getRespEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong!");

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
            cat.setId(obj.getInt("id"));
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
                    Optional<Category> opt = categoryRepo.findById(obj.getInt("id"));

                    if (opt.isPresent()) {
                        categoryRepo.updateCategory(getCategoryFromPayload(payload, true));

                        return CafeUtils.getRespEntity(HttpStatus.OK, "Updated category successfully");

                    } else {
                        return CafeUtils.getRespEntity(HttpStatus.OK, "Category id does not exist");
                    }
                } else {
                    return CafeUtils.getRespEntity(HttpStatus.BAD_REQUEST, "Invalid data");
                }

            } else {
                return CafeUtils.getRespEntity(HttpStatus.UNAUTHORIZED, "Unauthorized access");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getRespEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong!");
    }

}
