package com.nakhmedov.finance.ui.components;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;

import com.nakhmedov.finance.R;
import com.nakhmedov.finance.constants.ContextConstants;
import com.nakhmedov.finance.constants.PrefLab;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/7/17
 * Time: 4:38 PM
 * To change this template use File | Settings | File Templates
 */

public class UpdateAppDialog extends DialogFragment {

    boolean isNeverCheck = false;

    public UpdateAppDialog() {
    }

    public void showUpdateDialog(final Context mContext) {

        if (mContext == null)
            return;

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getResources().getString(R.string.new_version_available));
        builder.setMultiChoiceItems(R.array.check_to_update, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                isNeverCheck = isChecked;
            }
        });
        builder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                prefs.edit().putBoolean(PrefLab.NTFY_NEW_VERSION, !isNeverCheck).apply();
                Uri marketUri = Uri.parse("market://details?id=" + ContextConstants.PACKAGE_NAME);
                Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                try {
                    mContext.startActivity(marketIntent);
                } catch (android.content.ActivityNotFoundException error) {//if playMarket is not installed then, opens browser
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + ContextConstants.PACKAGE_NAME)));
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                prefs.edit().putBoolean(PrefLab.NTFY_NEW_VERSION, !isNeverCheck).apply();
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }
}
