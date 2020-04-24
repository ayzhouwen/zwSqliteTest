package com.zw.sqliteTest.entity;

import lombok.Data;

@Data
public class Student {
    private Integer id;
    private String name;
    private Integer age;
    private Integer classId;
    private Integer gradeId;
}
