package com.gracenotes.controller;

import com.gracenotes.model.*;
import com.gracenotes.util.PasswordEncryptionHelper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by adam on 4/25/15.
 */
@RestController
@RequestMapping("/courserequests")
public class CourseRequestController {
    @Value("${key}")
    private String masterKey;

    @Autowired
    MongoOperations mongoOperation;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseJSON create(@RequestHeader("Authorization") String key, @RequestBody CourseRequest courseRequest) {
        ResponseJSON response = new ResponseJSON();
        MetaJSON meta = new MetaJSON();
        try {
            String decryptedKey = PasswordEncryptionHelper.decrypt(key);
            List<String> credentials = Arrays.asList(decryptedKey.split(":"));
            String email = credentials.get(0);
            String hashedPassword = credentials.get(1);
            Query searchUserQuery = new Query(Criteria.where("email").is(email));
            User savedUser = mongoOperation.findOne(searchUserQuery, User.class);
            if(null == savedUser && !(key.equals(masterKey))) {
                meta.setStatus(2);
                meta.setStatusText("Invalid key");
            } else {
                if (!savedUser.getPassword().equals(hashedPassword) && !(key.equals(masterKey))) {
                    meta.setStatus(2);
                    meta.setStatusText("Invalid key");
                } else {
                    // check if registration already exists
                    Query searchCourseRequestQuery = new Query(Criteria.where("email").is(courseRequest.getEmail()));
                    CourseRequest savedCourseRequest = mongoOperation.findOne(searchCourseRequestQuery, CourseRequest.class);
                    if(null == savedCourseRequest) {
                        // create it
                        courseRequest.setCreatedAt(new DateTime().toString());
                        courseRequest.setModifiedAt(new DateTime().toString());
                        mongoOperation.save(courseRequest);
                        meta.setStatus(1);
                        meta.setStatusText("SUCCESS");
                    } else {
                        meta.setStatus(2);
                        meta.setStatusText("Email already exists");
                    }
                }
            }
        } catch(Exception e) {
            meta.setStatus(3);
            meta.setStatusText(e.getMessage());
            e.printStackTrace();
        }
        response.setData(courseRequest);
        response.setMeta(meta);
        return response;
    }

    @RequestMapping(method= RequestMethod.GET)
    @ResponseBody
    public ResponseJSON list(@RequestHeader("Authorization") String key) {
        ResponseJSON response = new ResponseJSON();
        MetaJSON meta = new MetaJSON();
        List<CourseRequest> courseRequests = new ArrayList<>();
        // decrypt the key
        try {
            String decryptedKey = PasswordEncryptionHelper.decrypt(key);
            List<String> credentials = Arrays.asList(decryptedKey.split(":"));
            String email = credentials.get(0);
            String hashedPassword = credentials.get(1);
            Query searchUserQuery = new Query(Criteria.where("email").is(email));
            User savedUser = mongoOperation.findOne(searchUserQuery, User.class);
            if(null == savedUser) {
                meta.setStatus(2);
                meta.setStatusText("Invalid key");
            } else {
                if (!savedUser.getPassword().equals(hashedPassword)) {
                    meta.setStatus(2);
                    meta.setStatusText("Invalid key");
                } else {
                    courseRequests = mongoOperation.findAll(CourseRequest.class);
                    meta.setStatus(1);
                    meta.setStatusText("SUCCESS");
                }
            }
        } catch (Exception e) {
            meta.setStatus(3);
            meta.setStatusText(e.getMessage());
            e.printStackTrace();
        }
        response.setData(courseRequests);
        response.setMeta(meta);
        return response;
    }
}
