package com.emailProcessor.emailProcessor.mappers;

import com.emailProcessor.basedomains.dto.WorkerDto;
import com.emailProcessor.emailProcessor.entity.Worker;
import org.mapstruct.Mapper;
@Mapper(componentModel = "spring")
public interface WorkerMapper {

    WorkerDto toWorkerDto(Worker worker);
}
