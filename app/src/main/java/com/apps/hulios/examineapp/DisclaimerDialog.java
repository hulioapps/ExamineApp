package com.apps.hulios.examineapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by Boss on 2015-07-24.
 */
public class DisclaimerDialog extends DialogFragment {
    public static DisclaimerDialog newInstance(){
        Bundle args = new Bundle();
        DisclaimerDialog dialog = new DisclaimerDialog();
        dialog.setArguments(args);
        return dialog;
    }
    public DisclaimerDialog() {
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_disclaimer_text)
                .setTitle(R.string.dialog_disclaimer_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        return builder.create();
    }
}

