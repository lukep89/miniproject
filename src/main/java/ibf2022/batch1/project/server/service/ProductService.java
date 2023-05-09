package ibf2022.batch1.project.server.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ibf2022.batch1.project.server.JWT.JwtFilter;
import ibf2022.batch1.project.server.model.Product;
import ibf2022.batch1.project.server.model.ProductByCategoryWrapper;
import ibf2022.batch1.project.server.model.ProductByIdWrapper;
import ibf2022.batch1.project.server.model.ProductWrapper;
import ibf2022.batch1.project.server.repository.ProductRepository;
import ibf2022.batch1.project.server.utils.CafeUtils;
import jakarta.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProductService {

    @Autowired
    ProductRepository productRepo;

    @Autowired
    JwtFilter jwtFilter;

    public ResponseEntity<String> addNewProduct(String payload) {
        log.info(">>>> inside addProduct {}", payload);

        try {
            if (jwtFilter.isAdmin()) {

                if (validateProductPayload(payload, false)) {
                    productRepo.saveNewProduct(getProductFromPayload(payload, false));

                    return CafeUtils.getRespEntity(HttpStatus.OK, "Added product successfully");
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

    private boolean validateProductPayload(String payload, boolean validiateId) {

        JsonObject obj = CafeUtils.jsonStringToJsonObj(payload);

        if (obj.containsKey("categoryId")
                && obj.containsKey("name")
                && obj.containsKey("description")
                && obj.containsKey("price")) {

            if (obj.containsKey("id") && validiateId) {
                return true;

            } else if (!validiateId) {
                return true;
            }
        }
        return false;
    }

    private Product getProductFromPayload(String payload, boolean isUpdate) {
        JsonObject obj = CafeUtils.jsonStringToJsonObj(payload);

        Product product = new Product();

        if (isUpdate) {
            product.setId(Integer.parseInt(obj.getString("id")));
        }
        product.setName(obj.getString("name"));
        product.setDescription(obj.getString("description"));
        product.setPrice(Float.parseFloat(obj.getString("price")));
        product.setStatus("active");
        product.setCategoryId(Integer.parseInt(obj.getString("categoryId")));

        return product;

    }

    public ResponseEntity<List<ProductWrapper>> getAllProduct() {

        try {
            return new ResponseEntity<List<ProductWrapper>>(productRepo.getAllProduct(), HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<List<ProductWrapper>>(
                new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);

    }

    public ResponseEntity<String> updateProduct(String payload) {

        try {
            if (jwtFilter.isAdmin()) {

                if (validateProductPayload(payload, true)) {
                    JsonObject obj = CafeUtils.jsonStringToJsonObj(payload);
                    Optional<Product> opt = productRepo.findById(Integer.parseInt(obj.getString("id")));

                    if (opt.isPresent()) {
                        Product product = getProductFromPayload(payload, true);
                        log.info(">>>> inside updateProduct - product: {}", product);

                        product.setStatus(opt.get().getStatus());

                        productRepo.updateProduct(product);

                        return CafeUtils.getRespEntity(HttpStatus.OK, "Updated product successfully");

                    } else {
                        return CafeUtils.getRespEntity(HttpStatus.OK, "Product id does not exist");
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

    public ResponseEntity<String> deleteProduct(Integer id) {
        log.info(">>>> inside deleteProduct - id: {}", id);
        try {
            if (jwtFilter.isAdmin()) {

                Optional<Product> opt = productRepo.findById(id);
                if (opt.isPresent()) {

                    productRepo.deleteProductById(id);

                    return CafeUtils.getRespEntity(HttpStatus.OK, "Deleted product successfully");

                } else {
                    return CafeUtils.getRespEntity(HttpStatus.OK, "Product id does not exist");
                }

            } else {
                return CafeUtils.getRespEntity(HttpStatus.UNAUTHORIZED, "Unauthorized access");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getRespEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong!");
    }

    public ResponseEntity<String> updateProductStatus(String payload) {
        log.info(">>>> inside updateProductStatus - payload: {}", payload);
        try {
            if (jwtFilter.isAdmin()) {
                JsonObject obj = CafeUtils.jsonStringToJsonObj(payload);

                Optional<Product> opt = productRepo.findById(Integer.parseInt(obj.getString("id")));
                if (opt.isPresent()) {

                    String status = obj.getString("status");
                    Integer id = Integer.parseInt(obj.getString("id"));

                    productRepo.updateProductStatus(status, id);

                    return CafeUtils.getRespEntity(HttpStatus.OK, "Updated product status successfully");

                } else {
                    return CafeUtils.getRespEntity(HttpStatus.OK, "Product id does not exist");
                }
            } else {
                return CafeUtils.getRespEntity(HttpStatus.UNAUTHORIZED, "Unauthorized access");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getRespEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong!");
    }

    public ResponseEntity<List<ProductByCategoryWrapper>> getProductByCategory(Integer id) {

        try {
            return new ResponseEntity<List<ProductByCategoryWrapper>>(productRepo.getProductByCategory(id),
                    HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<ProductByIdWrapper> getProductById(Integer id) {
        try {

            Optional<ProductByIdWrapper> opt = productRepo.getProductById(id);
            if (opt.isPresent()) {
                return new ResponseEntity<>(opt.get(), HttpStatus.OK);
                
            } else {
                new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
