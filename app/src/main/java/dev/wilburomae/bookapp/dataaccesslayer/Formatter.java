package dev.wilburomae.bookapp.dataaccesslayer;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dev.wilburomae.bookapp.views.VerseClickableSpan;

public class Formatter {

    private static ArrayList<VerseBounds> getVerseText(SpannableString spannableString) {
        ArrayList<VerseBounds> verseBoundsArrayList = new ArrayList<>();

        int start = -1, end;
        for (int i = 0; i < spannableString.length(); i++) {
            if (spannableString.charAt(i) == '“') {
                start = i;
            } else if (spannableString.charAt(i) == '”') {
                end = i;
                if (start != -1) {
                    VerseBounds verseBounds = new VerseBounds(start, end);
                    verseBoundsArrayList.add(verseBounds);
                }
            }
        }
        return verseBoundsArrayList;
    }

    private static SpannableString formatVerseText(SpannableString spannableString) {
        ArrayList<VerseBounds> verseBoundsArrayList = getVerseText(spannableString);
        for (VerseBounds verseBounds : verseBoundsArrayList) {
            final StyleSpan boldItalicSpan = new StyleSpan(Typeface.BOLD_ITALIC);
            spannableString.setSpan(boldItalicSpan, verseBounds.getStart(), verseBounds.getEnd(), 0);
        }
        return spannableString;
    }

    private static SpannableString formatVerseRef(SpannableString spannableString) {
        /*one regex to rule them all*/
        Pattern verseRegex = Pattern.compile("(?<!\\{)((\\d ?)?[a-zA-Z]{2,}\\.? ?(?=\\d{1,3}))(([,;:\\-] ?)?\\d{1,3})+(?!\\{)");
        Matcher verseMatcher = verseRegex.matcher(spannableString);
        while (verseMatcher.find()) {
            final VerseClickableSpan clickableSpan = new VerseClickableSpan();
            spannableString.setSpan(clickableSpan, verseMatcher.start(), verseMatcher.end(), 0);
        }
        return spannableString;
    }

    private static ArrayList<VerseBounds> getParRef(SpannableString spannableString) {
        ArrayList<VerseBounds> verseBoundsArrayList = new ArrayList<>();
        int start = -1;
        int end = -1;
        for (int i = 0; i < spannableString.length(); i++) {
            if (spannableString.charAt(i) == '{') {
                start = i;
            } else if (spannableString.charAt(i) == '}') {
                end = i + 1;
                if (start != -1 && ((end - start) < 10)) {
                    VerseBounds verseBounds = new VerseBounds(start, end);
                    verseBoundsArrayList.add(verseBounds);
                }
            }
        }
        return verseBoundsArrayList;
    }

    private static SpannableString formatParRef(SpannableString spannableString) {
        ArrayList<VerseBounds> verseBoundsArrayList = getParRef(spannableString);
        for (VerseBounds verseBounds : verseBoundsArrayList) {
            final StyleSpan italicSpan = new StyleSpan(Typeface.ITALIC);
            spannableString.setSpan(italicSpan, verseBounds.getStart(), verseBounds.getEnd(), 0);
        }
        return spannableString;
    }

    public static SpannableString format(SpannableString spannableString) {
        return formatVerseRef(formatParRef(formatVerseText(spannableString)));
    }
}
