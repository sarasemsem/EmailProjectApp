package com.emailProcessor.emailProcessor.controller;

import com.emailProcessor.basedomains.dto.CredentialsDto;
import com.emailProcessor.basedomains.dto.UserDto;
import com.emailProcessor.emailProcessor.configuration.AuthProvider;
import com.emailProcessor.emailProcessor.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {
     private final UserService UserService;
    private final AuthProvider authProvider;

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody @Validated CredentialsDto credentialsDto){
     clearCache();
        UserDto userDto = UserService.login(credentialsDto);
        userDto.setLangKey(authProvider.createToken(userDto));
     return ResponseEntity.ok(userDto);
    }

    @GetMapping("/clear_cache")
    @CacheEvict(value = {"keyword", "sender", "categories", "users", "emails"}, allEntries = true )
    public Map<String, String> clearCache(){
        Map<String, String> response = new HashMap<>();
        response.put("message", "Cache has been cleared");
        return response;
    }
}
