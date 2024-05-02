package com.emailProcessor.basedomains.dto;

import com.emailProcessor.basedomains.enumeration.Language;
import lombok.*;

/**
 * A TranslatedKeyword.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TranslatedKeywordDto {


    private String tkeywordId;
    private String wordTranslated;
    private Language language;


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
        if (!(o instanceof TranslatedKeywordDto)) {
            return false;
        }
        return getTkeywordId() != null && getTkeywordId().equals(((TranslatedKeywordDto) o).getTkeywordId());
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
