package org.example.stockradar.feature.product.controller;

import lombok.RequiredArgsConstructor;
import org.example.stockradar.feature.CustomerInquiry.controller.CustomerInquiryController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("product")
@RequiredArgsConstructor
public class ProductCategoryController {
    private static final Logger logger = LoggerFactory.getLogger(ProductCategoryController.class);

    @GetMapping("productCategory")
    public String productCategory() {
        logger.info("ProductCategory진입");
        return "product/productCategory";
    }
}
