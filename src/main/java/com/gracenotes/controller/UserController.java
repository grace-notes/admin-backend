package com.gracenotes.controller;

import com.gracenotes.model.MetaJSON;
import com.gracenotes.model.ResponseJSON;
import com.gracenotes.model.Session;
import com.gracenotes.model.User;
import com.gracenotes.util.PasswordEncryptionHelper;
import com.gracenotes.util.PasswordHashingHelper;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired MongoOperations mongoOperation;
    @Value("${code}")
    private String secretCode;
    @Value("${key}")
    private String masterKey;

    // create POST /users
    @RequestMapping(method=RequestMethod.POST)
    @ResponseBody
    public ResponseJSON create(@RequestHeader("Authorization") String key, @RequestBody User user) {
        ResponseJSON response = new ResponseJSON();
        MetaJSON meta = new MetaJSON();
        // decrypt the key
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
                    // check if user already exists
                    searchUserQuery = new Query(Criteria.where("email").is(user.getEmail()));
                    savedUser = mongoOperation.findOne(searchUserQuery, User.class);
                    if(null == savedUser) {
                        // create it
                        user.setCreatedAt(new DateTime().toString());
                        user.setModifiedAt(new DateTime().toString());
                        user.setHashedEmail(PasswordHashingHelper.toMD5(user.getEmail()));
                        mongoOperation.save(user);
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
        response.setData(user);
        response.setMeta(meta);
        return response;
    }
    // create POST /user/register
    // create POST /user/course

    @RequestMapping(method= RequestMethod.GET)
    @ResponseBody
    public ResponseJSON list(@RequestHeader("Authorization") String key) {
        ResponseJSON response = new ResponseJSON();
        MetaJSON meta = new MetaJSON();
        List<User> users = null;
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
                    users = mongoOperation.findAll(User.class);
                    meta.setStatus(1);
                    meta.setStatusText("SUCCESS");
                }
            }
        } catch (Exception e) {
            meta.setStatus(3);
            meta.setStatusText(e.getMessage());
            e.printStackTrace();
        }
        response.setData(users);
        response.setMeta(meta);
        return response;
    }

    @RequestMapping(value="/login", method=RequestMethod.POST)
    public ResponseJSON logIn(@RequestBody Object obj) {
        ObjectMapper objectMapper = new ObjectMapper();
        Session session = null;
        ResponseJSON response = new ResponseJSON();
        MetaJSON meta = new MetaJSON();
        try {
            String json = objectMapper.writeValueAsString(obj);
            JsonParser jsonParser = objectMapper.getJsonFactory().createJsonParser(json);
            User user = objectMapper.readValue(jsonParser, User.class);
            // encrypt the password to match it up against what's in the database.
            user.setPassword(PasswordHashingHelper.toSHA1(user.getPassword()));
            // see if that user exists in the database
            Query searchUserQuery = new Query(Criteria.where("email").is(user.getEmail()));
            User savedUser = mongoOperation.findOne(searchUserQuery, User.class);
            if(null == savedUser) {
                meta.setStatus(2);
                meta.setStatusText("Invalid username/password");
            } else {
                if(!savedUser.getPassword().equals(user.getPassword())) {
                    meta.setStatus(2);
                    meta.setStatusText("Invalid username/password");
                } else {
                    user = savedUser;
                    session = new Session();
                    session.setUser(user);
                    session.setKey(PasswordEncryptionHelper.encrypt(user.getEmail() + ":" + user.getPassword()));
                    session.setHashedEmail(PasswordHashingHelper.toMD5(user.getEmail()));
                    meta.setStatus(1);
                    meta.setStatusText("SUCCESS");
                }
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
        response.setData(session);
        response.setMeta(meta);
        return response;
    }

    @RequestMapping(value="/signup", method=RequestMethod.POST)
    public ResponseJSON signUp(@RequestBody Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        Session session = null;
        ResponseJSON response = new ResponseJSON();
        MetaJSON meta = new MetaJSON();
        try {
            LinkedHashMap<String, String> params = (LinkedHashMap<String, String>) obj;
            String code = params.remove("code");
            String json = mapper.writeValueAsString(params);
            JsonParser jsonParser = mapper.getJsonFactory().createJsonParser(json);
            User user = mapper.readValue(jsonParser, User.class);
            // encrypt the password when saving to the database
            user.setPassword(PasswordHashingHelper.toSHA1(user.getPassword()));
            // determine if user already exists if so don't save
            Query searchUserQuery = new Query(Criteria.where("email").is(user.getEmail()));
            User savedUser = mongoOperation.findOne(searchUserQuery, User.class);
            if(null == savedUser) {
                user.setCreatedAt(new DateTime().toString());
                user.setModifiedAt(new DateTime().toString());
                user.setRole("admin");
                user.setHashedEmail(PasswordHashingHelper.toMD5(user.getEmail()));
                mongoOperation.save(user);
                session = new Session();
                session.setUser(user);
                session.setKey(PasswordEncryptionHelper.encrypt(user.getEmail() + ":" + user.getPassword()));
                meta.setStatus(1);
                meta.setStatusText("SUCCESS");
            } else {
                meta.setStatus(2);
                meta.setStatusText("Email already exists");
            }
            if(!code.equals(secretCode)) {
                meta.setStatus(2);
                meta.setStatusText("The code was wrong");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.setData(session);
        response.setMeta(meta);
        return response;
    }

    /* TODO update password
      mongoOperation.updateFirst(searchUserQuery,
      Update.update("password", "new password"),User.class);
      delete
      mongoOperation.remove(searchUserQuery, User.class);
    */

}
