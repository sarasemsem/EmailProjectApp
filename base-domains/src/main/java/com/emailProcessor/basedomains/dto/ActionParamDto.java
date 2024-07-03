package com.emailProcessor.basedomains.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActionParamDto implements Serializable {
    private String actionParamId;
    private ActionDto action;
    private Map<String, String> params;
    private Boolean affected;
    private Instant actionDate;

    public ActionParamDto(ActionDto action, Map<String, String> params) {
        this.action = action;
        this.params = params;
    }

    @Override
    public String toString() {
        return "ActionParamDto{" +
                "actionParamId='" + actionParamId + '\'' +
                ", action=" + action +
                ", params=" + params +
                '}';
    }
}