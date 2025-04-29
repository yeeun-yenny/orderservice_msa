package com.playdata.productservice.common.auth;

import com.playdata.productservice.user.entity.Role;
import lombok.*;

@Setter @Getter @ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenUserInfo {

    private String email;
    private Role role;

}
