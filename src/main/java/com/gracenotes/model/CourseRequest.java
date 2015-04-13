package com.gracenotes.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by adam on 4/12/15.
 */
@Document(collection = "courseRequests")
@Data
public class CourseRequest {
    @Id
    String id;
    String email;
    String name;
    String course;
    String createdAt;
    String modifiedAt;
}
