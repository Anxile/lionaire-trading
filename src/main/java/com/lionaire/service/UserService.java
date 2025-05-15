package com.lionaire.service;

import com.lionaire.domain.VerificationType;
import com.lionaire.model.User;

public interface UserService {

    public User findByJwt(String jwt);
    public User findByEmail(String email);
    public User findById(Long id);


    public User enableTwoFactorAuth(
            User user,
            String sendTo,
            VerificationType verificationType
    );
    public User disableTwoFactorAuth(
            User user,
            String sendTo,
            VerificationType verificationType
    );

    User updatePassword(User user, String newPassword);


}
