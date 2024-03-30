package com.emailProcessor.emailProcessor.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

/**
 * A Sender.
 */
@Document(collection = "sender")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Sender implements Serializable {

    @MongoId
    private String senderId;
    private String firstName;
    private String lastName;
    private String gender;
    private String senderEmail;
    private String phoneNbr;
    private String rib;
    private String iban;
    private String address;
    private Integer priority;



    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sender)) {
            return false;
        }
        return getSenderId() != null && getSenderId().equals(((Sender) o).getSenderId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Sender{" +
            "senderId=" + getSenderId() +
            ", name='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", senderEmail='" + getSenderEmail() + "'" +
            ", phoneNbr='" + getPhoneNbr() + "'" +
            ", rib='" + getRib() + "'" +
            ", iban='" + getIban() + "'" +
            ", address='" + getAddress() + "'" +
            ", priority=" + getPriority() +
            "}";
    }
}
