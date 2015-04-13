package com.gracenotes.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document(collection = "users")
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
	@Id String id;
	String email;
    String hashedEmail; // used for gravatar
    String role; // admin or student
	String password;
    String createdAt;
    String modifiedAt;
}
