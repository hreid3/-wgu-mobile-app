package edu.wgu.hreid6.wgugo.data.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;

/**
 * Created by hreid on 2/3/17.
 */
@DatabaseTable(tableName = "graduate")
public class Graduate {

    public enum STATUS {
        ACTIVE("Active"), PENDING("Pending"), UNDERGRAD("Undergrad"), ALUMNUS("Alumnus");
        private final String stringValue;
        private STATUS(final String s) { stringValue = s; }
        public String toString() { return stringValue; }

    }

    @DatabaseField(id = true)
    private Integer id;

    @DatabaseField
    private String firstName;

    @DatabaseField
    private String lastName;

    @DatabaseField(width = 2048)
    private String email;

    @DatabaseField(dataType = DataType.ENUM_STRING)
    private STATUS status = STATUS.ACTIVE;

    @ForeignCollectionField
    private Collection<Course> courses;

    @ForeignCollectionField
    private Collection<Term> terms;

    public Graduate() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public Collection<Term> getTerms() {
        return terms;
    }

    public Collection<Course> getCourses() {
        return courses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Graduate graduate = (Graduate) o;
        return id == graduate.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
