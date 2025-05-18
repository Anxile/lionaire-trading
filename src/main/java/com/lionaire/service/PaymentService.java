package com.lionaire.service;

import com.lionaire.domain.PaymentMethod;
import com.lionaire.model.PaymentOrder;
import com.lionaire.model.User;
import com.lionaire.response.PaymentResponse;
import com.stripe.exception.StripeException;

public interface PaymentService {

    PaymentOrder createOrder(User user, Long amount, PaymentMethod paymentMethod);

    PaymentOrder getPaymentOrderById(Long id) throws Exception;

    Boolean ProccedPaymentOrder (PaymentOrder paymentOrder,
                                 String paymentId) throws Exception;


    PaymentResponse createStripePaymentLink(User user, Long Amount,
                                            Long orderId) throws StripeException, StripeException;
}
