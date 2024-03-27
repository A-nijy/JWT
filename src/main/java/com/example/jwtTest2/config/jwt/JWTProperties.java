package com.example.jwtTest2.config.jwt;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

public interface JWTProperties {

    String ACCESS_SECRET = "foAjdfk3we2wk4dfkfdfdDG9df734fdf86DG87dgG8dFd8fdG2";   // 우리 서버만 알고 있는 비밀 값
    String REFRESH_SECRET = "di3kd8bgj4jkf7sjk29dkUF4jnf982nfj48fkxbvoe02jz63ccj3b";
    byte[] accessKeyBytes = Decoders.BASE64.decode(JWTProperties.ACCESS_SECRET);
    byte[] refreshKeyBytes = Decoders.BASE64.decode(JWTProperties.ACCESS_SECRET);
    Key ACCESS_KEY = Keys.hmacShaKeyFor(accessKeyBytes);
    Key REFRESH_KEY = Keys.hmacShaKeyFor(refreshKeyBytes);
    int ACCESS_TIME = 60000; // access Token 만료(10분 * 3)
    int REFRESH_TIME = 60000*60*24; // refresh Token 만료(60분 * 24)
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";
    String REFRESH_STRING = "Refresh_Token";

    String ISSUER = "ADIMN_AN";
}
