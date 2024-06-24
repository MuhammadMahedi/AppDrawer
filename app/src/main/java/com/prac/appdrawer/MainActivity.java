package com.prac.appdrawer;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private GestureDetectorCompat gestureDetector;
    public static List<ApplicationInformations> appList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //packageNames = getInstalledPackages();
        appList = getInstalledApps();




        // Set up the gesture detector
        gestureDetector = new GestureDetectorCompat(this, new GestureListener());


        // Set up a touch listener on the main view to detect gestures
        View mainView = findViewById(R.id.main_view);
        mainView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("CheckingSwipe", "Touched" );
                return gestureDetector.onTouchEvent(event);
            }
        });

    }

//    private List<String> getInstalledPackages() {
//        List<String> packageNames = new ArrayList<>();
//        PackageManager packageManager = getPackageManager();
//        List<PackageInfo> packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);
//
//        for (PackageInfo packageInfo : packages) {
//            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
//            /*if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
//                // It's a non-system app
//                packageNames.add(packageInfo.packageName);
//            }*/
//            packageNames.add(packageInfo.packageName);
//        }
//
//        return packageNames;
//    }

    private List<ApplicationInformations> getInstalledApps() {
        List<ApplicationInformations> appInfoList = new ArrayList<>();
        PackageManager packageManager = getPackageManager();
        List<ApplicationInfo> apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo app : apps) {
            String appName = (String) packageManager.getApplicationLabel(app);
            String pkgName = (String) app.packageName;
            Drawable appIcon = packageManager.getApplicationIcon(app);
            appInfoList.add(new ApplicationInformations(appName,pkgName, appIcon));
        }

        return appInfoList;
    }

    private void showBottomSheet() {
        MyBottomSheetDialogFragment bottomSheet = new MyBottomSheetDialogFragment();
        bottomSheet.show(getSupportFragmentManager(), "MyBottomSheetDialogFragment");
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffY) > Math.abs(diffX)) {
                    if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY < 0) {
                            // Swipe up
                            showBottomSheet();
                            return true;
                        }
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }
}
