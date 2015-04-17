package com.gracenotes.model;

/**
 * Created by adam on 4/16/15.
 */

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@Data
public class UserFront implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    String id;
    String email;
    String hashedEmail; // used for gravatar
    String role; // admin or student
    String createdAt;
    String modifiedAt;

    public UserFront(User user) {
        this.id = user.id;
        this.email = user.email;
        this.hashedEmail = user.hashedEmail;
        this.role = user.role;
        this.createdAt = user.createdAt;
        this.modifiedAt = user.modifiedAt;
    }
}
