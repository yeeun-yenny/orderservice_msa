package com.playdata.userservice.common.auth;

import com.playdata.userservice.user.entity.Role;

@Setter @Getter @ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenUserInfo {

    private String email;
    private Role role;

}
