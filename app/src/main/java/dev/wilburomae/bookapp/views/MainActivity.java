package dev.wilburomae.bookapp.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.View;

import dev.wilburomae.bookapp.R;
import dev.wilburomae.bookapp.adapters.ChapterRecyclerAdapter;
import dev.wilburomae.bookapp.dataaccesslayer.Formatter;
import dev.wilburomae.bookapp.dataaccesslayer.XmlReader;
import dev.wilburomae.bookapp.dataaccesslayer.models.Chapter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView.Adapter adapterRecyclerView;
    private SearchDialogFragment mSearchDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        RecyclerView chaptersRecyclerView = findViewById(R.id.main_recycler);
        RecyclerView.LayoutManager layoutRecyclerView = new LinearLayoutManager(this);
        final DrawerLayout drawer = findViewById(R.id.main_drawer);
        if (XmlReader.instantiate(this.getAssets())) {
            Chapter[] chapters = XmlReader.getChapters();
            for (Chapter chapter : chapters) {
                chapter.setChapterContentFormatted(Formatter.format(SpannableString.valueOf(chapter.getChapterContent())));
            }
            adapterRecyclerView = new ChapterRecyclerAdapter(this, chapters);
        }

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.main_drawer_open, R.string.main_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.setDrawerIndicatorEnabled(true);
            toggle.syncState();
        }

        chaptersRecyclerView.setHasFixedSize(true);
        chaptersRecyclerView.setLayoutManager(layoutRecyclerView);
        chaptersRecyclerView.setAdapter(adapterRecyclerView);

        findViewById(R.id.main_menu_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                mSearchDialogFragment = new SearchDialogFragment();
                mSearchDialogFragment.show(getSupportFragmentManager(), "searchDlg");
            }
        });
        findViewById(R.id.main_menu_about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutDialogFragment aboutDialogFragment = new AboutDialogFragment();
                aboutDialogFragment.show(getSupportFragmentManager(), "aboutDlg");
            }
        });
    }

    @Override
    public void onClick(View v) {
        mSearchDialogFragment.getDialog().cancel();
        Intent nextActivity = new Intent(this, ReaderActivity.class);
        nextActivity.putExtra("POSITION", Integer.parseInt(v.getTag().toString()));
        startActivity(nextActivity);
    }
}
