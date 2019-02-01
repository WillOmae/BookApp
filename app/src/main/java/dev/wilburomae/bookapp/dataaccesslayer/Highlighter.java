package dev.wilburomae.bookapp.dataaccesslayer;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Highlighter {
    public class HighlightInfo {
        private int chapter;
        private VerseBounds verseBounds;
        private int colorInt;

        HighlightInfo(int chapter, VerseBounds verseBounds, int colorInt) {
            this.chapter = chapter;
            this.verseBounds = verseBounds;
            this.colorInt = colorInt;
        }

        public int getChapter() {
            return chapter;
        }

        public VerseBounds getVerseBounds() {
            return verseBounds;
        }

        public int getColorInt() {
            return colorInt;
        }

        @Override
        public boolean equals(Object object) {
            if (object.getClass() == this.getClass()) {
                HighlightInfo toCompare = (HighlightInfo) object;
                return this.chapter == toCompare.chapter &&
                        this.verseBounds.equals(toCompare.verseBounds) &&
                        this.colorInt == toCompare.colorInt;
            }
            return false;
        }

        @NonNull
        @Override
        public String toString() {
            return "HighlightInfo{" +
                    "chapter=" + chapter +
                    ", verseBounds=" + verseBounds +
                    ", colorInt=" + colorInt +
                    '}';
        }
    }

    private final String FILE_NAME = "HIGHLIGHTER";
    private final String START_TAG = "-S";
    private final String SPACE = " ";
    private final String EMPTY = "";
    private Context context;

    public Highlighter(Context context) {
        this.context = context;
    }

    public void saveHighlight(int chapter, VerseBounds verseBounds, int colorInt) {
        write(new HighlightInfo(chapter, verseBounds, colorInt));
    }

    public ArrayList<HighlightInfo> getHighlights() {
        return read();
    }

    private void write(HighlightInfo highlightInfo) {
        try {
            /* STRUCTURE
             * -S{SPACE}int{SPACE}int{SPACE}int{SPACE}int{SPACE}
             * start tag, chapter, start bound, end bound, color
             */
            ArrayList<HighlightInfo> highlightInfos = getHighlights();
            highlightInfos.add(highlightInfo);
            FileOutputStream fileOutputStream = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            byte[] bytesSpace = SPACE.getBytes();
            byte[] bytesStartTag = START_TAG.getBytes();
            for (HighlightInfo h : highlightInfos) {
                byte[] bytesChapter = Integer.toString(h.getChapter()).getBytes();
                byte[] bytesStartBound = Integer.toString(h.getVerseBounds().getStart()).getBytes();
                byte[] bytesEndBound = Integer.toString(h.getVerseBounds().getEnd()).getBytes();
                byte[] bytesColor = (Integer.toString(h.getColorInt())).getBytes();
                fileOutputStream.write(bytesStartTag);
                fileOutputStream.write(bytesSpace);
                fileOutputStream.write(bytesChapter);
                fileOutputStream.write(bytesSpace);
                fileOutputStream.write(bytesStartBound);
                fileOutputStream.write(bytesSpace);
                fileOutputStream.write(bytesEndBound);
                fileOutputStream.write(bytesSpace);
                fileOutputStream.write(bytesColor);
                fileOutputStream.write(bytesSpace);
            }
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<HighlightInfo> read() {
        final int EOF = -1;
        ArrayList<HighlightInfo> highlightInfos = new ArrayList<>();
        try {
            FileInputStream fileInputStream = context.openFileInput(FILE_NAME);
            StringBuilder sb = new StringBuilder();
            byte b;
            do {
                b = (byte) fileInputStream.read();
                sb.append((char) b);
            } while (b != EOF);
            highlightInfos = parseString(sb.toString());
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return highlightInfos;
    }

    private ArrayList<HighlightInfo> parseString(String stringToParse) {
        /* STRUCTURE
         * -S{SPACE}int{SPACE}int{SPACE}int{SPACE}int{SPACE}
         * start tag, chapter, start bound, end bound, color
         */
        ArrayList<HighlightInfo> highlightInfos = new ArrayList<>();
        String[] infoSections = stringToParse.split(START_TAG);
        for (String infoSection : infoSections) {
            String s = infoSection.replace(START_TAG, EMPTY);
            String[] numbers = s.split(SPACE);

            if (numbers.length >= 4) {
                int[] numbersProper = new int[4];
                for (int i = 0, j = 0; i < numbers.length; i++) {
                    if (!numbers[i].equals(SPACE)) {
                        try {
                            numbersProper[j] = Integer.parseInt(numbers[i].replace(SPACE, EMPTY));
                            j++;
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }
                int chapter = numbersProper[0];
                int start = numbersProper[1];
                int end = numbersProper[2];
                int colorInt = numbersProper[3];

                VerseBounds verseBounds = new VerseBounds(start, end);
                HighlightInfo highlightInfo = new HighlightInfo(chapter, verseBounds, colorInt);
                highlightInfos.add(highlightInfo);
            }
        }
        return highlightInfos;
    }

    private void delete(HighlightInfo highlightInfo) {

    }
}
