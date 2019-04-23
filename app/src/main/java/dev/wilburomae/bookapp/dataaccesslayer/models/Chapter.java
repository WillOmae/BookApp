package dev.wilburomae.bookapp.dataaccesslayer.models;

import android.text.SpannableString;

import java.util.ArrayList;

/**
 * Created by WILBUR OMAE on 30/03/2018.
 */
public class Chapter {
    private int chapterId;
    private String chapterTitle;
    private String chapterIntro;
    private String chapterContent;
    private SpannableString chapterContentFormatted = null;
    private static Chapter[] mChapters = null;

    public static ArrayList<String> lines = null;

    public Chapter(int chapterId, String chapterTitle, String chapterIntro, String chapterContent) {
        this.chapterId = chapterId;
        this.chapterTitle = chapterTitle;
        this.chapterIntro = chapterIntro;
        this.chapterContent = chapterContent;
    }

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public String getChapterTitle() {
        return chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    public String getChapterIntro() {
        return chapterIntro;
    }

    public void setChapterIntro(String chapterIntro) {
        this.chapterIntro = chapterIntro;
    }

    public String getChapterContent() {
        return chapterContent;
    }

    public void setChapterContent(String chapterContent) {
        this.chapterContent = chapterContent;
    }

    public SpannableString getChapterContentFormatted() {
        return (chapterContentFormatted == null) ? SpannableString.valueOf(chapterContent) : chapterContentFormatted;
    }

    public void setChapterContentFormatted(SpannableString chapterContentFormatted) {
        this.chapterContentFormatted = chapterContentFormatted;
    }

    public static Chapter get(int chapterId) {
        if (chapterId >= 0 && chapterId < mChapters.length) {
            return mChapters[chapterId];
        } else {
            return null;
        }
    }

    public static Chapter[] get() {
        return mChapters;
    }

    @Override
    public String toString() {
        return chapterTitle;
    }
}

