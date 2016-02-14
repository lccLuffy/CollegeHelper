package com.lcc.uestc.bean;

import com.orm.SugarRecord;

/**
 * Created by wanli on 2015/7/13.
 */
public class ExamInfoBean extends SugarRecord{
    String courseNumber;
    String courseName;
    String date;

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getOrderOfWeek() {
        return orderOfWeek;
    }

    public void setOrderOfWeek(String orderOfWeek) {
        this.orderOfWeek = orderOfWeek;
    }

    String week;
    String orderOfWeek;


    String time;
    String place;
    String seat;
    String situation;
    String others;
    int examType;
    int semester;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    long startTime;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }
    public String getCourseNumber() {
        return courseNumber;
    }
    public String getCourseName() {
        return courseName;
    }
    public String getDate() {
        return date;
    }


    public String getPlace() {
        return place;
    }

    public String getSeat() {
        return seat;
    }

    public String getSituation() {
        return situation;
    }

    public String getOthers() {
        return others;
    }

    public void setCourseNumber(String courseNumber) {
        this.courseNumber = courseNumber;
    }
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public void setOthers(String others) {
        this.others = others;
    }

    public int getExamType() {
        return examType;
    }

    public void setExamType(int examType) {
        this.examType = examType;
    }

    @Override
    public String toString() {
        return courseName + "\n" + date + "\n" + time + "\n" + place
                +"\n" + seat + "\n" + situation;
    }
}
