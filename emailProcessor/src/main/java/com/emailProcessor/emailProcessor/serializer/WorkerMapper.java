package com.emailProcessor.emailProcessor.serializer;

import com.emailProcessor.basedomains.dto.WorkerDto;
import com.emailProcessor.emailProcessor.entity.Worker;
import org.mapstruct.Mapper;
@Mapper(componentModel = "spring")
public interface WorkerMapper {

    WorkerDto toWorkerDto(Worker worker);
}
