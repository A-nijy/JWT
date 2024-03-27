package com.example.jwtTest2.config.filter;

import com.example.jwtTest2.config.auth.CustomUserDetailsService;
import com.example.jwtTest2.config.jwt.JWTProperties;
import com.example.jwtTest2.config.jwt.JwtFunction;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


// 토큰 검사하는 곳

// 1. 헤더에 토큰이 있는지 없는지 (회원가입, 로그인 / 로그인 이후)
// 2. 토큰이 유효한지 (올바른 토큰 형식인지)

@Slf4j
@RequiredArgsConstructor
public class CustomJwtFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtFunction jwtFunction;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        System.out.println("커스텀 필터 0");
        // 0. 헤더에 "Authorization"와 "Refresh_Token"에 담긴 값을 가져온다. (토큰)
        String header_access = request.getHeader(JWTProperties.HEADER_STRING);
        String header_refresh = request.getHeader(JWTProperties.REFRESH_STRING);

        System.out.println("커스텀 필터 1");
        // 1. 헤더에 토큰이 존재하는지 검사 (Authorization에 값이 있고, 그 값이 "Bearer "로 시작하는가? + Refresh_Token에는 값이 없는가) = access 토큰만 요청 받았는가?
        if(header_access != null && header_access.startsWith(JWTProperties.TOKEN_PREFIX) && header_refresh == null){

            System.out.println("커스텀 필터 1-1");
            // 1-1. 앞에 "Bearer " 문자열을 빼고 온전히 토큰만 가져온다.
            String jwtToken = header_access.replace("Bearer ", "");

            System.out.println("커스텀 필터 2");
            // 2. JWT 토큰 유효성 검사
            if(jwtFunction.validateAccessToken(jwtToken)){

                System.out.println("커스텀 필터 3");
                // 3. JWT 토큰을 복호화해서 Claim에 있는 loginId를 가져온다.
                String loginId = jwtFunction.getLoginId(jwtToken);

                System.out.println("커스텀 필터 3-1");
                // 3-1. Member에 토큰의 정보에 알맞는 Member가 있으면 userDetails 생성
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginId);

                System.out.println("커스텀 필터 3-2");
                // 3-2. 잘 가져왔나 확인
                if(userDetails != null){

                    System.out.println("커스텀 필터 --");
                    // 사용자 식별자(userDetails)와 접근권한 인증용 토큰 생성 (JWT 토큰 아님!!) [ 정확히는 사용자의 인증 정보를 가지고 있는 객체 생성 ]
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    System.out.println("커스텀 필터 --");
                    // 스프링 시큐리티에서 현재 사용자의 인증 정보를 설정하는 코드로
                    // 사용자가 인증되었음을 시스템에게 알려준다. (인증된 상태로 세션을 유지 = 시큐리티가 현재 사용자를 식별하고 보호가능)
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

                }
            }
        }



        System.out.println("(refresh)커스텀 필터 1");
        // 1. access 토큰이 만료되어 refresh 토큰을 재요청한 상태 / 헤더에 access와 refresh 토큰 모두 가지고 있는가?
        if(header_refresh != null && header_refresh.startsWith(JWTProperties.TOKEN_PREFIX) && header_access == null){

            System.out.println("(refresh)커스텀 필터 1-1");
            // 1-1. 앞에 "Bearer " 문자열을 빼고 온전히 토큰만 가져온다.
            String jwtToken = header_refresh.replace("Bearer ", "");

            System.out.println("(refresh)커스텀 필터 2");
            // 2. JWT 토큰 유효성 검사
            jwtFunction.validateRefreshToken(jwtToken);
        }



        System.out.println("이어서 하는 중");
        filterChain.doFilter(request, response);
    }
}



/*
UsernamePasswordAuthenticationToken는 사용자의 인증 정보를 표현하는 객체를 만들 때 만든다. (JWT 토큰 아님)

생성자
public UsernamePasswordAuthenticationToken(Object principal, Object credentials);
public UsernamePasswordAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities);

principal에는 주로 사용자의 식별자를 의미한다. (보통 사용자 이름(username) or 사용자 객체(user object)를 사용한다.)
credentials에는 사용자의 인증 자격증명(credenticals)을 의미한다. (보통 사용자의 비밀번호(password)를 사용한다. / 보안상 비밀번호를 평문으로 저장하거나 정송하지 않으므로, 주로 인증 토큰에서 비밀번호는 암호화가 되어있을 것이다.)
authorities에는 사용자가 가지고 있는 권한(authorities)목록을 의미한다. (보통 GrantedAuthority 인터페이스를 구현한 객체들의 컬렉션을 사용한다.)

userDetails에는 보통 사용자의 세부 정보를 가지고 있는 객체이다. (사용자의 이름, 비밀번호, 권한 등)
userDetails.getAuthorities()는 사용자가 가지고 있는 권한(authorities)목록을 반환하는 메서드이다.
두 번째에 사용자의 인증 자격 증명에 null을 넣는 것은
비밀번호나 다른 인증 자격증명을 사용하지 않고도 사용자의 인증을 처리할 때 사용한다.
 */