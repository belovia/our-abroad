package ru.belov.ourabroad.web.dto.update;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateEmailRequest {

    private String userId;
    private String email;
}
