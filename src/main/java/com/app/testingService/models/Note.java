package com.app.testingService.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Table
public class Note {
    @Id
    private Integer id;
    private String title;
    private String txt;
    @Column("personid")
    private int personId;
    @Transient
    private Person person;
}
