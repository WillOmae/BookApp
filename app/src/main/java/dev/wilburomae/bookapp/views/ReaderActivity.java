package dev.wilburomae.bookapp.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import dev.wilburomae.bookapp.R;
import dev.wilburomae.bookapp.adapters.ReaderPagerAdapter;
import dev.wilburomae.bookapp.dataaccesslayer.XmlReader;

public class ReaderActivity extends AppCompatActivity implements View.OnClickListener {
    private ViewPager mViewPager;
    private SearchDialogFragment mSearchDialogFragment;
    private FloatingActionButton mHighlightButton;
    private TextView mToolbarText;
    private ImageView highlightBlue;
    private ImageView highlightRed;
    private ImageView highlightGreen;
    private ImageView highlightYellow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reader);

        Intent prevIntent = getIntent();
        Bundle extras = prevIntent.getExtras();
        int position = extras.getInt("POSITION");

        Toolbar toolbar = (Toolbar) findViewById(R.id.reader_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mToolbarText = (TextView) findViewById(R.id.reader_toolbar_text);
        setToolbarText("");

        mHighlightButton = findViewById(R.id.reader_fab_highlight);
        highlightBlue = (ImageView) findViewById(R.id.reader_fab_highlight_blue);
        highlightRed = (ImageView) findViewById(R.id.reader_fab_highlight_red);
        highlightGreen = (ImageView) findViewById(R.id.reader_fab_highlight_green);
        highlightYellow = (ImageView) findViewById(R.id.reader_fab_highlight_yellow);

        ReaderPagerAdapter mPagerAdapter = new ReaderPagerAdapter(this, XmlReader.getChapters());
        mViewPager = findViewById(R.id.reader_viewPager);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(position);

    }

    public void setToolbarText(String text) {
        mToolbarText.setText(text);
    }
    public ArrayList<ImageView> getHighlightButtons(){
        ArrayList<ImageView> highlightButtons = new ArrayList<>();
        highlightButtons.add(highlightBlue);
        highlightButtons.add(highlightRed);
        highlightButtons.add(highlightGreen);
        highlightButtons.add(highlightYellow);
        return highlightButtons;
    }

    public void showHighlightButton(boolean toShow) {
        mHighlightButton.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.constant_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:
                break;
            case R.id.menu_settings:
                break;
            case R.id.menu_search:
                mSearchDialogFragment = new SearchDialogFragment();
                mSearchDialogFragment.show(getFragmentManager(), "searchDlg");
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        mSearchDialogFragment.getDialog().cancel();
        mViewPager.setCurrentItem(Integer.parseInt(v.getTag().toString()));
    }
}
