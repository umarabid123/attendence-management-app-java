package com.ajstudios.easyattendance.model;

public class AttendanceItem {
    private String studentName;
    private String regNo;
    private String status; // "Present" or "Absent"

    public AttendanceItem() {
    }

    public AttendanceItem(String studentName, String regNo, String status) {
        this.studentName = studentName;
        this.regNo = regNo;
        this.status = status;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
