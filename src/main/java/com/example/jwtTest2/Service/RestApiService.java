package com.example.jwtTest2.Service;


import com.example.jwtTest2.config.jwt.JWTProperties;
import com.example.jwtTest2.config.jwt.JwtFunction;
import com.example.jwtTest2.domain.Member;
import com.example.jwtTest2.domain.RefreshToken;
import com.example.jwtTest2.domain.repository.MemberRepository;
import com.example.jwtTest2.domain.repository.RefreshTokenRepository;
import com.example.jwtTest2.dto.MemberRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestApiService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtFunction jwtFunction;
    private final RefreshTokenRepository refreshTokenRepository;


    @Transactional
    public void memberJoin(MemberRequestDto memberDto) {

        Member memberCheck = memberRepository.findByLoginId(memberDto.getLoginId()).orElse(null);

        if(memberCheck != null){
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        memberDto.setLoginPassword(bCryptPasswordEncoder.encode(memberDto.getLoginPassword())); // 비밀번호를 암호화해서 DB에 저장
        memberDto.setRoles("ROLE_USER");

        Member member = new Member(memberDto);

        memberRepository.save(member);
    }


    @Transactional
    public void memberLogin(MemberRequestDto memberDto, HttpServletResponse response) {

        Member member = memberRepository.findByLoginId(memberDto.getLoginId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if(!bCryptPasswordEncoder.matches(memberDto.getLoginPassword(), member.getLoginPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtFunction.createAccessToken(member);
        String refreshToken = jwtFunction.createRefreshToken(member);

        RefreshToken checkRefreshTooken = refreshTokenRepository.findByMemberId(member.getId()).orElse(null);

        if(checkRefreshTooken != null){
            checkRefreshTooken.updateToken(refreshToken);
        } else {
            RefreshToken refreshTokenEntity = new RefreshToken(member.getId(), refreshToken);
            refreshTokenRepository.save(refreshTokenEntity);
        }

        response.addHeader(JWTProperties.HEADER_STRING, accessToken);
        response.addHeader(JWTProperties.REFRESH_STRING, refreshToken);
    }


    // access 만료로 재발급시 refresh 토큰도 제발급 해준다. (RTR)
    @Transactional
    public void reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {

        String header_refresh = request.getHeader(JWTProperties.REFRESH_STRING);

        RefreshToken refresh = refreshTokenRepository.findByToken(header_refresh).orElseThrow(() -> new IllegalArgumentException("해당 refresh토큰이 DB에 존재하지 않습니다."));

        Member member = memberRepository.findById(refresh.getMemberId()).orElseThrow(() -> new IllegalArgumentException("해당 회원은 존재하지 않습니다."));

        String accessToken = jwtFunction.createAccessToken(member);
        String refreshToken = jwtFunction.createRefreshToken(member);

        refresh.updateToken(refreshToken);

        response.addHeader(JWTProperties.HEADER_STRING, accessToken);
        response.addHeader(JWTProperties.REFRESH_STRING, refreshToken);
    }
}
