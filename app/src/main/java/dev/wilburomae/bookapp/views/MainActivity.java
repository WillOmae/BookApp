package dev.wilburomae.bookapp.views;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import dev.wilburomae.bookapp.R;
import dev.wilburomae.bookapp.adapters.ChapterRecyclerAdapter;
import dev.wilburomae.bookapp.dataaccesslayer.XmlReader;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView chaptersRecyclerView;
    RecyclerView.LayoutManager layoutRecyclerView;
    RecyclerView.Adapter adapterRecyclerView;

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

        DrawerLayout drawer = findViewById(R.id.main_drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.main_drawer_open, R.string.main_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        chaptersRecyclerView.setHasFixedSize(true);
        chaptersRecyclerView.setLayoutManager(layoutRecyclerView);
        chaptersRecyclerView.setAdapter(adapterRecyclerView);
    }
}
