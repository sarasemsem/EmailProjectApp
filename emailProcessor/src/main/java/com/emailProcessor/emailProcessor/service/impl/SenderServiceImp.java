package com.emailProcessor.emailProcessor.service.impl;

import com.emailProcessor.basedomains.dto.SenderDto;
import com.emailProcessor.emailProcessor.entity.Sender;
import com.emailProcessor.emailProcessor.repository.SenderRepository;
import com.emailProcessor.emailProcessor.service.SenderService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SenderServiceImp implements SenderService {

    private final Logger log = LoggerFactory.getLogger(SenderServiceImp.class);

    private final SenderRepository senderRepository;
    private final ModelMapper modelMapper;
    @Override
    @Transactional
    public ResponseEntity<String> saveSender(SenderDto senderDto) {
        log.debug("Request to save Sender : {}", senderDto);
        Sender savedSender = senderRepository.save(modelMapper.map(senderDto, Sender.class));
        if (savedSender.getSenderId() != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Sender saved successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to insert the Sender");
        }
    }
    @Override
    public SenderDto updateSender(SenderDto senderDto) {
        log.debug("Request to update Sender : {}", senderDto);
        Sender sender = senderRepository.save(modelMapper.map(senderDto, Sender.class));
        return  modelMapper.map(sender, SenderDto.class);
    }

    @Override
    public Optional<Sender> partialUpdateSender(Sender sender) {
        log.debug("Request to partially update Sender : {}", sender);
        System.out.println("Request to partially update Sender : "+ sender);
        return senderRepository
                .findById(sender.getSenderId())
                .map(existingSender -> {
                    if (sender.getFirstName() != null) {
                        existingSender.setFirstName(sender.getFirstName());
                    }
                    if (sender.getLastName() != null) {
                        existingSender.setLastName(sender.getLastName());
                    }
                    if (sender.getSenderEmail() != null) {
                        existingSender.setSenderEmail(sender.getSenderEmail());
                    }
                    if (sender.getPhoneNbr() != null) {
                        existingSender.setPhoneNbr(sender.getPhoneNbr());
                    }
                    if (sender.getRib() != null) {
                        existingSender.setRib(sender.getRib());
                    }
                    if (sender.getIban() != null) {
                        existingSender.setIban(sender.getIban());
                    }
                    if (sender.getAddress() != null) {
                        existingSender.setAddress(sender.getAddress());
                    }
                    if (sender.getPriority() != null) {
                        existingSender.setPriority(sender.getPriority());
                    }

                    return existingSender;
                })
                .map(senderRepository::save);
    }



    @Override
    @Transactional(readOnly = true)
    public List<Sender> findAllSenders() {
        log.debug("Request to get all Senders");
        return senderRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Sender> findOneSender(String id) {
        log.debug("Request to get Sender : {}", id);
        Optional<Sender> sender= senderRepository.findById(id);
        return sender;
    }

    @Override
    public ResponseEntity<Void> deleteSender(String id) {
        log.debug("Request to delete Sender : {}", id);
        if (senderRepository.existsById(id)) {
            senderRepository.deleteById(id);
            return ResponseEntity.noContent().build(); // Indicate successful deletion with no body
        } else {
            return ResponseEntity.notFound().build(); // Indicate that the resource was not found
        }
    }
}
