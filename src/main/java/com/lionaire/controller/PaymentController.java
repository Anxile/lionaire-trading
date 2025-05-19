package com.lionaire.controller;

import com.lionaire.domain.PaymentMethod;
import com.lionaire.model.PaymentOrder;
import com.lionaire.model.User;
import com.lionaire.response.PaymentResponse;
import com.lionaire.service.PaymentService;
import com.lionaire.service.UserService;
import com.stripe.exception.StripeException;
import jdk.jshell.spi.ExecutionControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PaymentController {

    @Autowired
    private UserService userService;

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/api/payment/{paymentMethod}/amount/{amount}")
    public ResponseEntity<PaymentResponse> paymentHandler(
            @PathVariable PaymentMethod paymentMethod,
            @PathVariable Long amount,
            @RequestHeader("Authorization") String jwt) throws ExecutionControl.UserException, StripeException {

        User user = userService.findByJwt(jwt);

        PaymentResponse paymentResponse;

        PaymentOrder order= paymentService.createOrder(user, amount,paymentMethod);

        paymentResponse=paymentService.createStripePaymentLink(user,amount, order.getId());


        return new ResponseEntity<>(paymentResponse, HttpStatus.CREATED);
    }


    @GetMapping("/api/payment/{paymentMethod}/amount/{amount}/check")
    public ResponseEntity<PaymentResponse> paymentCheck(
            @PathVariable PaymentMethod paymentMethod,
            @PathVariable Long amount,
            @RequestHeader("Authorization") String jwt) {

        return null;
    }
}
