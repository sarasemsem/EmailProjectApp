package com.emailProcessor.basedomains.dto;

import lombok.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A User.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto implements Serializable {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private boolean activated = false;
    private String langKey;
    private String imageUrl;
    private String phoneNbr;
    private String password;
    private Set<SecurityRoleDto> roles = new HashSet<>();

    public UserDto(String message) {
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserDto)) {
            return false;
        }
        return getUserId() != null && getUserId().equals(((UserDto) o).getUserId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
            "userId=" + getUserId() +
                ", firstName='" + getFirstName() + "'" +
                ", lastName='" + getLastName() + "'" +
                ", email='" + getEmail() + "'" + ", langKey='" + getLangKey() + "'" +
                ", imageUrl='" + getImageUrl() + "'" +
                ", phoneNbr='" + getPhoneNbr() + "'" +
                ", password='" + getPassword() + "'" +
            "}";
    }
}
