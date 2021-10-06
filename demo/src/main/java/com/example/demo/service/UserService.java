package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.persist.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(String username, String firstName, String lastName, String email) {
        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        return userRepository.save(u);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByUsername(String username) {
        Optional<User> byUsername = userRepository.findByUsername(username);
        if (!byUsername.isPresent())
            throw new RuntimeException("User is not found: "+username);

        return byUsername.get();
    }

}
