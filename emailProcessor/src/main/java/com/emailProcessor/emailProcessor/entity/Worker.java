package com.emailProcessor.emailProcessor.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.convert.DataSizeUnit;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Worker.
 */
@Document(collection = "worker")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Worker implements Serializable {
    @MongoId
    private String workerId;
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

    public Worker(String id) {
        this.workerId = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Worker)) {
            return false;
        }
        return getWorkerId() != null && getWorkerId().equals(((Worker) o).getWorkerId());
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
                ", email='" + getEmail() + "'" +
                ", langKey='" + getLangKey() + "'" +
                ", imageUrl='" + getImageUrl() + "'" +
            ", phoneNbr='" + getPhoneNbr() + "'" +
            ", password='" + getPassword() + "'" +
            "}";
    }
}
