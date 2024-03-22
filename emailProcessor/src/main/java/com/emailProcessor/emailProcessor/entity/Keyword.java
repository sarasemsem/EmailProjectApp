package com.emailProcessor.emailProcessor.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.MongoId;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Keyword.
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "keyword")
public class Keyword implements Serializable{

    @MongoId
    private String keywordId;
    @NotNull
    private String word;
    @DBRef
    private Worker createdBy;
    @JsonIgnoreProperties("keywords")
    private Set<Category> categories = new HashSet<>();

    @JsonIgnoreProperties(value = { "keyword" }, allowSetters = true)
    private Set<TranslatedKeyword> translatedKeywords = new HashSet<>();




    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Keyword)) {
            return false;
        }
        return getKeywordId() != null && getKeywordId().equals(((Keyword) o).getKeywordId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Keyword{" +
            "keywordId=" + getKeywordId() +
            ", word='" + getWord() + "'" +
            ", createdBy=" + getCreatedBy() +
            "}";
    }
}
