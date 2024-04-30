package com.emailProcessor.basedomains.dto;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

/**
 *  Action.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActionDto implements Serializable {

    private String actionId;
    @NonNull
    private String action;
    private String descriptionAct;
    private Instant actionDate;
    private Instant updatedAt;
    private WorkerDto updatedBy;
    private Boolean affected;
    private Boolean state;
    private List<CategoryDto> categories;
        @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ActionDto)) {
            return false;
        }
        return getActionId() != null && getActionId().equals(((ActionDto) o).getActionId());
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
