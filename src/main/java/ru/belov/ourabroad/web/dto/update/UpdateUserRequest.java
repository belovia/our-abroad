package ru.belov.ourabroad.web.dto.update;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UpdateUserRequest {

    private String email;
    private String phone;

    private String telegramUsername;
    private String whatsappNumber;
    private String activity;
}
