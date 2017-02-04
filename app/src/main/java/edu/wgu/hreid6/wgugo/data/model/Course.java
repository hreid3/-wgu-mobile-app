package edu.wgu.hreid6.wgugo.data.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

/**
 * Created by hreid on 2/3/17.
 */
@DatabaseTable(tableName = "course")
public class Course implements Serializable {

    public final static String colId            = "id";
    public final static String colTitle         = "title";
    public final static String colStartDate     = "start_date";
    public final static String colEndDate       = "end_date";
    public final static String colStatus        = "status";
    public final static String colMentorName    = "mentor_name";
    public final static String colTermId        = "term_id";
    public final static String colGradId        = "grad_id";

    public enum STATUS {
        START_APPROVED, PENDING, FAILED, PASSED
    }

    @DatabaseField(generatedId = true, columnName = colId)
    private Integer id;

    @DatabaseField(canBeNull = false, width = 256, dataType = DataType.STRING, columnName = colTitle)
    private String title;

    @DatabaseField(canBeNull = false, dataType = DataType.DATE_STRING, index = true, columnName = colStartDate)
    private Date startDate;

    @DatabaseField(canBeNull = true, dataType = DataType.DATE_STRING, index = true, columnName = colEndDate)
    private Date endDate;

    @DatabaseField(canBeNull = false, dataType = DataType.ENUM_STRING, columnName = colStatus)
    private STATUS status = STATUS.PENDING;

    @DatabaseField(canBeNull = true, dataType = DataType.STRING, width = 2048, columnName = colMentorName)
    private String courseMentorName;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = colTermId)
    private Term term;

    @ForeignCollectionField
    private Collection<Assessment> assessments;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = colGradId)
    private Graduate graduate;

    @DatabaseField(canBeNull = true, dataType = DataType.INTEGER, version = true)
    private int version;

    public Course() {
        this.assessments = new HashSet<>();
        this.term = new Term();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public String getCourseMentorName() {
        return courseMentorName;
    }

    public void setCourseMentorName(String courseMentorName) {
        this.courseMentorName = courseMentorName;
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Collection<Assessment> getAssessments() {
        return this.assessments;
    }

    public Graduate getGraduate() {
        return graduate;
    }

    public void setGraduate(Graduate graduate) {
        this.graduate = graduate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return id.equals(course.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
