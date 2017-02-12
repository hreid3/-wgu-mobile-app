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
@DatabaseTable(tableName = "term")
public class Term implements Serializable {

    public final static String colId            = "id";
    public final static String colTitle         = "title";
    public final static String colStartDate     = "start_date";
    public final static String colEndDate       = "end_date";
    public final static String colStatus        = "status";
    public final static String colGradId        = "grad_id";


    public enum STATUS {
        START_APPROVED("Start Approved"), INACTIVE("Inactive"), OPENED("Opened"), AWAITING_APPROVAL("Awaiting Approval");
        private final String stringValue;
        private STATUS(final String s) { stringValue = s; }
        public String toString() { return stringValue; }
    }

    @DatabaseField(generatedId = true, columnName = colId)
    private Integer pid;

    @DatabaseField(canBeNull = false, width = 256, dataType = DataType.STRING, columnName = colTitle)
    private String title;

    @DatabaseField(canBeNull = false, dataType = DataType.DATE_STRING, index = true, columnName = colStartDate)
    private Date startDate;

    @DatabaseField(canBeNull = false, dataType = DataType.DATE_STRING, index = true, columnName = colEndDate)
    private Date endDate;

    @DatabaseField(canBeNull = false, columnName = colStatus)
    private STATUS status = STATUS.START_APPROVED;

    @ForeignCollectionField
    private Collection<Course> courses;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = colGradId)
    private Graduate graduate;


    public Term() {
        this.courses = new HashSet<>();
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
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

    public Collection<Course> getCourses() {
        return this.courses;
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
        Term term = (Term) o;
        return pid == term.pid;
    }

    @Override
    public int hashCode() {
        return pid;
    }
}
