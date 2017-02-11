package edu.wgu.hreid6.wgugo.data.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public final static String colPhotopaths = "photopaths";

    public final static String colCourseId = "course_id";

    public enum TYPE {
        OBJECTIVE("Objective"), PERFORMANCE("Performance");
        private final String stringValue;
        private TYPE(final String s) {
            stringValue = s;
        }
        public String toString() {
            return stringValue;
        }

    }

    @DatabaseField(generatedId = true, columnName = colId)
    private Integer id;

    @DatabaseField(canBeNull = false, dataType = DataType.ENUM_STRING, columnName = colType)
    private TYPE type = TYPE.OBJECTIVE;

    @DatabaseField(canBeNull = false, dataType = DataType.DATE_STRING, columnName = colDueDate)
    private Date dueDate;

    @DatabaseField(width = 4096, columnName = colNotes)
    private String notes;

    @DatabaseField(width = 4096, columnName = colPhotopaths)
    private String photoPaths;

    @DatabaseField(columnName = colNotify)
    private boolean notify;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = colCourseId)
    private Course course;

    private List<String> internalPhotosList;
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

    public List<String> getFilePaths() {
        return getInternalList();
    }

    protected List<String> getInternalList() {
        if (internalPhotosList == null) {
            if (photoPaths != null && photoPaths.length() > 0) {
                // Json structure
                Gson gson = new GsonBuilder().create();
                internalPhotosList = gson.fromJson(photoPaths, new TypeToken<ArrayList<String>>() {}.getType());
            } else {
                // new List
                internalPhotosList = new ArrayList<>();
            }
        }
        return internalPhotosList;
    }

    public void clearPaths() {
        if (this.internalPhotosList != null) {
            this.internalPhotosList.clear();
        }
        this.photoPaths = "[]";
    }

    public void addFilePath(String path) {
        getFilePaths().add(path);
        saveInternalList();
    }

    public boolean removeFilePath(String path){
        boolean stat = getFilePaths().remove(path);
        if (stat) {
            saveInternalList();
        }
        return stat;
    }

    protected void saveInternalList() {
        if (getInternalList() != null) {
            Gson gson = new GsonBuilder().create();
            this.photoPaths = gson.toJson(getInternalList(), new TypeToken<ArrayList<String>>() {}.getType());
        }
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

