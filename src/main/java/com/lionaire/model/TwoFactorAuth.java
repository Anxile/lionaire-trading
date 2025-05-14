package com.lionaire.model;

import com.lionaire.domain.VerificationType;
import lombok.Data;

@Data
public class TwoFactorAuth {
    private boolean isEnabled;
    private VerificationType sendTo;
}
