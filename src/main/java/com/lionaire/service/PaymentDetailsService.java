package com.lionaire.service;

import com.lionaire.model.PaymentDetails;
import com.lionaire.model.User;

public interface PaymentDetailsService {

    public PaymentDetails addPaymentDetails(String accountNuber,
                                            String accountHoulderName,
                                            String ifsc,
                                            String bankName,
                                            User user);
    public PaymentDetails getUserPaymentDetails(User user);
}
