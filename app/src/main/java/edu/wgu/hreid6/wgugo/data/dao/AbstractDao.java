package edu.wgu.hreid6.wgugo.data.dao;

import android.app.Activity;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;

import edu.wgu.hreid6.wgugo.data.PersistenceHelper;

/**
 * Created by hreid on 2/3/17.
 */

abstract class AbstractDao<T> {

    protected PersistenceHelper helper;

    public AbstractDao(Activity activity) {
        helper = OpenHelperManager.getHelper(activity, PersistenceHelper.class);
    }

    protected QueryBuilder<T, ?> getQueryBuilder(Class<T> t) throws SQLException {
        return getDao(t).queryBuilder();
    }

    protected Dao<T, Integer> getDao(Class<T> t) throws SQLException {
        return helper.getDao(t);
    }

    public void releaseResources() {
        OpenHelperManager.releaseHelper();
    }
}
