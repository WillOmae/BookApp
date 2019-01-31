package dev.wilburomae.bookapp.dataaccesslayer;

import android.content.res.AssetManager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import dev.wilburomae.bookapp.dataaccesslayer.models.Chapter;

public class XmlReader {
    /*chapters: publicly available array of chapters*/
    private static Chapter[] chapters;

    /*instantiate: populate the chapters array
    * assetManager allows access to the njia.xml file
    * return a boolean showing success or failure*/
    public static boolean instantiate(AssetManager assetManager) {
        try {
            InputStream inputStream = assetManager.open("njia.xml");
            XmlPullParser xmlPullParser = createPullParser(inputStream);
            if (xmlPullParser == null) return false;
            chapters = extractChapters(xmlPullParser);
            inputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*extractChapters: get details for all chapters from the xml file
    * xmlPullParser is the XmlPullParser object to be used
    * return an array of chapters*/
    private static Chapter[] extractChapters(XmlPullParser xmlPullParser) {
        ArrayList<Chapter> chapterTitles = new ArrayList<>();
        try {
            int xmlEventType = xmlPullParser.getEventType();
            String title = "", intro = "", content = "";
            while (xmlEventType != XmlPullParser.END_DOCUMENT) {
                if (xmlEventType == XmlPullParser.START_TAG) {
                    String tagName = xmlPullParser.getName();
                    switch (tagName) {
                        case "Name":
                            title = xmlPullParser.nextText();
                            break;
                        case "Title":
                            intro = xmlPullParser.nextText();
                            break;
                        case "Content":
                            content = xmlPullParser.nextText();
                            chapterTitles.add(new Chapter(-1, title, intro, content));
                            break;
                    }
                }
                xmlEventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return chapterTitles.toArray(new Chapter[0]);
    }

    /*createPullParser: separate the logic for the instantiation of an XmlPullParser object
    * inputStream is the underlying stream used by the XmlPullParser object to access the xml file
    * return an instance of XmlPullParser; or null if unsuccessful*/
    private static XmlPullParser createPullParser(InputStream inputStream) {
        try {
            XmlPullParser xmlPullParser = XmlPullParserFactory.newInstance().newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(inputStream, null);
            return xmlPullParser;
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Chapter[] getChapters() {
        return chapters;
    }
}
