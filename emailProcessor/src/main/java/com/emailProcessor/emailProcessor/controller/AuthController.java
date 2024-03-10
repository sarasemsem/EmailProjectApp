package com.emailProcessor.emailProcessor.controller;

import com.emailProcessor.basedomains.dto.CredentialsDto;
import com.emailProcessor.basedomains.dto.WorkerDto;
import com.emailProcessor.emailProcessor.configuration.AuthProvider;
import com.emailProcessor.emailProcessor.service.WorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
     private final WorkerService workerService;
    private final AuthProvider authProvider;

    @PostMapping("/login")
    public ResponseEntity<WorkerDto> login(@RequestBody @Validated CredentialsDto credentialsDto){
     WorkerDto workerDto = workerService.login(credentialsDto);
     workerDto.setLangKey(authProvider.createToken(workerDto));
     return ResponseEntity.ok(workerDto);
    }
}
