package org.example.stockradar.feature.alert.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("alertViewController")
@RequiredArgsConstructor
@RequestMapping("alert")
public class ViewController {

    @GetMapping("wishlist")
    public String wishlist() {
        return "alert/wishlist";
    }


}
