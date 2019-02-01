package dev.wilburomae.bookapp.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import dev.wilburomae.bookapp.R;
import dev.wilburomae.bookapp.adapters.ChapterRecyclerAdapter;
import dev.wilburomae.bookapp.dataaccesslayer.XmlReader;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    RecyclerView chaptersRecyclerView;
    RecyclerView.LayoutManager layoutRecyclerView;
    RecyclerView.Adapter adapterRecyclerView;
    private SearchDialogFragment mSearchDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        toolbar = findViewById(R.id.main_toolbar);
        chaptersRecyclerView = findViewById(R.id.main_recycler);
        layoutRecyclerView = new LinearLayoutManager(this);
        if (XmlReader.instantiate(this.getAssets())) {
            adapterRecyclerView = new ChapterRecyclerAdapter(this, XmlReader.getChapters());
        }

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        final DrawerLayout drawer = findViewById(R.id.main_drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.main_drawer_open, R.string.main_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        chaptersRecyclerView.setHasFixedSize(true);
        chaptersRecyclerView.setLayoutManager(layoutRecyclerView);
        chaptersRecyclerView.setAdapter(adapterRecyclerView);

        ((Button) findViewById(R.id.main_menu_search)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
                mSearchDialogFragment = new SearchDialogFragment();
                mSearchDialogFragment.show(getSupportFragmentManager(), "searchDlg");
            }
        });
        ((Button) findViewById(R.id.main_menu_about)).setOnClickListener(new View.OnClickListener() {
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
