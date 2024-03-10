package com.emailProcessor.basedomains.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

/**
 *  Action.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActionDto implements Serializable {

    private String actionId;
    private String action;
    private String descriptionAct;

    private LinkedActionDto linkedActionDto;

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
            "}";
    }
}
