package com.example.jwtTest2.controller;


import com.example.jwtTest2.Service.RestApiService;
import com.example.jwtTest2.dto.MemberRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RestApiController {

    private final RestApiService restApiService;


    // 회원 가입
    @PostMapping("/member/join")
    public String memberJoin(@RequestBody MemberRequestDto memberDto){

        restApiService.memberJoin(memberDto);

        return "유저 회원가입 완료!!";
    }


    // 로그인
    @PostMapping("/member/login")
    public String memberLogin(@RequestBody MemberRequestDto memberDto, HttpServletResponse response){

        System.out.println("로그인 시도 (컨트롤러)");
        restApiService.memberLogin(memberDto, response);

        return "로그인 완료!! (access 토큰, refresh 토큰 발급 완료)";
    }


    // access 재발급
    @GetMapping("/reissue/accessToken")
    public String reissueAccessToken(HttpServletRequest request, HttpServletResponse response){

        System.out.println("refresh 토큰 재요청중 / access 토큰 재발급 (컨트롤러)");
        restApiService.reissueAccessToken(request, response);

        return "access 토큰 재발급 완료";
    }



    // user, manager, admin 권한 접근 가능
    @GetMapping("/api/v1/user")
    public String user() {
        return "user";
    }

    // manager, admin 권한 접근 가능
    @GetMapping("/api/v1/manager")
    public String manager() {
        return "manager";
    }

    // admin 권한 접근 가능
    @GetMapping("/api/v1/admin")
    public String admin() {
        return "admin";
    }

}
