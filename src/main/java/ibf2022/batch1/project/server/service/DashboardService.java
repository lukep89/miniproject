package ibf2022.batch1.project.server.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ibf2022.batch1.project.server.repository.BillRepository;
import ibf2022.batch1.project.server.repository.CategoryRepository;
import ibf2022.batch1.project.server.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;


@Service
public class DashboardService {

    @Autowired
    CategoryRepository categoryRepo;

    @Autowired
    ProductRepository productRepo;

    @Autowired
    BillRepository billRepo;

    public ResponseEntity<Map<String, Object>> getCount() {

        Map<String, Object> map = new HashMap<>();

        map.put("category", categoryRepo.count());
        map.put("product", productRepo.count());
        map.put("bill", billRepo.count());

        return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
    }

}
