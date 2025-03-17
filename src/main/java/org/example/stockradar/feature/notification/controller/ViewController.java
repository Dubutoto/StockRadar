package org.example.stockradar.feature.notification.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Hyun7en
 */

@Controller("notificationViewController")
@RequiredArgsConstructor
public class ViewController {

    // 관심 상품 페이지로 이동
    @GetMapping("/notification/wishlist")
    public String wishlist() {
        return "notification/wishlist";
    }


}
