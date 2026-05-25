package com.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "employees")
public class Employee {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String name;
    private String position;
    private Double salary;   // всегда Double
    @Field("department_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId departmentId;
}