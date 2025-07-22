package com.walkee.walkee.Dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.walkee.walkee.R;
import com.walkee.walkee.databinding.ToastCustomBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.NotNull;

public class BottomSheetDialogCustom extends BottomSheetDialogFragment {

    View view;
    LayoutInflater inflaters;
    ToastCustomBinding binding;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @NotNull ViewGroup container, @NotNull Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.bottom_sheet_dialog, container, false);

        return view;
    }

//    @Override
//    public void show(FragmentManager manager, String tag){
//        FragmentTransaction ft = manager.beginTransaction();
//        ft.add(this, tag);
//        ft.addToBackStack(null);
//        ft.commit();
//    }
}
