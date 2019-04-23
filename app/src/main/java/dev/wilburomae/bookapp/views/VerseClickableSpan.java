package dev.wilburomae.bookapp.views;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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

        displayVerse(textView.getContext(), clickedVerse);
    }

    private void displayVerse(Context context, String ref) {
        String url = "http://getbible.net/json?version=swahili&passage=";
        VerseDownloader verseDownloader = new VerseDownloader(context);
        verseDownloader.execute(url, ref);
    }

    public class VerseDownloader extends AsyncTask<String, String, String> {
        private Context mContext;
        private String mRef = null;

        VerseDownloader(Context context) {
            mContext = context;
        }

        @Override
        protected String doInBackground(String... params) {
            String response = null;
            if (params.length == 2) {
                mRef = params[1];
                HttpURLConnection httpURLConnection = null;
                try {
                    URL url = new URL(params[0] + params[1].replace(" ", ""));
                    Log.e("url", url.toString());
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.setRequestMethod("GET");
                    response = readStream(httpURLConnection.getInputStream());
                    Log.e("response", response);
                } catch (MalformedURLException e) {
                    Log.e("exception", e.getMessage());
                } catch (IOException e) {
                    Log.e("exception", e.getMessage());
                } finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                    Log.e("finally", "closing");
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String verseText) {
            super.onPostExecute(verseText);
            if (verseText != null) {
                AlertDialog verseDisplayDialog = new AlertDialog.Builder(mContext).
                        setTitle(mRef).
                        setMessage(verseText).
                        setNegativeButton("CLOSE", null).
                        create();
                verseDisplayDialog.setCanceledOnTouchOutside(true);
                verseDisplayDialog.show();
            }
        }

        private String readStream(InputStream inputStream) {
            char EOF = (char) -1;
            char in = 0;
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            StringBuilder stringBuilder = new StringBuilder();
            while (true) {
                try {
                    in = (char) inputStreamReader.read();
                    if (in == EOF) {
                        break;
                    }
                    stringBuilder.append(in);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return stringBuilder.toString();
        }
    }
}
