package project.floe.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    GUEST("ROLE_GUEST"), USER("ROLE_USER"), ADMIN("ROLE_ADMIN");

    //스프링 시큐리티에서는 권한(Role) 코드에 항상 "ROLE_" 접두사가 앞에 붙어야 하기 떄문
    private final String key;
}
