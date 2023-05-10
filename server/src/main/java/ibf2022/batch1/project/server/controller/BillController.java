package ibf2022.batch1.project.server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ibf2022.batch1.project.server.model.Bill;
import ibf2022.batch1.project.server.service.BillService;
import ibf2022.batch1.project.server.utils.CafeUtils;

@RestController
@RequestMapping(path = "/api/bill")
@CrossOrigin(origins = "*")
public class BillController {

    @Autowired
    BillService billSvc;

    @PostMapping(path = "/generateReport")
    ResponseEntity<String> generateReport(@RequestBody String payload) {

        try {
            return billSvc.generateReport(payload);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getRespEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong!");
    }

    @GetMapping(path = "/getBills")
    ResponseEntity<List<Bill>> getBills() {

        try {
            return billSvc.getBills();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @PostMapping(path = "/getPdf")
    ResponseEntity<byte[]> getPdf(@RequestBody String payload) {

        try {
            return billSvc.getPdf(payload);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @DeleteMapping(path = "/delete/{id}")
    ResponseEntity<String> deleteBill(@PathVariable Integer id) {

        try {
            return billSvc.deleteBill(id);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getRespEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong!");
    }
}
