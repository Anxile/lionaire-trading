package com.lionaire.service;

import com.lionaire.model.TwoFactorOTP;
import com.lionaire.model.User;

public interface TwoFactorOtpService {

    TwoFactorOTP createTwoFactorOtp(User user, String opt, String jwt);

    TwoFactorOTP findByUser(Long userId);

    TwoFactorOTP findById(String id);

    boolean verifyOtp(TwoFactorOTP twoFactorOTP, String otp);

    void delete(TwoFactorOTP twoFactorOTP);
}
