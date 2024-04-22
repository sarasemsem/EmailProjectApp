package com.emailProcessor.emailProcessor.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;

/**
 * A SecurityRole.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "securityRole")
public class SecurityRole implements Serializable {
    @MongoId
    private String roleId;
    private String roleName;
    private String description;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SecurityRole)) {
            return false;
        }
        return getRoleId() != null && getRoleId().equals(((SecurityRole) o).getRoleId());
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
