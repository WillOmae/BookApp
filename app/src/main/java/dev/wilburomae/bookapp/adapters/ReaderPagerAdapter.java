package dev.wilburomae.bookapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Layout;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import dev.wilburomae.bookapp.R;
import dev.wilburomae.bookapp.dataaccesslayer.Highlighter;
import dev.wilburomae.bookapp.dataaccesslayer.VerseBounds;
import dev.wilburomae.bookapp.dataaccesslayer.models.Chapter;
import dev.wilburomae.bookapp.views.ReaderActivity;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

/**
 * Created by WILBUR OMAE on 01/04/2018.
 */
public class ReaderPagerAdapter extends PagerAdapter {
    final private BackgroundColorSpan mBackgroundColorSpan = new BackgroundColorSpan(Color.BLUE);
    final private ForegroundColorSpan mForegroundColorSpan = new ForegroundColorSpan(Color.WHITE);
    private Chapter[] mChapters;
    private LayoutInflater mLayoutInflater;
    private Highlighter mHighlighter;
    private GestureDetector mGestureDetector;
    private boolean mHighlightActive = false;
    private TextView mWorkingTextView;
    private int mWorkingIndex;
    private ArrayList<Highlighter.HighlightInfo> mRestoredHighlights;
    private ViewPager mViewPager = null;
    private VerseBounds mHighlightBounds = null;
    private ReaderActivity mReaderActivity;
    private boolean mCanAccessInternet;

    public ReaderPagerAdapter(ReaderActivity readerActivity, Chapter[] chapters) {
        SimplifiedDoubleClickListener doubleClickListener = new SimplifiedDoubleClickListener();
        this.mReaderActivity = readerActivity;
        this.mLayoutInflater = (LayoutInflater) readerActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mChapters = chapters;
        this.mHighlighter = new Highlighter(readerActivity);
        this.mGestureDetector = new GestureDetector(readerActivity, doubleClickListener);
        this.mGestureDetector.setOnDoubleTapListener(doubleClickListener);
        this.mRestoredHighlights = mHighlighter.getHighlights();
        this.mReaderActivity.showHighlightButton(false);
        this.mCanAccessInternet = readerActivity.getInternetPermission();
    }

    @Override
    public int getCount() {
        return mChapters.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
        if (mViewPager == null) {
            mViewPager = (ViewPager) container;
            mReaderActivity.setToolbarText(mChapters[position].getChapterTitle());
            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {

                }

                @Override
                public void onPageSelected(int i) {
                    unHighlightDClickedWord();
                    mHighlightActive = false;
                    mReaderActivity.setToolbarText(mChapters[i].getChapterTitle());
                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });
        }
        Chapter chapter = mChapters[position];
        View view = this.mLayoutInflater.inflate(R.layout.reader_fragment_content, container, false);

        TextView tvContent = view.findViewById(R.id.reader_fragment_content_textView);
        TextView tvTitle = view.findViewById(R.id.reader_fragment_content_title);

        tvTitle.setText(chapter.getChapterIntro());
        tvContent.setMovementMethod(LinkMovementMethod.getInstance());
        tvContent.setText(chapter.getChapterContentFormatted());

        tvContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mWorkingTextView = (TextView) v;
                mWorkingIndex = position;
                mGestureDetector.onTouchEvent(event);
                return false;
            }
        });
        restoreHighlights(tvContent, position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mChapters[position].getChapterTitle();
    }

    private void highlightDClickedWord(int x, int y) {
        VerseBounds clickedWordBounds = getDClickedWord(x, y);
        if (clickedWordBounds != null) {
            if (setDClickedBounds(clickedWordBounds)) {
                SpannableString spannableString = SpannableString.valueOf(mWorkingTextView.getText());

                spannableString.setSpan(mBackgroundColorSpan, mHighlightBounds.getStart(), mHighlightBounds.getEnd(), 0);
                spannableString.setSpan(mForegroundColorSpan, mHighlightBounds.getStart(), mHighlightBounds.getEnd(), 0);
                mWorkingTextView.setText(spannableString);
                mReaderActivity.showHighlightButton(true);
            }
        }
    }

    private boolean setDClickedBounds(VerseBounds wordBound) {
        boolean hasChanged = false;
        if (mHighlightBounds == null) {
            mHighlightBounds = wordBound;
            hasChanged = true;
        }
        if (mHighlightBounds.getStart() > wordBound.getStart()) {
            mHighlightBounds.setStart(wordBound.getStart());
            hasChanged = true;
        }
        if (mHighlightBounds.getEnd() < wordBound.getEnd()) {
            mHighlightBounds.setEnd(wordBound.getEnd());
            hasChanged = true;
        }
        return hasChanged;
    }

    private VerseBounds getDClickedWord(int x, int y) {
        try {
            Layout layout = mWorkingTextView.getLayout();
            int line = layout.getLineForVertical(y);
            int positionInLine = layout.getOffsetForHorizontal(line - 1, x);
            String text = mWorkingTextView.getText().toString();
            int startIndex = 0, endIndex = text.length() - 1;
            for (int i = positionInLine; i >= 0; i--) {
                if (!Character.isLetter(text.charAt(i)) && !Character.isDigit(text.charAt(i)) && i != positionInLine) {
                    startIndex = i + 1;
                    break;
                }
            }
            for (int i = positionInLine; i < text.length(); i++) {
                if (Character.isWhitespace(text.charAt(i))) {
                    endIndex = i;
                    break;
                }
            }

            return new VerseBounds(startIndex, endIndex);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    private void unHighlightDClickedWord() {
        if (mHighlightBounds != null) {
            SpannableString spannableString = SpannableString.valueOf(mWorkingTextView.getText());
            spannableString.removeSpan(mBackgroundColorSpan);
            spannableString.removeSpan(mForegroundColorSpan);
            mWorkingTextView.setText(spannableString);
            mReaderActivity.showHighlightButton(false);
        }
    }

    public void permanentHighlight(int highlightId) {
        if (mHighlightBounds != null) {
            int colorInt;
            switch (highlightId) {
                case R.id.reader_fab_highlight_blue:
                    colorInt = Color.CYAN;
                    break;
                case R.id.reader_fab_highlight_red:
                    colorInt = Color.MAGENTA;
                    break;
                case R.id.reader_fab_highlight_green:
                    colorInt = Color.GREEN;
                    break;
                case R.id.reader_fab_highlight_yellow:
                default:
                    colorInt = Color.YELLOW;
                    break;
            }
            BackgroundColorSpan highlightSpan = new BackgroundColorSpan(colorInt);
            SpannableString spannableString = SpannableString.valueOf(mWorkingTextView.getText());
            spannableString.setSpan(highlightSpan, mHighlightBounds.getStart(), mHighlightBounds.getEnd(), 0);
            spannableString.removeSpan(mForegroundColorSpan);
            mWorkingTextView.setText(spannableString);
            mHighlighter.saveHighlight(mWorkingIndex, mHighlightBounds, colorInt);
            mHighlightBounds = null;
            mHighlightActive = false;
            mReaderActivity.showHighlightButton(false);
        }
    }

    private void restoreHighlights(TextView textView, int position) {
        for (Highlighter.HighlightInfo highlightInfo : mRestoredHighlights) {
            int chapter = highlightInfo.getChapter();
            if (chapter == position) {
                int colorInt = highlightInfo.getColorInt();
                VerseBounds verseBounds = highlightInfo.getVerseBounds();
                final BackgroundColorSpan restoredHighlight = new BackgroundColorSpan(colorInt);
                SpannableString spannableString = SpannableString.valueOf(textView.getText());
                spannableString.setSpan(restoredHighlight, verseBounds.getStart(), verseBounds.getEnd(), 0);
                textView.setText(spannableString);
            }
        }
    }

    private class SimplifiedDoubleClickListener implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (mHighlightActive) {
                unHighlightDClickedWord();
                mHighlightBounds = null;
                mHighlightActive = false;
            }
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            highlightDClickedWord((int) e.getX(), (int) e.getY());
            mHighlightActive = true;
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }

    private class DoubleClickListener implements View.OnTouchListener {
        private long downTime1 = -1;
        private long downTime2 = -1;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            long NULL_TIME = -1;
            long TIME_DOUBLE_CLICK_INTERVAL = 500;
            long TIME_CLICK = 200;

            long now = Calendar.getInstance().getTimeInMillis();
            int action = event.getAction();
            switch (action) {
                case ACTION_DOWN:
                    //check the interval between the first click and the second
                    if (downTime1 != NULL_TIME && downTime2 == NULL_TIME && (now - downTime1) > TIME_DOUBLE_CLICK_INTERVAL) {
                        Toast.makeText(v.getContext(), "Exceeded TIME_DOUBLE_CLICK_INTERVAL", Toast.LENGTH_SHORT).show();
                        downTime1 = now;
                    } else if (downTime1 == NULL_TIME) {
                        downTime1 = now;
                        downTime2 = NULL_TIME;
                    } else {
                        downTime2 = now;
                    }
                    break;
                case ACTION_UP:
                    if (downTime1 != NULL_TIME) {
                        if (downTime2 == NULL_TIME) {
                            if ((now - downTime1) > TIME_CLICK) downTime1 = NULL_TIME;
                        } else if ((now - downTime2) > TIME_CLICK) {
                            downTime1 = downTime2 = NULL_TIME;
                        } else {
                            downTime1 = downTime2 = NULL_TIME;
                            highlightDClickedWord((int) event.getX(), (int) event.getY());
                        }
                    }
                    break;
            }
            return false;
        }
    }

    private class LongClickListener implements View.OnTouchListener {
        private Calendar downTime = null;
        private Calendar upTime = null;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            if (action == ACTION_DOWN) {
                downTime = Calendar.getInstance();
            } else if (action == ACTION_UP) {
                upTime = Calendar.getInstance();
                downTime.add(Calendar.MILLISECOND, 499);
                if (upTime.compareTo(downTime) == 1) {
                    highlightDClickedWord((int) event.getX(), (int) event.getY());
                }
            }
            return false;
        }
    }
}
