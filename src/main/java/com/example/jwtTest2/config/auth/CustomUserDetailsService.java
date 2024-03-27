package com.example.jwtTest2.config.auth;

import com.example.jwtTest2.domain.Member;
import com.example.jwtTest2.domain.repository.MemberRepository;
import com.example.jwtTest2.dto.MemberServerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;


    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {

        System.out.println("PrincipalDetailsService의 loadUserByUsername() 실행");


        Member member = memberRepository.findByLoginId(loginId).orElseThrow(() -> new UsernameNotFoundException("해당 아이디는 존재하지 않습니다."));

        MemberServerDto memberServerDto = new MemberServerDto(member);

        return new CustomUserDetails(memberServerDto);
    }
}
