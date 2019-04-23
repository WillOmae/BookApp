package dev.wilburomae.bookapp.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import dev.wilburomae.bookapp.R;

public class ColorDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ReaderActivity readerActivity = (ReaderActivity) getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(readerActivity);
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View layoutView = layoutInflater.inflate(R.layout.color_dialog, null);
        ImageView[] highlightButtons = new ImageView[4];
        highlightButtons[0] = layoutView.findViewById(R.id.reader_fab_highlight_blue);
        highlightButtons[1] = layoutView.findViewById(R.id.reader_fab_highlight_red);
        highlightButtons[2] = layoutView.findViewById(R.id.reader_fab_highlight_green);
        highlightButtons[3] = layoutView.findViewById(R.id.reader_fab_highlight_yellow);
        for (int i = 0; i < 4; i++) {
            highlightButtons[i].setOnClickListener(readerActivity);
        }

        builder.setTitle("Highlight colour");
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getDialog().cancel();
            }
        });
        builder.setView(layoutView);
        return builder.create();
    }
}
