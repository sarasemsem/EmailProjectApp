package com.emailProcessor.emailProcessor.service;
import com.emailProcessor.basedomains.dto.SecurityRoleDto;
import com.emailProcessor.emailProcessor.entity.SecurityRole;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing
 */
@Service
public interface SecurityRoleService {
    /**
     * Save a securityRole.
     *
     * @param securityRole the entity to save.
     * @return the persisted entity.
     */
    SecurityRole saveSecurityRole(SecurityRole securityRole);

    /**
     * Updates a securityRole.
     *
     * @param securityRole the entity to update.
     * @return the persisted entity.
     */
    SecurityRoleDto updateSecurityRole(SecurityRole securityRole);

    /**
     * Partially updates a securityRole.
     *
     * @param securityRole the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SecurityRole> partialUpdateSecurityRole(SecurityRole securityRole);

    /**
     * Get all the securityRoles.
     *
     * @return the list of entities.
     */
    List<SecurityRole> findAllSecurityRoles();

    /**
     * Get the "id" securityRole.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SecurityRoleDto> findOneSecurityRole(String id);

    /**
     * Delete the "id" securityRole.
     *
     * @param id the id of the entity.
     */
    ResponseEntity<Void> deleteSecurityRole(String id);
}
