package com.gracenotes.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * Created by adam on 4/12/15.
 */
@Document(collection = "registrations")
@Data
public class Registration implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    String id;
    String email;
    String name;
    String address;
    String city;
    boolean hasHighSchool;
    String collegeDegree;
    String work;
    String testimony;
    String ministryPrepare;
    String bibleTeaching;
    String pastorBackground;
    String churchName;
    String averageAttendance;
    String teachingType;
    String comments;
    String createdAt;
    String modifiedAt;
}
