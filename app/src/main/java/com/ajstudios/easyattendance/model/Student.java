package com.ajstudios.easyattendance.model;

public class Student {
    private String id;
    private String name;
    private String regNo;
    private String mobileNo;
    private String classId;

    public Student() {
    }

    public Student(String name, String regNo, String mobileNo, String classId) {
        this.name = name;
        this.regNo = regNo;
        this.mobileNo = mobileNo;
        this.classId = classId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }
}
