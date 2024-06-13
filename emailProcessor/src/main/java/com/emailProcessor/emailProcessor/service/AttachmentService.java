package com.emailProcessor.emailProcessor.service;
import com.emailProcessor.basedomains.dto.AttachmentDto;
import com.emailProcessor.emailProcessor.entity.Attachment;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public interface AttachmentService {
    Attachment saveAttachment(Attachment attachment);
    Attachment updateAttachment(Attachment attachment);
    Optional<Attachment> partialUpdateAttachment(Attachment attachment);
    List<Attachment> findAllAttachment();
    Optional<Attachment> findOneAttachment(String id);
    void deleteAttachment(String id);
}
