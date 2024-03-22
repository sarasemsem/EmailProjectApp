package com.emailProcessor.basedomains.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * A Category.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto implements Serializable {

    private String categoryId;
    private String title;
    private String description;
    private List<String> keywords ;

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    public void Category(String id) {
        this.categoryId = id;
    }
    // prettier-ignore
    @Override
    public String toString() {
        return "Category{" +
            "categoryId=" + getCategoryId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
