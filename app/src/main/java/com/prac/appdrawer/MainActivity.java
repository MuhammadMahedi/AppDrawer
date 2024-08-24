package com.prac.appdrawer;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.content.pm.PermissionInfo;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.format.Formatter;
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

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
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
                String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

                Log.e("CheckingSwipe", "Serial : " +androidId);
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

//    public static long getAppSize(Context context, String packageName) {
//        try {
//            PackageManager packageManager = context.getPackageManager();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
//                ApplicationInfo applicationInfo = packageInfo.applicationInfo;
//                StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
//                Method getStorageStatsMethod = storageManager.getClass().getMethod("queryStatsForPackage", String.class, ApplicationInfo.class);
//                Object storageStats = getStorageStatsMethod.invoke(storageManager, applicationInfo.storageUuid, packageName);
//                Method getCodeBytesMethod = storageStats.getClass().getMethod("getCodeBytes");
//                Method getDataBytesMethod = storageStats.getClass().getMethod("getDataBytes");
//                Method getCacheBytesMethod = storageStats.getClass().getMethod("getCacheBytes");
//                long codeSize = (long) getCodeBytesMethod.invoke(storageStats);
//                long dataSize = (long) getDataBytesMethod.invoke(storageStats);
//                long cacheSize = (long) getCacheBytesMethod.invoke(storageStats);
//                return codeSize + dataSize + cacheSize;
//            } else {
//                return 0;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return -1;
//        }
//    }
//
//    public static UsageStats getAppUsageStats(Context context, String packageName) {
//        // Get the UsageStatsManager
//        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
//
//        // Define the time range to query usage stats
//        Calendar calendar = Calendar.getInstance();
//        long endTime = calendar.getTimeInMillis();
//        calendar.add(Calendar.DAY_OF_MONTH, -1);
//        long startTime = calendar.getTimeInMillis();
//
//        // Query the usage stats
//        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
//
//        // Find the UsageStats for the specified package name
//        for (UsageStats usageStats : usageStatsList) {
//            if (usageStats.getPackageName().equals(packageName)) {
//                return usageStats;
//            }
//        }
//
//        // If no usage stats found for the package, return null
//        return null;
//    }


//    final PackageManager pm = context.getPackageManager();
//    ApplicationInfo applicationInfo = pm.getApplicationInfo(context.getPackageName(), 0);
//    File file = new File(applicationInfo.publicSourceDir);
//    long size = file.length();

    public String getAppSize(String packageName) {
        long appSize = 0;

        try {
            ApplicationInfo appInfo = this.getPackageManager().getApplicationInfo(packageName, 0);

            Log.e("AppSize-sourceDir initial", "getAppSize: "+ appSize );
            // Get App File size
            appSize += getFileSize(new File(appInfo.sourceDir));
            Log.e("AppSize-sourceDir", "getAppSize: "+ appSize );

            // Get App Data size
            appSize += getFileSize(new File(appInfo.dataDir));
            Log.e("AppSize-dataDir", "getAppSize: "+ appSize );

            // Get App Cache size
            appSize += getFileSize(this.getCacheDir());
            Log.e("AppSize-cacheDir", "getAppSize: "+ appSize );

            // Get External Cache size if exists
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                appSize += getFileSize(this.getExternalCacheDir());
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return Formatter.formatFileSize(MainActivity.this, appSize);
    }

    private long getFileSize(File file) {
        long size = 0;
        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                for (File child : file.listFiles()) {
                    size += getFileSize(child);
                }
            } else {
                size += file.length();
            }
        }
        return size;
    }
}

