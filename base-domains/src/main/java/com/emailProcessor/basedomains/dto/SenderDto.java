package com.emailProcessor.basedomains.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * A Sender.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SenderDto implements Serializable {

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
        if (!(o instanceof SenderDto)) {
            return false;
        }
        return getSenderId() != null && getSenderId().equals(((SenderDto) o).getSenderId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Sender{" +
            "senderId=" + getSenderId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", gender='" + getGender() + "'" +
            ", senderEmail='" + getSenderEmail() + "'" +
            ", phoneNbr='" + getPhoneNbr() + "'" +
            ", rib='" + getRib() + "'" +
            ", iban='" + getIban() + "'" +
            ", address='" + getAddress() + "'" +
            ", priority=" + getPriority() +
            "}";
    }
}
