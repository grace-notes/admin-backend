package com.gracenotes.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by adam on 4/11/15.
 */
@Data
public class Session implements Serializable {

    private static final long serialVersionUID = 1L;

    UserFront user;
    String key;
    String hashedEmail;
}
