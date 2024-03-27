package com.example.jwtTest2.dto;


import com.example.jwtTest2.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MemberServerDto {

    private long memberId;
    private String loginId;
    private String loginPassword;
    private String roles;

    public MemberServerDto(Member member){
        memberId = member.getId();
        loginId = member.getLoginId();
        loginPassword = member.getLoginPassword();
        roles = member.getRoles();
    }


    public List<String> getRoleList(){
        if(this.roles.length()>0){
            return Arrays.asList(this.roles.split(","));
        }
        return new ArrayList<>();
    }
}
