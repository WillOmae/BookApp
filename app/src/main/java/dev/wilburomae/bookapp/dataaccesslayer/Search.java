package dev.wilburomae.bookapp.dataaccesslayer;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;

import java.util.ArrayList;

import dev.wilburomae.bookapp.dataaccesslayer.models.Chapter;

public class Search {
    public static class SearchResult {
        private String searchText;
        private String chapter;
        private String ref;
        private SpannableString snippet;
        private int chapterNumber;


        /**
         * A class defining a search result.
         *
         * @param searchText    the search text
         * @param chapter       the chapter title
         * @param ref           the paragraph reference
         * @param snippet       the entire sentence in which the search text was found
         * @param chapterNumber the chapter number
         */
        SearchResult(String searchText, String chapter, String ref, SpannableString snippet, int chapterNumber) {
            this.searchText = searchText;
            this.chapter = chapter;
            this.ref = ref;
            this.snippet = snippet;
            this.chapterNumber = chapterNumber;
        }

        public String getSearchText() {
            return searchText;
        }

        public String getChapter() {
            return chapter;
        }

        public String getRef() {
            return ref;
        }

        public SpannableString getSnippet() {
            return snippet;
        }

        public int getChapterNumber() {
            return chapterNumber;
        }
    }

    public static ArrayList<Search.SearchResult> search(String needle) {
        Chapter[] chapters = XmlReader.getChapters();
        ArrayList<SearchResult> searchResults = new ArrayList<>();

        for (int i = 0; i < chapters.length; i++) {
            Chapter chapter = chapters[i];
            String content = chapter.getChapterContent();
            // use lower case for case insensitivity
            String needleLowerCase = needle.toLowerCase();
            int needleIndex = content.toLowerCase().indexOf(needleLowerCase);
            if (needleIndex != -1) {
                VerseBounds refBounds = findRefBounds(needleIndex, content);
                VerseBounds snippetBounds = findSnippetBounds(needleIndex, content);

                String ref = (refBounds != null) ? content.substring(refBounds.getStart(), refBounds.getEnd()) : null;
                String snippet = (snippetBounds != null) ? String.format("...%s...", content.substring(snippetBounds.getStart(), snippetBounds.getEnd())) : null;
                if (snippet != null) {
                    SpannableString spannedSnippet = new SpannableString(snippet);
                    String snippetLowerCase = snippet.toLowerCase();

                    int highlightStart = snippetLowerCase.indexOf(needleLowerCase);
                    int highlightEnd = highlightStart + needle.length();
                    spannedSnippet.setSpan(new BackgroundColorSpan(Color.YELLOW), highlightStart, highlightEnd, 0);
                    spannedSnippet.setSpan(new ForegroundColorSpan(Color.DKGRAY), highlightStart, highlightEnd, 0);

                    searchResults.add(new SearchResult(needle, chapter.getChapterTitle(), ref, spannedSnippet, i));
                }
            }
        }
        return searchResults;
    }

    private static VerseBounds findRefBounds(int needleIndex, String content) {
        int refStart = -1, refEnd = -1;
        for (int i = needleIndex; i < content.length(); i++) {
            if (content.charAt(i) == '{') {
                refStart = i;
            } else if ((content.charAt(i) == '}') && (refStart != -1)) {
                refEnd = i + 1;
                break;
            }
        }
        if (refEnd != -1) {
            return new VerseBounds(refStart, refEnd);
        } else {
            return null;
        }
    }

    private static VerseBounds findSnippetBounds(int needleIndex, String content) {
        int snippetStart = (needleIndex >= 9) ? (needleIndex - 9) : 0;
        int snippetEnd = (((content.length() - 1) - (needleIndex + 10)) > 0) ? (needleIndex + 10) : (content.length() - 1);
        return snippetStart != -1 && snippetEnd != -1 ? new VerseBounds(snippetStart, snippetEnd) : null;
    }
}
