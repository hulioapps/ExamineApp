package com.apps.hulios.examineapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.apps.hulios.examineapp.R;

/**
 * Created by Boss on 2015-07-17.
 */
public class TitleDialog extends DialogFragment {
    public static TitleDialog newInstance(String title){
        Bundle args = new Bundle();
        args.putString("title",title);
        TitleDialog dialog = new TitleDialog();
        dialog.setArguments(args);
        return dialog;
    }
    public TitleDialog() {
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getArguments().getString("title"))
                .setTitle(R.string.title_dialog_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        return builder.create();
    }
}

