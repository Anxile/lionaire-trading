package com.lionaire.service;

import com.lionaire.domain.VerificationType;
import com.lionaire.model.User;
import com.lionaire.model.VerificationCode;

public interface VerificationCodeService {
    VerificationCode sendVerificationCode(User user, VerificationType verificationType);

    VerificationCode getVerificationCode(Long id);

    VerificationCode getVerificationCodeByUser(Long userId);


    void deleteVerificationCodeById(VerificationCode verificationCode);
}
