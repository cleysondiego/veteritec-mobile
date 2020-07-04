package br.com.veteritec.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import br.com.veteritec.R;

public class LoadingDialog {

    private Activity activity;
    private AlertDialog dialog;

    public LoadingDialog(Activity activity) {
        this.activity = activity;
    }

    public void startLoadingDialog() {
        AlertDialog.Builder loadingDialog = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        loadingDialog.setView(inflater.inflate(R.layout.loading_dialog, null));
        loadingDialog.setCancelable(false);

        dialog = loadingDialog.create();
        dialog.show();
    }

    public void dismissLoadingDialog() {
        dialog.dismiss();
    }
}
