package com.prac.appdrawer;

import static com.prac.appdrawer.MainActivity.appList;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.prac.appdrawer.adapters.AppAdapter;

import java.util.List;

public class MyBottomSheetDialogFragment extends BottomSheetDialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

        AppAdapter appAdapter;
//        List<ApplicationInformations> appList;
//        ListView listView = view.findViewById(R.id.package_list_view);
//        listView.setAdapter(adapter);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext().getApplicationContext()));
          recyclerView.setLayoutManager(new GridLayoutManager(requireContext().getApplicationContext(),3));


        appAdapter = new AppAdapter(requireContext().getApplicationContext(), appList, new AppAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ApplicationInformations appInfo) {
                launchApp(appInfo);
            }
        });
        recyclerView.setAdapter(appAdapter);

        view.findViewById(R.id.bottom_sheet_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }


    private void launchApp(ApplicationInformations appInfo) {
        Intent launchIntent = requireContext().getPackageManager().getLaunchIntentForPackage(appInfo.getPkgName());
        if (launchIntent != null) {
            startActivity(launchIntent);
        } else {
            Toast.makeText(requireContext(), "Unable to launch the app", Toast.LENGTH_SHORT).show();
        }
    }

}
