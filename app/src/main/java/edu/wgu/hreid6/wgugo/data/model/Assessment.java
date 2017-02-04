package edu.wgu.hreid6.wgugo.data.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by hreid on 2/3/17.
 */
@DatabaseTable(tableName = "assessment")
public class Assessment {

    public final static String colId = "id";
    public final static String colType = "type";
    public final static String colDueDate = "due_date";
    public final static String colNotes = "notes";
    public final static String colNotify = "notify";

    public final static String colCourseId = "course_id";

    public enum TYPE {
        PERFORMANCE, OBJECTIVE
    }

    @DatabaseField(generatedId = true, columnName = colId)
    private Integer id;

    @DatabaseField(canBeNull = false, dataType = DataType.ENUM_STRING, columnName = colType)
    private TYPE type;

    @DatabaseField(canBeNull = false, dataType = DataType.DATE_STRING, columnName = colDueDate)
    private Date dueDate;

    @DatabaseField(width = 4096, columnName = colNotes)
    private String notes;

    @DatabaseField(columnName = colNotify)
    private boolean notify;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = colCourseId)
    private Course course;

    // TODO: How are we going to reference photos.

    public Assessment() {
        this.course = new Course();
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Assessment that = (Assessment) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

