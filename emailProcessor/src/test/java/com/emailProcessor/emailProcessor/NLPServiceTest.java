package com.emailProcessor.emailProcessor;

import com.emailProcessor.basedomains.dto.CategoryDto;
import com.emailProcessor.basedomains.dto.EmailDto;
import com.emailProcessor.basedomains.dto.EmailProcessingResultDto;
import com.emailProcessor.basedomains.dto.KeywordDto;
import com.emailProcessor.emailProcessor.entity.Email;
import com.emailProcessor.emailProcessor.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class NLPServiceTest {

    @Mock
    private EmailService emailService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private EmailProcessingResultService emailProcessingResultService;

    @Mock
    private KeywordService keywordService;

    @InjectMocks
    private NLPService nlpService;

    private ModelMapper modelMapper;

    @BeforeEach
    public void setup() {
        modelMapper = new ModelMapper();
    }

    @Test
    public void testTreatEmail() {
        // Mocking email data
        Email email = new Email();
        email.setContent("Sample email content");

        // Mocking keyword data
        KeywordDto keywordDto = new KeywordDto();
        keywordDto.setWord("test");
        keywordDto.setWeight(0.5);

        // Mocking category data
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryId("1");

        // Mocking behavior of dependencies
        when(emailService.partialUpdate(any(EmailDto.class))).thenReturn(Optional.of(email)); // Assuming 'email' is an instance of Email
        when(keywordService.findKeywordByWord(anyString())).thenReturn(Optional.of(keywordDto));
        when(categoryService.findOneCategory(anyString())).thenReturn(Optional.of(categoryDto));
        when(emailProcessingResultService.saveEmailProcessingResult(any(EmailProcessingResultDto.class))).thenReturn(new EmailProcessingResultDto());

        // Calling the method to be tested
        nlpService.treatEmail(modelMapper.map(email, EmailDto.class));

        // Verifying that the dependencies are called
        verify(emailService, times(1)).partialUpdate(any(EmailDto.class));
        verify(keywordService, times(1)).findKeywordByWord(anyString());
        verify(categoryService, times(1)).findOneCategory(anyString());
        verify(emailProcessingResultService, times(1)).saveEmailProcessingResult(any(EmailProcessingResultDto.class));
    }
}
