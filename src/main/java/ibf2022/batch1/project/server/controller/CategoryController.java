package ibf2022.batch1.project.server.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ibf2022.batch1.project.server.model.Category;
import ibf2022.batch1.project.server.service.CategoryService;
import ibf2022.batch1.project.server.utils.CafeUtils;

@RestController
@RequestMapping(path = "/api/category")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    CategoryService categorySvc;

    @PostMapping(path = "/add")
    ResponseEntity<String> addNewCategory(@RequestBody String payload) {

        try {
            return categorySvc.addNewCategory(payload);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getRespEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong!");
    }

    @GetMapping(path = "/get")
    ResponseEntity<List<Category>> getAllCategory(@RequestParam(required = false) String filterValue) {

        try {
            return categorySvc.getAllCategory(filterValue);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @PutMapping(path = "/update")
    ResponseEntity<String> updateCategory(@RequestBody String payload) {

        try {
            return categorySvc.updateCategory(payload);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getRespEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong!");
    }

}
