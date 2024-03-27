package com.example.jwtTest2.config.auth;

import com.example.jwtTest2.dto.MemberServerDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final MemberServerDto memberServerDto;

    //------------------------------------------------

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        memberServerDto.getRoleList().forEach(r -> {
            authorities.add(() -> r);
        });

        return authorities;
    }

    //--------------------------------------------------

    @Override
    public String getPassword() {
        return memberServerDto.getLoginPassword();
    }

    @Override
    public String getUsername() {
        return memberServerDto.getLoginId();
    }

    //----------------------------------------------------

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
