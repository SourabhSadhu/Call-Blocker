package com.call.block.group.model;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by sourabh on 6/4/17.
 */


public class AlertDialogWithImage extends DialogFragment {



    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("My Dialog")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Toast.makeText(getActivity(),"Cancel",Toast.LENGTH_SHORT);
                    }
                }).setPositiveButton("Play", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Toast.makeText(getActivity(),"Play",Toast.LENGTH_SHORT);
                    }
                }).create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

                // if you do the following it will be left aligned, doesn't look
                // correct
                // button.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_media_play,
                // 0, 0, 0);

                Drawable drawable = getActivity().getResources().getDrawable(
                        android.R.drawable.ic_media_play);

                // set the bounds to place the drawable a bit right
                drawable.setBounds((int) (drawable.getIntrinsicWidth() * 0.5),
                        0, (int) (drawable.getIntrinsicWidth() * 1.5),
                        drawable.getIntrinsicHeight());
                button.setCompoundDrawables(drawable, null, null, null);

                // could modify the placement more here if desired
                // button.setCompoundDrawablePadding();
            }
        });
        dialog.show();
        return dialog;
    }
}