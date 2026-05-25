package com.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "system_users")
public class SystemUser {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    @Indexed(unique = true)
    private String login;
    private String password;   // в реальном проекте хранить хеш
    private String role;       // READER, EDITOR, ADMIN

}