package com.prac.appdrawer;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
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

    // If we had to get only the system applications
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


    //  If we had to get apps depending on intents and actions

//    private List<ApplicationInformations> getInstalledApps() {
//        List<ApplicationInformations> appInfoList = new ArrayList<>();
//        PackageManager packageManager = getPackageManager();
//        List<ApplicationInfo> apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
//
//        // Intent actions to check for user-accessible system apps
//        Intent[] userIntents = {
//                new Intent(Intent.ACTION_DIAL),
//                new Intent(Intent.ACTION_VIEW, Uri.parse("content://contacts/people")),
//                new Intent(Intent.ACTION_VIEW).setType("vnd.android-dir/mms-sms"),
//                new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_CALCULATOR),
//                new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_CALENDAR),
//                new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_BROWSER),
//                new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_MUSIC),
//                new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_CONTACTS),
//                new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_EMAIL),
//                new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_FILES),
//                new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_FITNESS),
//                new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_GALLERY),
//                new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_MAPS),
//                new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_MARKET),
//                new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_MESSAGING),
//                new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_WEATHER),
//                // Add more intents as needed for other system apps
//        };
//
//        for (ApplicationInfo app : apps) {
//            // Check if the application is a user application or a system application with user-intended actions
//
//                String appName = (String) packageManager.getApplicationLabel(app);
//                String pkgName = app.packageName;
//                Drawable appIcon = packageManager.getApplicationIcon(app);
//               // if(!appName.contains("."))
//                    appInfoList.add(new ApplicationInformations(appName, pkgName, appIcon));
//
//
//        }
//
//        return appInfoList;
//    }

private List<ApplicationInformations> getInstalledApps() {
    List<ApplicationInformations> appInfoList = new ArrayList<>();
    PackageManager packageManager = getPackageManager();

    // Intent to check for launchable activities
    Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
    mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

    // Get a list of resolve info for launchable activities
    List<ResolveInfo> launchableApps = packageManager.queryIntentActivities(mainIntent, 0);

    for (ResolveInfo resolveInfo : launchableApps) {
        ApplicationInfo app = resolveInfo.activityInfo.applicationInfo;
        String appName = (String) packageManager.getApplicationLabel(app);
        String pkgName = app.packageName;
        Drawable appIcon = packageManager.getApplicationIcon(app);
        appInfoList.add(new ApplicationInformations(appName, pkgName, appIcon));
    }

    return appInfoList;
}


    private boolean isUserAccessibleSystemApp(PackageManager packageManager, ApplicationInfo app, Intent[] userIntents) {
        for (Intent intent : userIntents) {
            List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, 0);
            for (ResolveInfo resolveInfo : resolveInfos) {
                if (resolveInfo.activityInfo.packageName.equals(app.packageName)) {
                    return true;
                }
            }
        }
        return false;
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
