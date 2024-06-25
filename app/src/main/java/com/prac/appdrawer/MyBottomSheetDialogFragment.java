package com.prac.appdrawer;

import static com.prac.appdrawer.MainActivity.appList;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.prac.appdrawer.adapters.AppAdapter;

public class MyBottomSheetDialogFragment extends BottomSheetDialogFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext().getApplicationContext(), 6, GridLayoutManager.HORIZONTAL, false));

        AppAdapter appAdapter = new AppAdapter(requireContext().getApplicationContext(), appList, new AppAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ApplicationInformations appInfo) {
                launchApp(appInfo);
            }
        });
        recyclerView.setAdapter(appAdapter);

        view.findViewById(R.id.bottom_sheet_button).setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        if (dialog != null) {
            View bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
                bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                behavior.setSkipCollapsed(true);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }
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
