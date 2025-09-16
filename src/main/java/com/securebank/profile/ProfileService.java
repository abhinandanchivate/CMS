package com.securebank.profile;

import com.securebank.common.security.CurrentUserService;
import com.securebank.domain.user.User;
import com.securebank.domain.user.UserRepository;
import com.securebank.profile.dto.ProfileResponse;
import com.securebank.profile.dto.UpdateProfileRequest;
import com.securebank.profile.dto.UpdateProfileResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {

    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;

    public ProfileService(CurrentUserService currentUserService, UserRepository userRepository) {
        this.currentUserService = currentUserService;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfile() {
        User user = currentUserService.requireCurrentUser();
        return new ProfileResponse(
                user.getId().toString(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getMobile(),
                user.getDateOfBirth(),
                user.getAddress()
        );
    }

    @Transactional
    public UpdateProfileResponse updateProfile(UpdateProfileRequest request) {
        User user = currentUserService.requireCurrentUser();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setMobile(request.mobile());
        user.setAddress(request.address());
        userRepository.save(user);
        return UpdateProfileResponse.updated();
    }
}
