package com.emailProcessor.emailProcessor.service.impl;

import com.emailProcessor.basedomains.dto.ActionDto;
import com.emailProcessor.emailProcessor.entity.Action;
import com.emailProcessor.emailProcessor.entity.Attachment;
import com.emailProcessor.emailProcessor.repository.ActionRepository;
import com.emailProcessor.emailProcessor.repository.AttachmentRepository;
import com.emailProcessor.emailProcessor.service.ActionService;
import com.emailProcessor.emailProcessor.service.AttachmentService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AttachmentServiceImp implements AttachmentService {
    private final Logger log = LoggerFactory.getLogger(AttachmentServiceImp.class);
    private final ModelMapper modelMapper;
    private final AttachmentRepository attachmentRepository;
    @Override
    public Attachment saveAttachment(Attachment attachment) {
        log.debug("Request to save attachment : {}", attachment);
        return attachmentRepository.save(attachment);
    }

    @Override
    public Attachment updateAttachment(Attachment attachment) {
        log.debug("Request to update Attachment : {}", attachment);
        Attachment updatedAttachment= attachmentRepository.save(attachment);
        return updatedAttachment;
    }

    @Override
    public Optional<Attachment> partialUpdateAttachment(Attachment attachment) {
        log.debug("Request to partially update Attachment : {}", attachment);
        return attachmentRepository
                .findById(attachment.getAttachmentId())
                .map(existingAttachment -> {
                    if (attachment.getFileId() != null) {
                        existingAttachment.setFileId(attachment.getFileId());
                    }
                    if (attachment.getFileName() != null) {
                        existingAttachment.setFileName(attachment.getFileName());
                    }
                    return existingAttachment;
                })
                .map(attachmentRepository::save);
    }

    @Override
    public List<Attachment> findAllAttachment() {
        log.debug("Request to get all Attachments");
        return attachmentRepository.findAll();
    }

    @Override
    public Optional<Attachment> findOneAttachment(String id) {
        log.debug("Request to get Attachment : {}", id);
        Optional<Attachment> attachment= attachmentRepository.findById(id);
        return attachment;
}

    @Override
    public void deleteAttachment(String id) {
        log.debug("Request to delete Attachment : {}", id);
        attachmentRepository.deleteById(id);
    }
}
