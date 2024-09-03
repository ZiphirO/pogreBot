package com.ziphiro.podBot.Services;

import com.ziphiro.podBot.DTO.UserDTO;
import com.ziphiro.podBot.entityes.User;
import com.ziphiro.podBot.repositoyes.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private final UserRepository repository;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    public User initUser(User user){
        return repository.save(user);
    }

    public boolean checkUser(String name){
        boolean check = false;
        if (repository.findByName(name).isPresent()){
            check = true;
        }
        return check;
    }

}
