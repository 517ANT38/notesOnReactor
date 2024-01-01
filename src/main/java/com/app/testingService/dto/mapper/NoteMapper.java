package com.app.testingService.dto.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.app.testingService.dto.NoteDto;
import com.app.testingService.models.Note;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NoteMapper {
    @InheritInverseConfiguration
    Note map(NoteDto dto);
}
