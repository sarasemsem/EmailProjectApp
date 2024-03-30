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
    private String type;
    private String descriptionAct;
    private Instant actionDate;
    private Instant updatedAt;
    private Boolean affected;
    private Integer treatedBy;
    @DBRef
    private LinkedCategory linkedCategory;

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
            ", type='" + getType() + "'" +
            ", descriptionAct='" + getDescriptionAct() + "'" +
            ", actionDate='" + getActionDate() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", affected='" + getAffected() + "'" +
            ", treatedBy=" + getTreatedBy() +
            "}";
    }
}
