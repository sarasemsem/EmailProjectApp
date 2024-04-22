package com.emailProcessor.emailProcessor.controller;

import com.emailProcessor.basedomains.dto.CredentialsDto;
import com.emailProcessor.basedomains.dto.WorkerDto;
import com.emailProcessor.emailProcessor.configuration.AuthProvider;
import com.emailProcessor.emailProcessor.service.WorkerService;
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
     private final WorkerService workerService;
    private final AuthProvider authProvider;

    @PostMapping("/login")
    public ResponseEntity<WorkerDto> login(@RequestBody @Validated CredentialsDto credentialsDto){
     clearCache();
     WorkerDto workerDto = workerService.login(credentialsDto);
     workerDto.setLangKey(authProvider.createToken(workerDto));
     return ResponseEntity.ok(workerDto);
    }

    @GetMapping("/clear_cache")
    @CacheEvict(value = {"keyword", "sender", "categories", "workers", "emails"}, allEntries = true )
    public Map<String, String> clearCache(){
        Map<String, String> response = new HashMap<>();
        response.put("message", "Cache has been cleared");
        return response;
    }
}
