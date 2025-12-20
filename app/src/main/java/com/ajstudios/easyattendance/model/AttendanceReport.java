package com.ajstudios.easyattendance.model;

import java.util.List;

public class AttendanceReport {
    private String date;
    private String classId;
    private String className;
    private String subjectName;
    private List<AttendanceItem> attendanceList;

    public AttendanceReport() {
    }

    public AttendanceReport(String date, String classId, String className, String subjectName, List<AttendanceItem> attendanceList) {
        this.date = date;
        this.classId = classId;
        this.className = className;
        this.subjectName = subjectName;
        this.attendanceList = attendanceList;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    @com.google.firebase.firestore.Exclude
    private String id;

    public List<AttendanceItem> getAttendanceList() {
        return attendanceList;
    }

    public void setAttendanceList(List<AttendanceItem> attendanceList) {
        this.attendanceList = attendanceList;
    }

    @com.google.firebase.firestore.Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
