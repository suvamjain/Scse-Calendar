package scse.vit.calendar;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Ayush on 3/24/2018.
 */

public class FirebaseApp extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
    /* Enable disk persistence  */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
