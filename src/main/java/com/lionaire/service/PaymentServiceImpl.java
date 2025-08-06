package com.lionaire.service;

import com.lionaire.domain.OrderStatus;
import com.lionaire.domain.PaymentMethod;
import com.lionaire.domain.PaymentOrderStatus;
import com.lionaire.model.PaymentOrder;
import com.lionaire.model.User;
import com.lionaire.model.Wallet;
import com.lionaire.repository.PaymentOrderRepository;
import com.lionaire.repository.UserRepository;
import com.lionaire.repository.WalletRepository;
import com.lionaire.response.PaymentResponse;
import com.stripe.Stripe;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.InvoicePayment;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
//import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

import static com.stripe.Stripe.apiKey;

@Service
public class PaymentServiceImpl implements PaymentService{

    @Value("${stripe.api.key}")
    private String stripeSecretKey;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private PaymentOrderRepository paymentOrderRepository;

    @Override
    public PaymentOrder createOrder(User user, Long amount, PaymentMethod paymentMethod) {
        PaymentOrder order=new PaymentOrder();
        order.setUser(user);
        order.setAmount(amount);
        order.setPaymentMethod(paymentMethod);
        order.setStatus(PaymentOrderStatus.PENDING);
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
    public Boolean ProcessPaymentOrder(PaymentOrder paymentOrder, String paymentId) throws Exception {
        if(paymentOrder.getStatus().equals(PaymentOrderStatus.PENDING)){

            if(paymentOrder.getPaymentMethod().equals(PaymentMethod.STRIPE)){
                StripeClient stripeClient = new StripeClient(stripeSecretKey);
                Session payment = stripeClient.checkout().sessions().retrieve(paymentId);


                Integer amount = Math.toIntExact(payment.getAmountTotal());
                String status = payment.getPaymentStatus();
                if(status.equals("paid")){
                    paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
                    Optional<Wallet> wallet = walletRepository.findById(paymentOrder.getUser().getId());
                    if (wallet.isPresent()) {
                        Wallet userWallet = wallet.get();
                        userWallet.setBalance(userWallet.getBalance().add(BigDecimal.valueOf(amount)));
                        walletRepository.save(userWallet);
                    } else {
                        throw new Exception("User wallet not found");
                    }

                    return true;
                }
                paymentOrder.setStatus(PaymentOrderStatus.FAILED);
                paymentOrderRepository.save(paymentOrder);
                return false;
            }
            paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
            paymentOrderRepository.save(paymentOrder);
            paymentOrderRepository.save(paymentOrder);
            return true;
        }

        return false;
    }


    @Override
    public PaymentResponse createStripePaymentLink(User user, Long amount, Long orderId) throws StripeException {
        apiKey = stripeSecretKey;


        String successUrl = "http://localhost:2345/api/wallet/deposit?order_id=" + orderId;

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl + "&payment_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("http://localhost:2345/api/payment/cancel")
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("cad")
                                .setUnitAmount(amount * 100)
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

