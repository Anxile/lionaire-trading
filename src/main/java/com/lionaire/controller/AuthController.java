package com.lionaire.controller;

import com.lionaire.config.JwtProvider;
import com.lionaire.model.TwoFactorOTP;
import com.lionaire.model.User;
import com.lionaire.model.Wallet;
import com.lionaire.repository.UserRepository;
import com.lionaire.repository.WalletRepository;
import com.lionaire.response.AuthResponse;
import com.lionaire.service.*;
import com.lionaire.utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private TwoFactorOtpService twoFactorOtpService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private WatchlistService watchlistService;

    @Autowired
    private WalletRepository walletRepository;



    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> register(@RequestBody User user) throws Exception {
        User emailExist = userRepository.findByEmail((user.getEmail()));

        if(emailExist != null) throw new Exception("email already exists");

        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setName(user.getName());
        newUser.setPassword(user.getPassword());
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(0));
        wallet.setUser(newUser);


        User savedUser = userRepository.save(newUser);
        walletRepository.save(wallet);

        watchlistService.createWatchlist(savedUser);

        Authentication authentication=new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                user.getPassword()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = JwtProvider.generateToken(authentication);

        AuthResponse res = new AuthResponse();
        res.setJwt(jwt);
        res.setStatus(true);
        res.setMessage("register success");

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> login(@RequestBody User user) throws Exception {

        String userName = user.getEmail();
        String password = user.getPassword();

        Authentication authentication = authenticate(userName, password);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = JwtProvider.generateToken(authentication);

        User authUser = userRepository.findByEmail(userName);

        if(user.getTwoFactorAuth().isEnabled()){
            AuthResponse res = new AuthResponse();
            res.setMessage("Two factor authentication is enabled");
            res.setTwoFactorAuthEnabled(true);
            String opt = OtpUtils.generateOtp();

            TwoFactorOTP oldTwoFactorOTP = twoFactorOtpService.findByUser(authUser.getId());
            if(oldTwoFactorOTP!=null){
                twoFactorOtpService.delete(oldTwoFactorOTP);    //refresh OTP
            }
            TwoFactorOTP newTwoFactorOTP = twoFactorOtpService.createTwoFactorOtp(
                    authUser,opt,jwt
            );

            emailService.sendVerificationEmail(userName,opt);

            res.setSession(newTwoFactorOTP.getId());
            return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
        }

        AuthResponse res = new AuthResponse();
        res.setJwt(jwt);
        res.setStatus(true);
        res.setMessage("login success");

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    private Authentication authenticate(String userName, String password) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userName);

        if(userDetails==null){
            throw new BadCredentialsException("user not found");
        }
        else if(!userDetails.getPassword().equals(password)){
            throw new BadCredentialsException("wrong password");
        }
        else{
            return new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
        }
    }



    @PostMapping("/verify-otp/{otp}")
    public ResponseEntity<AuthResponse> verifyOtp(
            @PathVariable String otp, @RequestParam String id) throws Exception {
        TwoFactorOTP twoFactorOTP = twoFactorOtpService.findById(id);

        if(twoFactorOtpService.verifyOtp(twoFactorOTP,otp)){
            AuthResponse res = new AuthResponse();
            res.setMessage("otp verified");
            res.setStatus(true);
            res.setTwoFactorAuthEnabled(true);
            res.setJwt(twoFactorOTP.getJwt());
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        throw new Exception("invalid otp");
    }
}
