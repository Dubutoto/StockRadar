package org.example.stockradar.feature.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("common")
public class CommonController {

    @GetMapping("privacy")
    public String privacy() {
        return "common/privacy";
    }

    @GetMapping("aboutUs")
    public String aboutUs() {
        return "common/aboutUs";
    }
}
