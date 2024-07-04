package com.emailProcessor.emailProcessor.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Worker.
 */
@Document(collection = "user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    @MongoId
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private boolean activated = false;
    private String langKey;
    private String imageUrl;
    private String phoneNbr;
    private String password;
    @DBRef
    private Set<SecurityRole> roles = new HashSet<>();


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        return getUserId() != null && getUserId().equals(((User) o).getUserId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
            "workerId=" + getUserId() +
            ", firstName='" + getFirstName() + "'" +
                ", lastName='" + getLastName() + "'" +
                ", email='" + getEmail() + "'" +
                ", langKey='" + getLangKey() + "'" +
                ", imageUrl='" + getImageUrl() + "'" +
            ", phoneNbr='" + getPhoneNbr() + "'" +
            ", password='" + getPassword() + "'" +
            "}";
    }
}
