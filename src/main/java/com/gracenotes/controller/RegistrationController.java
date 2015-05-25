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
@RequestMapping("/registrations")
public class RegistrationController {

    @Value("${key}")
    private String masterKey;

    @Autowired
    MongoOperations mongoOperation;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseJSON create(@RequestHeader("Authorization") String key, @RequestBody Registration registration) {
        ResponseJSON response = new ResponseJSON();
        MetaJSON meta = new MetaJSON();
        try {
            String decryptedKey = PasswordEncryptionHelper.decrypt(key);
            List<String> credentials = Arrays.asList(decryptedKey.split(":"));
            String email = credentials.get(0);
            String hashedPassword = credentials.get(1);
            Query searchUserQuery = new Query(Criteria.where("email").is(email));
            boolean passed = false;
            User savedUser = mongoOperation.findOne(searchUserQuery, User.class);
            if (null != savedUser) {
                if(savedUser.getPassword().equals(hashedPassword)) { passed = true; }
            }
            if(key.equals(masterKey)) { passed = true; }
            if(passed) {
                // check if registration already exists
                Query searchRegistrationQuery = new Query(Criteria.where("email").is(registration.getEmail()));
                Registration savedRegistration = mongoOperation.findOne(searchRegistrationQuery, Registration.class);
                if (null == savedRegistration) {
                    // create it
                    registration.setCreatedAt(new DateTime().toString());
                    registration.setModifiedAt(new DateTime().toString());
                    mongoOperation.save(registration);
                    meta.setStatus(1);
                    meta.setStatusText("SUCCESS");
                } else {
                    meta.setStatus(2);
                    meta.setStatusText("Email already exists");
                }
            } else {
                meta.setStatus(2);
                meta.setStatusText("Invalid key");
            }
        } catch(Exception e) {
            meta.setStatus(3);
            meta.setStatusText(e.getMessage());
            e.printStackTrace();
        }
        response.setData(registration);
        response.setMeta(meta);
        return response;
    }

    @RequestMapping(method= RequestMethod.GET)
    @ResponseBody
    public ResponseJSON list(@RequestHeader("Authorization") String key) {
        ResponseJSON response = new ResponseJSON();
        MetaJSON meta = new MetaJSON();
        List<Registration> registrations = new ArrayList<>();
        // decrypt the key
        try {
            String decryptedKey = PasswordEncryptionHelper.decrypt(key);
            List<String> credentials = Arrays.asList(decryptedKey.split(":"));
            String email = credentials.get(0);
            String hashedPassword = credentials.get(1);
            Query searchUserQuery = new Query(Criteria.where("email").is(email));
            User savedUser = mongoOperation.findOne(searchUserQuery, User.class);
            boolean passed = false;
            if (null != savedUser) {
                if(savedUser.getPassword().equals(hashedPassword)) { passed = true; }
            }
            if(key.equals(masterKey)) { passed = true; }
            if(passed) {
                registrations = mongoOperation.findAll(Registration.class);
                meta.setStatus(1);
                meta.setStatusText("SUCCESS");
            } else {
                meta.setStatus(2);
                meta.setStatusText("Invalid key");
            }
        } catch (Exception e) {
            meta.setStatus(3);
            meta.setStatusText(e.getMessage());
            e.printStackTrace();
        }
        response.setData(registrations);
        response.setMeta(meta);
        return response;
    }

}
