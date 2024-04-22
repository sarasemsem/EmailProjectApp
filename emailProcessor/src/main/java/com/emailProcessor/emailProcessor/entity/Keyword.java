package com.emailProcessor.emailProcessor.entity;

import com.mongodb.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;
import java.util.List;

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
    @NonNull
    private String word;
    @NonNull
    @DBRef
    private Worker createdBy;
    private Double weight ;
    @NonNull
    @DBRef
    private List<Category> categories ;
    @DBRef
    private transient List<TranslatedKeyword> translatedKeywords ;




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
