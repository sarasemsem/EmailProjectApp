package com.emailProcessor.emailProcessor.service.impl;

import com.emailProcessor.basedomains.dto.SecurityRoleDto;
import com.emailProcessor.emailProcessor.entity.SecurityRole;
import com.emailProcessor.emailProcessor.repository.SecurityRoleRepository;
import com.emailProcessor.emailProcessor.service.SecurityRoleService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SecurityRoleServiceImp implements SecurityRoleService {
    private final Logger log = LoggerFactory.getLogger(SecurityRoleServiceImp.class);
    private final ModelMapper modelMapper;
    private final SecurityRoleRepository securityRoleRepository;
    @Override
    public SecurityRole saveSecurityRole(SecurityRole securityRole) {
        log.debug("Request to save SecurityRole : {}", securityRole);
         SecurityRole savedRole = securityRoleRepository.save(securityRole);
        return savedRole;
    }

    @Override
    @Transactional(readOnly = true)
    public SecurityRoleDto updateSecurityRole(SecurityRole securityRole) {
        log.debug("Request to update SecurityRole : {}", securityRole);
        return modelMapper.map(securityRoleRepository.save(securityRole), SecurityRoleDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SecurityRole> partialUpdateSecurityRole(SecurityRole securityRole) {
        log.debug("Request to partially update SecurityRole : {}", securityRole);
        return securityRoleRepository
                .findById(securityRole.getRoleId())
                .map(existingSecurityRole -> {
                    if (securityRole.getRoleName() != null) {
                        existingSecurityRole.setRoleName(securityRole.getRoleName());
                    }
                    if (securityRole.getDescription() != null) {
                        existingSecurityRole.setDescription(securityRole.getDescription());
                    }

                    return existingSecurityRole;
                })
                .map(securityRoleRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SecurityRole> findAllSecurityRoles() {
        log.debug("Request to get all SecurityRoles");
        return securityRoleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SecurityRoleDto> findOneSecurityRole(String id) {
        log.debug("Request to get SecurityRole : {}", id);
        Optional<SecurityRole> role = securityRoleRepository.findById(id) ;
        return Optional.of(modelMapper.map(role, SecurityRoleDto.class));
    }

    @Override
    public ResponseEntity<Void> deleteSecurityRole(String id) {
        // Check if the SecurityRole exists before attempting to delete
        if (securityRoleRepository.existsById(id)) {
            securityRoleRepository.deleteById(id);
            return ResponseEntity.noContent().build(); // Indicate successful deletion with no body
        } else {
            return ResponseEntity.notFound().build(); // Indicate that the resource was not found
        }
    }
}
