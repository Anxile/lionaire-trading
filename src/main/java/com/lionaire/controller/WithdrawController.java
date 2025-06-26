package com.lionaire.controller;

import com.lionaire.model.User;
import com.lionaire.model.Wallet;
import com.lionaire.model.WalletTransaction;
import com.lionaire.model.Withdraw;
import com.lionaire.service.UserService;
import com.lionaire.service.WalletService;
import com.lionaire.service.WalletTransactionService;
import com.lionaire.service.WithdrawService;
import com.lionaire.domain.WalletTransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class WithdrawController {
    @Autowired
    private WithdrawService WithdrawService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserService userService;

    @Autowired
    private WalletTransactionService walletTransactionService;

    @PostMapping("/api/Withdraw/{amount}")
    public ResponseEntity<?> WithdrawRequest(
            @PathVariable Long amount,
            @RequestHeader("Authorization")String jwt) throws Exception {
        User user=userService.findByJwt(jwt);
        Wallet userWallet=walletService.getUserWallet(user);

        Withdraw Withdraw=WithdrawService.requestWithdraw(amount,user);
        walletService.addBalance(userWallet, -Withdraw.getAmount());

        WalletTransaction walletTransaction = walletTransactionService.createTransaction(
                userWallet,
                WalletTransactionType.WITHDRAWAL,null,
                "bank account Withdraw",
                Withdraw.getAmount()
        );

        return new ResponseEntity<>(Withdraw, HttpStatus.OK);
    }

    @PatchMapping("/api/admin/Withdraw/{id}/proceed/{accept}")
    public ResponseEntity<?> proceedWithdraw(
            @PathVariable Long id,
            @PathVariable boolean accept,
            @RequestHeader("Authorization")String jwt) throws Exception {
        User user=userService.findByJwt(jwt);

        Withdraw Withdraw=WithdrawService.processWithdrawal(id,accept);

        Wallet userWallet=walletService.getUserWallet(user);
        if(!accept){
            walletService.addBalance(userWallet, Withdraw.getAmount());
        }

        return new ResponseEntity<>(Withdraw, HttpStatus.OK);
    }

    @GetMapping("/api/Withdraw")
    public ResponseEntity<List<Withdraw>> getWithdrawHistory(

            @RequestHeader("Authorization")String jwt) throws Exception {
        User user=userService.findByJwt(jwt);

        List<Withdraw> Withdraw=WithdrawService.getUsersWithdrawHistory(user);

        return new ResponseEntity<>(Withdraw, HttpStatus.OK);
    }

    @GetMapping("/api/admin/Withdraw")
    public ResponseEntity<List<Withdraw>> getAllWithdrawRequest(

            @RequestHeader("Authorization")String jwt) throws Exception {
        User user=userService.findByJwt(jwt);

        List<Withdraw> Withdraw=WithdrawService.getAllWithdrawalRequest();

        return new ResponseEntity<>(Withdraw, HttpStatus.OK);
    }
}
