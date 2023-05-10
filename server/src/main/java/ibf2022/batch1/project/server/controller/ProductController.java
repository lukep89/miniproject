package ibf2022.batch1.project.server.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ibf2022.batch1.project.server.model.ProductByCategoryWrapper;
import ibf2022.batch1.project.server.model.ProductByIdWrapper;
import ibf2022.batch1.project.server.model.ProductWrapper;
import ibf2022.batch1.project.server.service.ProductService;
import ibf2022.batch1.project.server.utils.CafeUtils;

@RestController
@RequestMapping(path = "/api/product")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productSvc;

    @PostMapping(path = "/add")
    public ResponseEntity<String> addProduct(@RequestBody String payload) {

        try {
            return productSvc.addNewProduct(payload);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getRespEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong!");
    }

    @GetMapping(path = "/get")
    public ResponseEntity<List<ProductWrapper>> getAllProduct(@RequestBody String payload) {

        try {
            return productSvc.getAllProduct();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping(path = "/update")
    ResponseEntity<String> updateProduct(@RequestBody String payload) {

        try {
            return productSvc.updateProduct(payload);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getRespEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong!");
    }

    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Integer id) {

        try {
            return productSvc.deleteProduct(id);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getRespEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong!");
    }

    @PostMapping(path = "/updateStatus")
    public ResponseEntity<String> updateStatus(@RequestBody String payload) {

        try {
            return productSvc.updateProductStatus(payload);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getRespEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong!");
    }

    @GetMapping(path = "/getByCategory/{id}")
    public ResponseEntity<List<ProductByCategoryWrapper>> getByCategory(@PathVariable Integer id) {

        try {
            return productSvc.getProductByCategory(id);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping(path = "/getById/{id}")
    public ResponseEntity<ProductByIdWrapper> getById(@PathVariable Integer id) {

        try {
            return productSvc.getProductById(id);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
