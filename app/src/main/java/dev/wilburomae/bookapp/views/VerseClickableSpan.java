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

    class VerseDownloader extends AsyncTask<String, String, String> {
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
                    URL url = new URL(params[0] + VerseMapper.buildVerse(params[1]));
                    Log.e("url", url.toString());
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.setRequestMethod("GET");
                    int responseCode = httpURLConnection.getResponseCode();
                    Log.e("response-code", "Code: " + responseCode);
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

        String readStream(InputStream inputStream) {
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
            Log.e("stringBuilder", stringBuilder.toString());
            return stringBuilder.toString();
        }
    }

    static class VerseMapper {
        /**
         * An array containing Bible book titles in English.
         */
        static String[] english = new String[]{
                "Genesis", "Exodus", "Leviticus", "Numbers",
                "Deuteronomy", "Joshua", "Judges", "Ruth",
                "1 Samuel", "2 Samuel", "1 Kings", "2 Kings",
                "1 Chronicles", "2 Chronicles", "Ezra", "Nehemiah",
                "Esther", "Job", "Psalms", "Proverbs",
                "Ecclesiastes", "Song of Solomon", "Isaiah", "Jeremiah",
                "Lamentations", "Ezekiel", "Daniel", "Hosea",
                "Joel", "Amos", "Obadiah", "Jonah",
                "Micah", "Nahum", "Habakkuk", "Zephaniah",
                "Haggai", "Zechariah", "Malachi", "Matthew",
                "Mark", "Luke", "John", "Acts",
                "Romans", "1 Corinthians", "2 Corinthians", "Galatians",
                "Ephesians", "Philippians", "Colossians", "1 Thessalonians",
                "2 Thessalonians", "1 Timothy", "2 Timothy", "Titus",
                "Philemon", "Hebrews", "James", "1 Peter",
                "2 Peter", "1 John", "2 John", "3 John",
                "Jude", "Revelation"
        };
        /**
         * An array containing Bible book titles in Swahili.
         */
        static String[] swahili = new String[]{
                "Mwanzo", "Kutoka", "Walawi", "Hesabu",
                "Kumbukumbu la Torati", "Yoshua", "Waamuzi", "Ruthu",
                "1 Samueli", "2 Samueli", "1 Wafalme", "2 Wafalme",
                "1 Mambo ya Nyakati", "2 Mambo ya Nyakati", "Ezra", "Nehemia",
                "Esta", "Ayubu", "Zaburi", "Methali",
                "Mhubiri", "Wimbo Ulio Bora", "Isaya", "Yeremia",
                "Maombolezo", "Ezekieli", "Danieli", "Hosea",
                "Yoeli", "Amosi", "Obadia", "Yona",
                "Mika", "Nahumu", "Habakuki", "Sefania",
                "Hagai", "Zekaria", "Malaki", "Mathayo",
                "Marko", "Luka", "Yohana", "Matendo ya Mitume",
                "Warumi", "1 Wakorintho", "2 Wakorintho", "Wagalatia",
                "Waefeso", "Wafilipi", "Wakolosai", "1 Wathesalonike",
                "2 Wathesalonike", "1 Timotheo", "2 Timotheo", "Tito",
                "Filemoni", "Waebrania", "Yakobo", "1 Petero",
                "2 Petero", "1 Yohana", "2 Yohana", "3 Yohana",
                "Yuda", "Ufunuo wa Yohana"
        };

        /**
         * Maps a Swahili Bible book title to its English equivalent.
         *
         * @param needle The Bible book title in Swahili.
         * @return The Bible book title in English.
         */
        static String mapToEnglish(String needle) {
            if (needle == null) return null;

            String[] cleanSwahiliArray = removeNonLettersFromArray(swahili);
            String[] cleanEnglishArray = removeNonLettersFromArray(english);
            String cleanNeedle = removeNonLetters(needle.toLowerCase());

            for (int i = 0; i < cleanSwahiliArray.length; i++) {
                if (cleanSwahiliArray[i].toLowerCase().equals(cleanNeedle))
                    return cleanEnglishArray[i];
            }
            if (cleanNeedle.length() > 3) {
                for (int i = 0; i < cleanSwahiliArray.length; i++) {
                    if (cleanSwahiliArray[i].toLowerCase().substring(0, 4).equals(cleanNeedle.substring(0, 4)))
                        return cleanEnglishArray[i];
                }
            }
            if (cleanNeedle.length() > 2) {
                for (int i = 0; i < cleanSwahiliArray.length; i++) {
                    if (cleanSwahiliArray[i].toLowerCase().substring(0, 3).equals(cleanNeedle.substring(0, 3)))
                        return cleanEnglishArray[i];
                }
            }
            return null;
        }

        /**
         * Remove non-letter characters from a string save the beginning digit if applicable.
         *
         * @param dirty The string to be cleaned.
         * @return The cleaned string.
         */
        static String removeNonLetters(String dirty) {
            if (dirty == null) return null;
            StringBuilder stringBuilder = new StringBuilder();
            boolean foundLetter = false;
            for (int i = 0; i < dirty.length(); i++) {
                char testChar = dirty.charAt(i);
                if (!foundLetter && Character.isDigit(testChar)) {
                    stringBuilder.append(testChar);
                } else if (Character.isLetter(testChar)) {
                    stringBuilder.append(testChar);
                    foundLetter = true;
                }
            }
            return stringBuilder.toString();
        }

        /**
         * Generates a string array whose contents lack non-letter characters save the beginning digit if applicable.
         *
         * @param dirty The string array whose contents are to be cleaned.
         * @return The cleaned string array.
         */
        static String[] removeNonLettersFromArray(String[] dirty) {
            if (dirty == null) return null;
            String[] clean = new String[dirty.length];
            for (int i = 0; i < dirty.length; i++) {
                clean[i] = removeNonLetters(dirty[i]);
            }
            return clean;
        }

        /**
         * Extract the chapter and verse text from a string.
         * e.g. from Hebrews 1:1-2;3:5 the method returns 1:1-2;3:5
         *
         * @param needle The string to be parsed for chapter and verse text.
         * @return The chapter and verse text.
         */
        static String getChapterVerseText(String needle) {
            StringBuilder stringBuilder = new StringBuilder();
            boolean foundLetter = false;
            for (int i = 0; i < needle.length(); i++) {
                char testChar = needle.charAt(i);
                if (foundLetter && (
                        Character.isDigit(testChar)) ||
                        testChar == ':' ||
                        testChar == ';' ||
                        testChar == '-' ||
                        testChar == ',') {
                    stringBuilder.append(testChar);
                } else if (Character.isLetter(testChar)) {
                    foundLetter = true;
                }
            }
            return stringBuilder.toString();
        }

        /**
         * Generate a proper Bible reference.
         *
         * @param needle The string to be parsed for a Bible reference.
         * @return The string containing a Bible reference.
         */
        static String buildVerse(String needle) {
            String book = VerseMapper.removeNonLetters(needle);
            String chapterVerse = getChapterVerseText(needle);
            String englishBook = VerseMapper.mapToEnglish(book);
            return englishBook + chapterVerse;
        }
    }
}
