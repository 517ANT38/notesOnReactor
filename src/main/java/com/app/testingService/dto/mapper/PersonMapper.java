package com.app.testingService.dto.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.app.testingService.dto.PersonDto;
import com.app.testingService.dto.PersonDtoWithNotes;
import com.app.testingService.models.Person;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonMapper {
    
    PersonDto map(Person person);

    @InheritInverseConfiguration
    Person map(PersonDto dto);

    PersonDtoWithNotes mapWithNotes(Person p);
}
