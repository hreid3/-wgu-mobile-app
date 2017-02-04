package edu.wgu.hreid6.wgugo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import edu.wgu.hreid6.wgugo.data.model.Assessment;
import edu.wgu.hreid6.wgugo.data.model.Course;
import edu.wgu.hreid6.wgugo.data.model.Graduate;
import edu.wgu.hreid6.wgugo.data.model.Term;

/**
 * Created by hreid on 2/3/17.
 */
public class PersistenceHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "wgu_go.db";
    private static final int DATABASE_VERSION = 1;

    public PersistenceHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Term.class);
            TableUtils.createTable(connectionSource, Course.class);
            TableUtils.createTable(connectionSource, Assessment.class);
            TableUtils.createTable(connectionSource, Graduate.class);
        } catch (SQLException e) {
            Log.e(PersistenceHelper.class.getName(), "Error creating database and tables.", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        // Not implementing for academic project.
    }


}

