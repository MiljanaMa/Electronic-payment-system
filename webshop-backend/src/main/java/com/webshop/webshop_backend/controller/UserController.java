package com.webshop.webshop_backend.controller;

import com.webshop.webshop_backend.dto.LoginRequestDTO;
import com.webshop.webshop_backend.model.User;
import com.webshop.webshop_backend.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/users")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("")
    public List<User> getUsers(){
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") Long id){
        return userService.getUser(id);
    }

    @PutMapping("/{id}")
    public User updateUser(@RequestBody() User user, @PathVariable("id") Long id){
        return userService.updateUser(user);
    }

    @PostMapping("/register")
    public ResponseEntity<User> newUser(@RequestBody() User user){
        User newUser = userService.addUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") Long id){
        userService.deleteUser(id);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO loginRequest, HttpSession session) {
        try{
            boolean isAuthenticated = userService.authenticate(loginRequest.getUsername(),loginRequest.getPassword());

            if (isAuthenticated){
                session.setAttribute("user", loginRequest.getUsername());
                return ResponseEntity.ok("Login was successful!");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unknown error occurred");
        }
    }
}