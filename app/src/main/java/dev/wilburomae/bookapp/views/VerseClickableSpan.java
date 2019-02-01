package dev.wilburomae.bookapp.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

public class VerseClickableSpan extends ClickableSpan {
    @Override
    public void onClick(@NonNull View view) {
        TextView textView = (TextView) view;
        CharSequence charSequence = textView.getText();
        String textString = charSequence.toString();
        Spannable spannable = new SpannableString(charSequence);
        int start = spannable.getSpanStart(this);
        int end = spannable.getSpanEnd(this);
        String clickedVerse = textString.substring(start, end);

        String verseText = downloadVerseText(clickedVerse);
        displayVerse(textView.getContext(), clickedVerse, verseText);
    }

    private void displayVerse(Context context, String ref, String verseText) {
        //TODO call a Bible API in the method that follows, then uncomment the section below to display
//        AlertDialog verseDisplayDialog = new AlertDialog.Builder(context).
//                setTitle(ref).
//                setMessage(verseText).
//                setNegativeButton("CLOSE", null).
//                create();
//        verseDisplayDialog.setCanceledOnTouchOutside(true);
//        verseDisplayDialog.show();
    }

    private String downloadVerseText(String clickedVerse) {
        //TODO call a Bible API
        return "verse text";
    }
}
