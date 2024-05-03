package com.emailProcessor.emailProcessor.entity;
import com.mongodb.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;
import java.time.Instant;

/**
 *  Action.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "action")
public class Action implements Serializable {

    @MongoId
    private String actionId;
    @NonNull
    private String action;
    private String descriptionAct;
    private Instant actionDate;
    private Instant updatedAt;
    @DBRef
    private Worker updatedBy;
    private Boolean affected;
    private Boolean state;
    @DBRef
    private RelatedData relatedData;

        @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Action)) {
            return false;
        }
        return getActionId() != null && getActionId().equals(((Action) o).getActionId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Action{" +
            "actionId=" + getActionId() +
            ", type='" + getAction() + "'" +
            ", descriptionAct='" + getDescriptionAct() + "'" +
            ", actionDate='" + getActionDate() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", affected='" + getAffected() + "'" +
            ", treatedBy=" + getUpdatedBy() +
            "}";
    }
}
