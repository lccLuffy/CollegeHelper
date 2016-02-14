package com.lcc.uestc.bean;

import com.orm.SugarRecord;

/**
 * Created by wanli on 2015/7/13.
 */
public class GradeBean extends SugarRecord implements Comparable{
    private String semester;
    private String courseCode;
    private String courseNumber;
    private String courseName;
    private String courseType;
    private String credit;
    private String grade;
    private String makeupGrade;
    private String finalGrade;
    private String GPA;

    public int getSemesterCode() {
        return semesterCode;
    }

    public void setSemesterCode(int semesterCode) {
        this.semesterCode = semesterCode;
    }

    private int semesterCode;
    public String getSemester() {
        return semester;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseNumber() {
        return courseNumber;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getCourseType() {
        return courseType;
    }

    public String getCredit() {
        return credit;
    }

    public String getGrade() {
        return grade;
    }

    public String getMakeupGrade() {
        return makeupGrade;
    }

    public String getFinalGrade() {
        return finalGrade;
    }

    public String getGPA() {
        return GPA;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public void setCourseNumber(String courseNumber) {
        this.courseNumber = courseNumber;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public void setMakeupGrade(String makeupGrade) {
        this.makeupGrade = makeupGrade;
    }

    public void setFinalGrade(String finalGrade) {
        this.finalGrade = finalGrade;
    }

    public void setGPA(String GPA) {
        this.GPA = GPA;
    }

    @Override
    public String toString() {
        return semester + " " + courseName + " " + finalGrade + " "+credit + " " + courseCode;
    }

    @Override
    public int compareTo(Object another) {
        GradeBean gradeBean = (GradeBean)another;
        try
        {
            return Integer.parseInt(finalGrade) - Integer.parseInt(gradeBean.getFinalGrade());
        }
        catch (NumberFormatException e)
        {
            return -1;
        }
    }
}
