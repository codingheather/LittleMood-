package edu.northeastern.group26.littlemood;

import com.google.firebase.FirebaseApp;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        initFirebase();
    }
    private void initFirebase(){
//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                        if (!task.isSuccessful()) {
//                            MLog.e( "getInstanceId failed"+task.getException());
//                            return;
//                        }
//                        String token = task.getResult().getToken();
//                    }
//                });
    }
}
