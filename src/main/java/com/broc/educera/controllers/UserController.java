package com.broc.educera.controllers;

import com.broc.educera.dtos.TokenResponse;
import com.broc.educera.dtos.UserLoginRequest;
import com.broc.educera.entities.User;
import com.broc.educera.entities.UserType;
import com.broc.educera.services.UserService;
import com.broc.educera.utils.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @GetMapping
    public ResponseEntity<?> all(@RequestParam("page") Integer pageNumber,
                                 @RequestParam("size") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.unsorted());
        return ResponseEntity.ok().body(userService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> one(@PathVariable Long id) {
        User user = userService.findById(id).orElseThrow();
        return ResponseEntity.ok().body(user);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@Valid @RequestBody User user, @AuthenticationPrincipal User currentUser) {
        if(currentUser.getUserType().equals(UserType.ADMIN)) {
            return ResponseEntity.ok().body(userService.save(user));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping(value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> modify(@RequestBody @Valid User newUser,
                                    @PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        if(currentUser.getUserType().equals(UserType.ADMIN)) {
            User user = userService.findById(id).orElseThrow();
            user.setEmail(newUser.getEmail());
            user.setPassword(passwordEncoder.encode(newUser.getPassword()));
            user.setFirstname(newUser.getFirstname());
            user.setLastname(newUser.getLastname());
            user.setUserType(newUser.getUserType());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        if(currentUser.getUserType().equals(UserType.ADMIN)) {
            User user = userService.findById(id).orElseThrow();
            userService.deleteById(user.getId());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping(value = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLoginRequest.getEmail(),
                    userLoginRequest.getPassword()));
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong credentials");
        }
        User user =  userService.findByEmail(userLoginRequest.getEmail()).orElseThrow();
        TokenResponse token = TokenResponse.builder()
                .token(jwtUtil.generateToken(user)).build();
        return ResponseEntity.ok().body(token);
    }

}
