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
public class WorkerDto implements Serializable {
    private String workerId;
    private String firstName;
    private String lastName;
    private String email;
    private boolean activated = false;
    private String langKey;
    private String imageUrl;
    private String phoneNbr;
    private String password;
    private Set<SecurityRoleDto> roles = new HashSet<>();

    public WorkerDto(String message) {
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkerDto)) {
            return false;
        }
        return getWorkerId() != null && getWorkerId().equals(((WorkerDto) o).getWorkerId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Worker{" +
            "workerId=" + getWorkerId() +
                ", firstName='" + getFirstName() + "'" +
                ", lastName='" + getLastName() + "'" +
                ", email='" + getEmail() + "'" + ", langKey='" + getLangKey() + "'" +
                ", imageUrl='" + getImageUrl() + "'" +
                ", phoneNbr='" + getPhoneNbr() + "'" +
                ", password='" + getPassword() + "'" +
            "}";
    }
}
