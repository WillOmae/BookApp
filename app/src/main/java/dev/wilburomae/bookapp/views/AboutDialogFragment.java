package dev.wilburomae.bookapp.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

public class AboutDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("This app...");
        builder.setMessage("...was developed to point every troubled heart to Jesus the Way, " +
                "making use of text from a translation of the best-selling book 'Steps to Christ' by Ellen Gould White. " +
                "Many thanks to W. O. and J. O.\n\n" +
                "From the developers: softtech4mobile@gmail.com");
        builder.setCancelable(true);
        return builder.create();
    }
}
