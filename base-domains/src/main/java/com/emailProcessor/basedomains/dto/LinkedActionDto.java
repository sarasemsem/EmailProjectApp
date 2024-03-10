package com.emailProcessor.basedomains.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

/**
 * A LinkedCategory.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LinkedActionDto implements Serializable {

    private String linkCatId;
    private Integer description;
    private Instant actionDate;
    private Instant updatedAt;
    private Boolean affected;
    private Integer treatedBy;
    private ActionDto actionDto;
    private CategoryDto categoryDto;
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "LinkedCategory{" +
            "linkCatId=" + getLinkCatId() +
            ", description=" + getDescription() +
            "}";
    }
}
