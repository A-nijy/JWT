package com.example.jwtTest2.config.jwt;


import com.example.jwtTest2.domain.Member;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class JwtFunction {

    // access 토큰 생성
    public String createAccessToken(Member member)
    {
        Claims claims = Jwts.claims();
        claims.put("memberId", member.getId());
        claims.put("loginId", member.getLoginId());
        claims.put("roles", member.getRoles());

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuer(JWTProperties.ISSUER)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWTProperties.ACCESS_TIME))
                .signWith(JWTProperties.ACCESS_KEY, SignatureAlgorithm.HS256)
                .compact();

        accessToken = JWTProperties.TOKEN_PREFIX + accessToken;

        return accessToken;
    }

    // access 토큰 생성
    public String createRefreshToken(Member member)
    {

        String refreshToken = Jwts.builder()
                .setIssuer(JWTProperties.ISSUER)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWTProperties.REFRESH_TIME))
                .signWith(JWTProperties.REFRESH_KEY, SignatureAlgorithm.HS256)
                .compact();

        refreshToken = JWTProperties.TOKEN_PREFIX + refreshToken;

        return refreshToken;
    }


    // 헤더에 있는 토큰에서 "Bearer "를 뺀 토큰 유효 검증 (access Token 검증)
    public boolean validateAccessToken(String token){

        try {
            Jwts.parserBuilder().setSigningKey(JWTProperties.ACCESS_KEY).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e){
            log.info("잘못된 JWT access 토큰 형식입니다.");
        } catch (ExpiredJwtException e){
            log.info("JWT access 토큰의 유효 기간이 지났습니다.");
        } catch (UnsupportedJwtException e){
            log.info("지원하지 않는 JWT access 토큰입니다.");
        } catch (IllegalArgumentException e){
            log.info("access 잘못된 값이 입력되었습니다.");
        }

        return false;
    }


    // 헤더에 있는 토큰에서 "Bearer "를 뺀 토큰 유효 검증 (refresh Token 검증)
    public boolean validateRefreshToken(String token){

        try {
            Jwts.parserBuilder().setSigningKey(JWTProperties.REFRESH_KEY).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e){
            log.info("잘못된 JWT refresh 토큰 형식입니다.");
        } catch (ExpiredJwtException e){
            log.info("JWT refresh 토큰의 유효 기간이 지났습니다.");
            log.info("재로그인 요청바람");
        } catch (UnsupportedJwtException e){
            log.info("지원하지 않는 JWT refresh 토큰입니다.");
        } catch (IllegalArgumentException e){
            log.info("refresh 잘못된 값이 입력되었습니다.");
        }

        return false;
    }



    // JWT 토큰을 해독하여 Claim에 "loginId"의 값을 가져온다.
    public String getLoginId(String accessToken){
        String loginId = parseClaims(accessToken).get("loginId", String.class);

        return loginId;
    }


    // access 토큰에서 가져온 Claims안에 memberId 추출하기
    public Long getMemberId(String accessToken){
        return parseClaims(accessToken).get("memberId", Long.class);
    }


    // JWT access토큰에서 Claims 추출하기
    public Claims parseClaims(String accessToken){
        try {
            return Jwts.parserBuilder().setSigningKey(JWTProperties.ACCESS_KEY).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }
}
