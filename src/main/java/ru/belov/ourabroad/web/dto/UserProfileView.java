package ru.belov.ourabroad.web.dto;

import lombok.Builder;
import lombok.Getter;
import ru.belov.ourabroad.core.domain.*;

import java.util.List;

@Getter
@Builder
public class UserProfileView {

    private User user;
    private Profile profile;
    private Reputation reputation;
    private SpecialistProfile specialistProfile;
    private List<Verification> verifications;
}
