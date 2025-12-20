package com.ajstudios.easyattendance.model;

public class SubjectStat {
    private String subjectName;
    private int attended;
    private int total;

    public SubjectStat(String subjectName, int attended, int total) {
        this.subjectName = subjectName;
        this.attended = attended;
        this.total = total;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public int getAttended() {
        return attended;
    }

    public int getTotal() {
        return total;
    }

    public int getPercentage() {
        if (total == 0) return 0;
        return (int) ((attended / (float) total) * 100);
    }
}
