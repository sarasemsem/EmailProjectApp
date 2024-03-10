package com.emailProcessor.emailProcessor.entity;
import com.emailProcessor.basedomains.enumeration.Language;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.io.Serializable;

/**
 * A TranslatedKeyword.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "translated_keyword")
public class TranslatedKeyword {


    @NotNull
    @MongoId
    private String tkeywordId;
    @NotNull
    private String wordTranslated;
    private Language language;


    // Convert Enum to String when setting the language
    public void setLanguage(Language language) {
        this.language = Language.valueOf(language.name());
    }

    // Convert String to Enum when getting the language
    public Language getLanguageEnum() {
        return Language.valueOf(language.toString().toUpperCase());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TranslatedKeyword)) {
            return false;
        }
        return getTkeywordId() != null && getTkeywordId().equals(((TranslatedKeyword) o).getTkeywordId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TranslatedKeyword{" +
            "tkeywordId=" + getTkeywordId() +
            ", wordTranslated='" + getWordTranslated() + "'" +
            ", language='" + getLanguage() + "'" +
            "}";
    }
}
