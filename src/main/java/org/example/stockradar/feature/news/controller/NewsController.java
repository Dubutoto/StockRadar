package org.example.stockradar.feature.news.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "news")
public class NewsController {

    // 뉴스 목록
    @GetMapping("list")
    public String news() {
        return "news/list";
    }

    //뉴스 상세
    @GetMapping("detail")
    public String detail() {
        return "news/detail";
    }
}
