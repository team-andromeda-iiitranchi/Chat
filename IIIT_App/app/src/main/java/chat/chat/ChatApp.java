package chat.chat;

import android.app.Application;
import android.app.ProgressDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import chat.chat.chat.Users;

public class ChatApp extends Application {
    public static String rollInfo;
    public static ProgressDialog mProgress;
    public static Users user;
    @Override
    public void onCreate() {

        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}
