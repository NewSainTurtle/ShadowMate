package com.newsainturtle.shadowmate.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
public class JoinRequest {

    @Email
    @NotNull
    String email;

    @NotNull
    @Size(min = 6, max = 20, message = "비밀번호는 6~20자로 입력해야합니다.")
    String password;

    @NotNull
    @Size(min = 2, max = 10, message = "닉네임는 2~10자로 입력해야합니다.")
    String nickname;

    @Builder
    public JoinRequest(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }
}
