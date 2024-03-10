package com.emailProcessor.basedomains.dto;

import lombok.*;
import java.util.HashSet;
import java.util.Set;

/**
 * A Keyword.
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KeywordDto {

    @NonNull
    private String keywordId;
    @NonNull
    private String word;
    private WorkerDto createdBy;
    @NonNull
    private String categoryId;
    private Set<TranslatedKeywordDto> translatedKeywords = new HashSet<>();




    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KeywordDto)) {
            return false;
        }
        return getKeywordId() != null && getKeywordId().equals(((KeywordDto) o).getKeywordId());
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
