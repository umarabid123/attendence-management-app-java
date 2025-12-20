package com.ajstudios.easyattendance.models;

import java.util.List;

public class AttendanceReport {
    private String date;
    private String monthOnly;
    private String dateOnly;
    private String classId;
    private String date_and_classID;
    private String classname;
    private String subjName;
    private List<AttendanceStudent> attendance_students_lists;

    public AttendanceReport() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMonthOnly() {
        return monthOnly;
    }

    public void setMonthOnly(String monthOnly) {
        this.monthOnly = monthOnly;
    }

    public String getDateOnly() {
        return dateOnly;
    }

    public void setDateOnly(String dateOnly) {
        this.dateOnly = dateOnly;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getDate_and_classID() {
        return date_and_classID;
    }

    public void setDate_and_classID(String date_and_classID) {
        this.date_and_classID = date_and_classID;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getSubjName() {
        return subjName;
    }

    public void setSubjName(String subjName) {
        this.subjName = subjName;
    }

    public List<AttendanceStudent> getAttendance_students_lists() {
        return attendance_students_lists;
    }

    public void setAttendance_students_lists(List<AttendanceStudent> attendance_students_lists) {
        this.attendance_students_lists = attendance_students_lists;
    }
}
