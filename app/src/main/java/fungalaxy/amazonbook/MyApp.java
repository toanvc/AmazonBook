package fungalaxy.amazonbook;

import android.app.Application;

import org.greenrobot.greendao.database.Database;

import fungalaxy.amazonbook.model.DaoMaster;
import fungalaxy.amazonbook.model.DaoSession;


public class MyApp extends Application {
    private static final String LOG_TAG = "MyApplication";

    private DaoSession daoSession;


    @Override
    public void onCreate() {
        super.onCreate();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "book-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }


    public DaoSession getDaoSession() {
        return daoSession;
    }

}
