package org.example.stockradar.feature.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.stockradar.feature.auth.entity.Member;
import org.example.stockradar.feature.auth.repository.MemberRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder; // 혹시 비밀번호 수정 시 필요하다면 사용

    /**
     * 현재 로그인된 사용자의 회원정보 수정 폼
     * GET /profile
     */
    @GetMapping("/profile")
    public String profileForm(Model model) {
        // 1) SecurityContext에서 현재 로그인된 사용자 아이디(이메일) 가져오기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || "anonymousUser".equals(auth.getName())) {
            // 로그인되지 않은 상태이면 로그인 페이지로
            return "redirect:/login";
        }
        String currentUserId = auth.getName(); // 예: memberId(이메일)

        // 2) DB에서 회원 조회
        Member member = memberRepository.findByMemberId(currentUserId);
        if (member == null) {
            // 혹시 DB에 없으면 에러 페이지 or 로그인 페이지로
            return "redirect:/login";
        }

        // 3) 모델에 담아 폼으로 전달
        model.addAttribute("member", member);
        return "auth/profile"; // → resources/templates/auth/profile.html
    }

    /**
     * 현재 로그인된 사용자의 회원정보 수정 처리
     * POST /profile/update
     */
    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam("memberId") String memberId,
                                @RequestParam("userName") String userName,
                                @RequestParam("memberPhone") String memberPhone,
                                Model model) {

        // 1) SecurityContext에서 현재 로그인된 사용자와 일치하는지 검증
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !memberId.equals(auth.getName())) {
            // 로그인 정보와 폼의 memberId가 다르면 에러 처리
            model.addAttribute("error", "잘못된 접근입니다. 다시 시도해주세요.");
            return "auth/profile";
        }

        // 2) DB에서 사용자 조회
        Member member = memberRepository.findByMemberId(memberId);
        if (member == null) {
            model.addAttribute("error", "존재하지 않는 회원입니다.");
            return "auth/profile";
        }

        // 3) 변경할 필드 업데이트 (ex: 이름, 전화번호)
        member.setUserName(userName);
        member.setMemberPhone(memberPhone);

        // 만약 비밀번호 수정도 같이 하고 싶다면
        // member.setMemberPw(passwordEncoder.encode(새로운비번));

        // 4) 저장
        memberRepository.save(member);

        // 5) 수정 성공 메시지 및 갱신된 정보 모델에 담기
        model.addAttribute("success", "회원정보가 수정되었습니다.");
        model.addAttribute("member", member);

        return "auth/profile";
    }
}
