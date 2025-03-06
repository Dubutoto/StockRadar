package org.example.stockradar.feature.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class testController {

    //회원 가입 페이지 이동
    @GetMapping("/auth/signUp")
    public String signIn() {
        return "auth/signUp";
    }
    
    //로그인 페이지 이동
    @GetMapping("/auth/signIn")
    public String signUp() {
        return "auth/signIn";
    }
    
    //마이 페이지 이동
    @GetMapping("/auth/accountDetails")
    public String myPage() {
        return "auth/accountDetails";
    }

    //관심상품 페이지 이동
    @GetMapping("/auth/wishlist")
    public String wishList() {
        return "auth/wishlist";
    }

    //비밀번호 찾기 페이지
    @GetMapping("/auth/idInquiry")
    public String idInquiry() {
        return "auth/idInquiry";
    }
    //아이디 찾기 페이지
    @GetMapping("/auth/pwInquiry")
    public String pwInquiry() {
        return "auth/pwInquiry";
    }

    //사이트 소개
    @GetMapping("/common/aboutUs")
    public String aboutUs() {
        return "common/aboutUs";
    }

    //문의하기
    @GetMapping("/common/contact")
    public String contact() {
        return "common/contact";
    }

    //개인정보 처리방침
    @GetMapping("/common/privacy")
    public String privacy() {
        return "common/privacy";
    }

    //뉴스 페이지 이동
    @GetMapping("/news/news")
    public String news() {
        return "news/news";
    }

    //뉴스 게시글 페이지 이동
    @GetMapping("/news/newsDetail")
    public String newsDetail() {
        return "news/newsDetail";
    }

    
    //상품
    @GetMapping("/product/stockLayout")
    public String category() {
        return "/product/stockLayout";
    }


    @GetMapping("/product/productCategory")
    public String productCategory() {return "/product/productCategory";}

    //커뮤니티 이동
//    @GetMapping("/board/boardList")
//    public String boardMain() {
//        return "board/boardList";
//    }

    @GetMapping("/product/gpu/rtx3050")
    public String gpuRtx3050() {return "/product/gpu/rtx3050";}

    // 커뮤니티 글 쓰기 페이지 이동
    
    //커뮤니티 글 삭제 페이지 이동


}
