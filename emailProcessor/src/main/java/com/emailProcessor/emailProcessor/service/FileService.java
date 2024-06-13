package com.emailProcessor.emailProcessor.service;

import com.emailProcessor.basedomains.dto.AttachmentDto;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class FileService {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFsOperations operations;

    public ObjectId saveFile(AttachmentDto attachmentDto) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(attachmentDto.getFileContent());
        return gridFsTemplate.store(inputStream, attachmentDto.getFileName());
    }

    public GridFsResource getFile(String id) {
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));
        return operations.getResource(file);
    }
}