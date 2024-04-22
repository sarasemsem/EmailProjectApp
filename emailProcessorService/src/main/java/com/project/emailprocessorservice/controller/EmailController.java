package com.project.emailprocessorservice.controller;
import com.emailProcessor.basedomains.dto.EmailDto;
import com.emailProcessor.basedomains.dto.EmailEvent;
import com.project.emailprocessorservice.service.EmailProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/email")
public class EmailController {
    @Autowired
    private EmailProducer emailProducer;

@PostMapping("/saveEmail")
    public String palceOrder(@RequestBody EmailDto emailDto)
    {
        EmailEvent emailEvent =new EmailEvent();
        emailEvent.setStatus("PENDING");
        emailEvent.setMessage("email satus is in pending");
        emailEvent.setEmail(emailDto);

        emailProducer.sendMessage(emailEvent);
        return  "email send successfully ...";
    }
}
