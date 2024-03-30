package com.emailProcessor.emailProcessor.entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;

/**
 * A Category.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "category")
public class Category implements Serializable {

    @MongoId
    private String categoryId;
    private String title;
    private String description;
    private List<String> keywords ;

    //@DBRef
    //private ActionDto linkedActionDto;

    public Category(String id) {
        this.categoryId = id;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
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
