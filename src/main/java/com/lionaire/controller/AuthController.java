package com.lionaire.controller;

import com.lionaire.config.JwtProvider;
import com.lionaire.model.User;
import com.lionaire.repository.UserRepository;
import com.lionaire.response.AuthResponse;
import com.lionaire.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> register(@RequestBody User user) throws Exception {
        User emailExist = userRepository.findByEmail((user.getEmail()));

        if(emailExist != null) throw new Exception("email already exists");

        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setName(user.getName());
        newUser.setPassword(user.getPassword());

        User savedUser = userRepository.save(newUser);


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
}
