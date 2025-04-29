package com.playdata.userservice.user.dto;

import com.playdata.userservice.common.entity.Address;
import com.playdata.userservice.user.entity.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

// 데이터 단순 운반 용도로 사용하는 DTO
// 프론트 단으로 전달할, 전달받은 데이터는 따로 DTO를 선언하는 것을 권장.
// Entity는 DB와 밀접하게 연관되어 있음.
// -> 화면단에서 전달된 데이터에 Entity를 초기화하기 부족할 수도 있고, 노출되면 안되는 데이터도 존재할 가능성 있음.
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSaveReqDto {
    private String name;

    @NotEmpty(message = "이메일은 필수입니다!")
    private String email;

    @NotEmpty(message = "비밀번호는 필수입니다!")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;

    private Address address;

    // dto가 자기가 가지고 있는 필드 정보를 토대로 User Entity를 생성해서 리턴하는 메서드
    public User toEntity(PasswordEncoder encoder) {
        return User.builder()
                .name(this.name)
                .email(this.email)
                .password(encoder.encode(this.password))
                .address(this.address)
                .build();
    }
}
