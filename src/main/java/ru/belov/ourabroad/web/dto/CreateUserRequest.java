package ru.belov.ourabroad.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateUserRequest {

    private String email;
    private String phone;
    private String password;
}
