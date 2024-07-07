package com.example.SocialNetwork.Security;

import com.example.SocialNetwork.Domain.User;
import com.example.SocialNetwork.Service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Optional;
public class Authentication {

    private final UserService userService = UserService.getInstance();
    private  BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    public Optional<User> attemptAuthentication(String email, String password){
        Optional<User> u = userService.findAccount(email);
        if(u.isEmpty())
            return Optional.empty();
        if(bCryptPasswordEncoder.matches(password,u.get().getPassword()))
            return u;
        return Optional.empty();
    }
}
