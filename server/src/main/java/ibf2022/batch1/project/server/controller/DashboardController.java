package ibf2022.batch1.project.server.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ibf2022.batch1.project.server.service.DashboardService;

@RestController
@RequestMapping(path = "/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    DashboardService dashboardSvc;

    @GetMapping(path = "/details")
    ResponseEntity<Map<String, Object>> getCount() {
        return dashboardSvc.getCount();
    }

}
