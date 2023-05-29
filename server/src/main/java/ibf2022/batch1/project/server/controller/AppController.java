package ibf2022.batch1.project.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.AllArgsConstructor;

@Controller
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class AppController {

    @GetMapping(path = "/{path:[^\\.]*}")
    public String redirect() {
        return "forward:/";
    }

    @GetMapping(path = "/")
    public String index() {
        // System.out.println("index");
        return "index.html";
    }

}