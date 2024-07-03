package com.emailProcessor.basedomains.dto;

import lombok.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Worker.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User1Dto implements Serializable {
    private String firstName;
    private String lastName;
    private String email;
    private boolean activated = false;
    private String imageUrl;
    private String langKey;
    private String phoneNbr;
    private String password;
    private Set<SecurityRoleDto> roles = new HashSet<>();

    public User1Dto(String message) {
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + getFirstName() + "'" +
                ", lastName='" + getLastName() + "'" +
                ", email='" + getEmail() + "'" +
                ", imageUrl='" + getImageUrl() + "'" +
                ", phoneNbr='" + getPhoneNbr() + "'" +
            "}";
    }
}
