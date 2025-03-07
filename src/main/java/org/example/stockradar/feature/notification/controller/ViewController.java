package org.example.stockradar.feature.notification.controller;
import lombok.RequiredArgsConstructor;
import org.example.stockradar.feature.notification.service.NotificationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Hyun7en
 */

@Controller("notificationViewController")
@RequiredArgsConstructor
@RequestMapping("notification")
public class ViewController {

    private final NotificationService notificationService;

    @GetMapping("wishlist")
    public String wishlist() {
        return "notification/wishlist";
    }


}
