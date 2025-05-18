package com.lionaire.service;

import com.lionaire.domain.PaymentMethod;
import com.lionaire.model.PaymentOrder;
import com.lionaire.model.User;
import com.lionaire.repository.PaymentOrderRepository;
import com.lionaire.response.PaymentResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService{

    @Value("${stripe.api.key}")
    private String stripeSecretKey;

    @Autowired
    private PaymentOrderRepository paymentOrderRepository;

    @Override
    public PaymentOrder createOrder(User user, Long amount, PaymentMethod paymentMethod) {
        PaymentOrder order=new PaymentOrder();
        order.setUser(user);
        order.setAmount(amount);
        order.setPaymentMethod(paymentMethod);
        return paymentOrderRepository.save(order);
    }

    @Override
    public PaymentOrder getPaymentOrderById(Long id) throws Exception {
        Optional<PaymentOrder> optionalPaymentOrder=paymentOrderRepository.findById(id);
        if(optionalPaymentOrder.isEmpty()){
            throw new Exception("payment order not found with id "+id);
        }
        return optionalPaymentOrder.get();
    }

    @Override
    public Boolean ProccedPaymentOrder(PaymentOrder paymentOrder, String paymentId) throws Exception {
        return null;
    }

    @Override
    public PaymentResponse createStripePaymentLink(User user, Long Amount, Long orderId) throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:2345/wallet?order_id="+orderId)
                .setCancelUrl("http://localhost:2345/payment/cancel")
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("cad")
                                .setUnitAmount(Amount*100)
                                .setProductData(SessionCreateParams
                                        .LineItem
                                        .PriceData
                                        .ProductData
                                        .builder()
                                        .setName("Top up wallet")
                                        .build()
                                ).build()
                        ).build()
                ).build();

        Session session = Session.create(params);

        System.out.println("session _____ " + session);

        PaymentResponse res = new PaymentResponse();
        res.setPayment_url(session.getUrl());

        return res;
    }
}
