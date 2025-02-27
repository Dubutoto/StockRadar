package org.example.stockradar;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
    @RequestMapping("/")
    public String home() {
        return "main"; // main.html (템플릿 폴더에 위치)
    }
}
