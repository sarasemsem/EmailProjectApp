package com.emailProcessor.emailProcessor.service.impl;

import com.emailProcessor.basedomains.dto.CredentialsDto;
import com.emailProcessor.basedomains.dto.SenderDto;
import com.emailProcessor.basedomains.dto.WorkerDto;
import com.emailProcessor.emailProcessor.controller.errors.AppExecption;
import com.emailProcessor.emailProcessor.entity.SecurityRole;
import com.emailProcessor.emailProcessor.entity.Sender;
import com.emailProcessor.emailProcessor.entity.Worker;
import com.emailProcessor.emailProcessor.mappers.WorkerMapper;
import com.emailProcessor.emailProcessor.repository.SenderRepository;
import com.emailProcessor.emailProcessor.repository.WorkerRepository;
import com.emailProcessor.emailProcessor.service.WorkerService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.CharBuffer;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class WorkerServiceImp implements WorkerService {
    private final Logger log = LoggerFactory.getLogger(SenderServiceImp.class);

    private final WorkerRepository workerRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    @Override
    public WorkerDto login(CredentialsDto credentialsDto) {
        System.out.println("credentials are :"+credentialsDto);
        Worker worker = workerRepository.findByEmail(credentialsDto.email())
                .orElseThrow(() -> {
                    return new AppExecption("Unknown worker",HttpStatus.NOT_FOUND);
                });
        if (passwordEncoder.matches(CharBuffer.wrap(credentialsDto.password()),
                worker.getPassword())) {
            return modelMapper.map(worker, WorkerDto.class);
        }
        throw new AppExecption("Invalid password",HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<String> saveWorker(Worker worker) {
        log.debug("Request to save Worker: {}", worker);
        try {
            Optional<Worker> findWorker = workerRepository.findByEmail(worker.getEmail());
            if (findWorker.isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
            }
            Worker toSaveWorker = modelMapper.map(worker, Worker.class);
            toSaveWorker.setPassword(passwordEncoder.encode(worker.getPassword()));
            Worker savedWorker = workerRepository.save(toSaveWorker);
            if (savedWorker.getWorkerId() != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body("Worker saved successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to insert Worker");
            }
        } catch (DataIntegrityViolationException ex) {
            log.error("Error saving worker", ex);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error saving worker", ex);
        }
    }

    @Override
    public WorkerDto updateWorker(Worker worker) {
        log.debug("Request to update Worker : {}", worker);
        Worker updatedWorker = workerRepository.save(worker);
        return modelMapper.map(updatedWorker, WorkerDto.class);
    }

    @Override
    public Optional<WorkerDto> partialUpdateWorker(Worker worker) {
        log.debug("Request to partially update Worker : {}", worker);
        return workerRepository
                .findById(worker.getWorkerId())
                .map(existingWorker -> {
                    if (worker.getFirstName() != null) {
                        existingWorker.setFirstName(worker.getFirstName());
                    }
                    if (worker.getEmail() != null) {
                        existingWorker.setEmail(worker.getEmail());
                    }
                    if (worker.getRoles() != null) {
                        // Assuming that getRoles() returns a Set<SecurityRole>
                        existingWorker.getRoles().clear(); // Clear existing roles
                        existingWorker.getRoles().addAll(worker.getRoles());
                    }
                    if (worker.getPhoneNbr() != null) {
                        existingWorker.setPhoneNbr(worker.getPhoneNbr());
                    }
                    if (worker.getPassword() != null) {
                        existingWorker.setPassword(worker.getPassword());
                    }

                    return existingWorker;
                })
                .map(workerRepository::save)
                .map(updatedWorker -> modelMapper.map(updatedWorker, WorkerDto.class));
    }


    @Override
    public List<Worker> findAllWorkers() {
        log.debug("Request to get all workers");
        return workerRepository.findAll();
    }
    @Override
    public WorkerDto findByLogin(String login) {
        Optional<Worker> worker = workerRepository.findByEmail(login);
        return modelMapper.map(worker, WorkerDto.class);
    }
    @Override
    public Optional<WorkerDto> findOneWorker(String id) {
        log.debug("Request to get Sender : {}", id);
        Optional<Worker> workerOptional = workerRepository.findById(id);

        if (workerOptional.isPresent()) {
            Worker worker = workerOptional.get();
            WorkerDto workerDto = modelMapper.map(worker, WorkerDto.class);
            return Optional.of(workerDto);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void deleteWorker(String id) {
        log.debug("Request to delete Sender : {}", id);
        workerRepository.deleteById(id);
    }
}
