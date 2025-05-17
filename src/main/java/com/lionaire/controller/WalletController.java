package com.lionaire.controller;

import com.lionaire.model.Order;
import com.lionaire.model.User;
import com.lionaire.model.Wallet;
import com.lionaire.model.WalletTransaction;
import com.lionaire.service.OrderService;
import com.lionaire.service.UserService;
import com.lionaire.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/api/wallet")
    public ResponseEntity<Wallet> getUserWallet(@RequestHeader("Authorization") String jwt) {
        User user = userService.findByJwt(jwt);

        Wallet wallet = walletService.getUserWallet(user);

        return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
    }

    @PutMapping("/api/wallet/{walletId}/transfer")
    public ResponseEntity<Wallet> walletTransfer(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long walletId,
            @RequestBody WalletTransaction walletTransaction
            ) {
        User user = userService.findByJwt(jwt);
        Wallet toWallet = walletService.findWalletById(walletId);
        Wallet fromWallet = walletService.walletTransfer(
                user,
                toWallet.getUser(),
                walletTransaction.getAmount()
        );
        return new ResponseEntity<>(fromWallet, HttpStatus.ACCEPTED);
    }

    @PutMapping("/api/wallet/order/{orderId}/pay")
    public ResponseEntity<Wallet> payOrder(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long orderId
    ) throws Exception {
        User user = userService.findByJwt(jwt);
        Order order = orderService.getOrderById(orderId);
        Wallet wallet = walletService.payOrder(order, user);

        return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
    }
}
