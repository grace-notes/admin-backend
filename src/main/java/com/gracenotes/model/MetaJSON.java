package com.gracenotes.model;

import lombok.Data;

import java.util.List;

/**
 * Created by adam on 4/8/15.
 */
@Data
public class MetaJSON {
    int status;
    String statusText;
    List<String> errors;
}
