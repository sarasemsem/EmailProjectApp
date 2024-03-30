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
 * A LinkedCategory.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "linkedCategory")
public class LinkedCategory implements Serializable {

    @MongoId
    private String linkCatId;
    private Integer description;
    private Action action;
    @DBRef
    private Category category;

    @DBRef
    private Email email;

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
