package com.authcheck.services.impl;

import com.authcheck.dto.JwtAuthenticationResponse;
import com.authcheck.dto.RefreshTokenRequest;
import com.authcheck.dto.SignInRequest;
import com.authcheck.dto.SignUpRequest;
import com.authcheck.entities.Role;
import com.authcheck.entities.User;
import com.authcheck.repository.UserRepository;
import com.authcheck.services.AuthenticationService;
import com.authcheck.services.JWTService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    @Override
    public User signup(SignUpRequest signUpRequest){
        User user  = new User();


        user.setUsername(signUpRequest.getUsername());
        user.setPhone(signUpRequest.getPhone());
        user.setEmail(signUpRequest.getEmail());
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
       return userRepository.save(user);
    }


    @Override
    public JwtAuthenticationResponse signIn(SignInRequest signInRequest){

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken
                (
                signInRequest.getEmail(),
                 signInRequest.getPassword()
                ));

        var user = userRepository.findByEmail(signInRequest.getEmail())
                .orElseThrow(()->new IllegalArgumentException("Invalid userName or password"));
        System.out.println("exception occured");

        var jwt = jwtService.generateToken(user);
        System.out.println("nt coming");
        var refreshToken =  jwtService.generateRefreshToken(new HashMap<>(),user);

        JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();
        jwtAuthenticationResponse.setToken(jwt);
        jwtAuthenticationResponse.setRefreshToken(refreshToken);
        jwtAuthenticationResponse.setUser(user);

        return jwtAuthenticationResponse;
    }

    @Override
    public JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest){
        String userEmail = jwtService.extractUserName(refreshTokenRequest.getToken());
        User user = userRepository.findByEmail(userEmail).orElseThrow();

        if(jwtService.isTokenValid(refreshTokenRequest.getToken(),user)){
            var jwt = jwtService.generateToken(user);

            JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse();

            jwtAuthenticationResponse.setToken(jwt);
            jwtAuthenticationResponse.setRefreshToken(refreshTokenRequest.getToken());
            jwtAuthenticationResponse.setUser(user);
            return jwtAuthenticationResponse;

        }
        return null;
    }
}
