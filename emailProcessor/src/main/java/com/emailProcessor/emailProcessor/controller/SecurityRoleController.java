package com.emailProcessor.emailProcessor.controller;

import com.emailProcessor.basedomains.dto.CustomResponse;
import com.emailProcessor.basedomains.dto.SecurityRoleDto;
import com.emailProcessor.emailProcessor.controller.errors.BadRequestException;
import com.emailProcessor.emailProcessor.entity.SecurityRole;
import com.emailProcessor.emailProcessor.repository.SecurityRoleRepository;
import com.emailProcessor.emailProcessor.service.SecurityRoleService;

import com.mongodb.lang.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link com.emailProcessor.emailProcessor.entity.SecurityRole}.
 */
@RestController
@RequestMapping("/api/v1/SecurityRole")
public class SecurityRoleController {

    private final Logger log = LoggerFactory.getLogger(SecurityRoleController.class);

    private static final String ENTITY_NAME = "securityRole";

    private final SecurityRoleService securityRoleService;

    private final SecurityRoleRepository securityRoleRepository;

    public SecurityRoleController(SecurityRoleService securityRoleService, SecurityRoleRepository securityRoleRepository) {
        this.securityRoleService = securityRoleService;
        this.securityRoleRepository = securityRoleRepository;
    }

    /**
     * {@code POST  /security-roles} : Create a new securityRole.
     *
     * @param securityRole the securityRole to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new securityRole, or with status {@code 400 (Bad Request)} if the securityRole has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CustomResponse> createSecurityRole(@Validated @RequestBody SecurityRole securityRole) throws URISyntaxException {
        log.debug("REST request to save SecurityRole : {}", securityRole);
        if (securityRole.getRoleId() != null) {
            throw new BadRequestException("A new securityRole cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SecurityRole result = securityRoleService.saveSecurityRole(securityRole);
        // Create a custom response object with both data and HTTP status
        CustomResponse customResponse = new CustomResponse(result, HttpStatus.CREATED.value(), "Role saved successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(customResponse);
    }

    /**
     * {@code PUT  /security-roles/:roleId} : Updates an existing securityRole.
     *
     * @param roleId the id of the securityRole to save.
     * @param securityRole the securityRole to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated securityRole,
     * or with status {@code 400 (Bad Request)} if the securityRole is not valid,
     * or with status {@code 500 (Internal Server Error)} if the securityRole couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{roleId}")
    public ResponseEntity<SecurityRoleDto> updateSecurityRole(
        @PathVariable(value = "roleId", required = false) final String roleId,
        @Validated @RequestBody SecurityRole securityRole
    ) throws URISyntaxException {
        log.debug("REST request to update SecurityRole : {}, {}", roleId, securityRole);
        if (securityRole.getRoleId() == null) {
            throw new BadRequestException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(roleId, securityRole.getRoleId())) {
            throw new BadRequestException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!securityRoleRepository.existsById(roleId)) {
            throw new BadRequestException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SecurityRoleDto result = securityRoleService.updateSecurityRole(securityRole);
        return ResponseEntity
                .ok()
                .body(result);
    }

    /**
     * {@code PATCH  /security-roles/:roleId} : Partial updates given fields of an existing securityRole, field will ignore if it is null
     *
     * @param roleId       the id of the securityRole to save.
     * @param securityRole the securityRole to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated securityRole,
     * or with status {@code 400 (Bad Request)} if the securityRole is not valid,
     * or with status {@code 404 (Not Found)} if the securityRole is not found,
     * or with status {@code 500 (Internal Server Error)} if the securityRole couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{roleId}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Optional<SecurityRole>> partialUpdateSecurityRole(
        @PathVariable(value = "roleId", required = false) final String roleId,
        @NonNull @RequestBody SecurityRole securityRole
    ) throws URISyntaxException {
        log.debug("REST request to partial update SecurityRole partially : {}, {}", roleId, securityRole);
        if (securityRole.getRoleId() == null) {
            throw new BadRequestException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(roleId, securityRole.getRoleId())) {
            throw new BadRequestException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!securityRoleRepository.existsById(roleId)) {
            throw new BadRequestException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SecurityRole> result = securityRoleService.partialUpdateSecurityRole(securityRole);

        return ResponseEntity.ok(Optional.of(result.get()));
    }

    /**
     * {@code GET  /security-roles} : get all the securityRoles.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of securityRoles in body.
     */
    @GetMapping("")
    public List<SecurityRole> getAllSecurityRoles() {
        log.debug("REST request to get all SecurityRoles");
        return securityRoleService.findAllSecurityRoles();
    }

    /**
     * {@code GET  /security-roles/:id} : get the "id" securityRole.
     *
     * @param id the id of the securityRole to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the securityRole, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SecurityRoleDto> getSecurityRole(@PathVariable("id") String id) {
        log.debug("REST request to get SecurityRole : {}", id);
        Optional<SecurityRoleDto> securityRole = securityRoleService.findOneSecurityRole(id);
        return securityRole
                .map(role -> ResponseEntity.ok().body(role))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * {@code DELETE  /security-roles/:id} : delete the "id" securityRole.
     *
     * @param id the id of the securityRole to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String>  deleteSecurityRole(@PathVariable("id") String id) {
        log.debug("REST request to delete SecurityRole : {}", id);
        clearCache();
         securityRoleService.deleteSecurityRole(id);
        clearCache();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/clear_cache")
    @CacheEvict(value = "securityRole", allEntries = true )
    public String clearCache(){
        return "Cache has been cleared";
    }
}
