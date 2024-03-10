package com.emailProcessor.basedomains.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A SecurityRole.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SecurityRoleDto implements Serializable {
    private String roleId;
    private String roleName;
    private String description;
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SecurityRoleDto)) {
            return false;
        }
        return getRoleId() != null && getRoleId().equals(((SecurityRoleDto) o).getRoleId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SecurityRole{" +
            "roleId=" + getRoleId() +
            ", roleName='" + getRoleName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
