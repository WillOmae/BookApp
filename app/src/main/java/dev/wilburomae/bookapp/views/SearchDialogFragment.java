package dev.wilburomae.bookapp.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

import dev.wilburomae.bookapp.R;
import dev.wilburomae.bookapp.adapters.SearchResultsAdapter;
import dev.wilburomae.bookapp.dataaccesslayer.Search;

public class SearchDialogFragment extends DialogFragment {
    private EditText mSearchInput;
    private RecyclerView mResultsList;
    private RecyclerView.Adapter mResultsAdapter;
    private RecyclerView.LayoutManager mResultsLayoutManager;
    private ArrayList<Search.SearchResult> mResultSet = new ArrayList<>();
    private Context mContext;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View layoutView = layoutInflater.inflate(R.layout.search, null);
        final ImageButton searchButton = (ImageButton) layoutView.findViewById(R.id.search_search);
        mContext = getActivity().getApplicationContext();

        mSearchInput = (EditText) layoutView.findViewById(R.id.search_text);
        mResultsList = (RecyclerView) layoutView.findViewById(R.id.search_results_list);
        mResultsLayoutManager = new LinearLayoutManager(mContext);
        mResultsAdapter = new SearchResultsAdapter(mContext, mResultSet, (ReaderActivity) getActivity());
        mResultsList.setHasFixedSize(true);
        mResultsList.setLayoutManager(mResultsLayoutManager);
        mResultsList.setAdapter(mResultsAdapter);

        dialogBuilder.setTitle("SEARCH");
        dialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getDialog().cancel();
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResultSet.clear();
                mResultsAdapter.notifyDataSetChanged();
                if (mSearchInput.getText().length() > 0) {
                    String toSearch = mSearchInput.getText().toString();
                    ArrayList<Search.SearchResult> searchResults = Search.search(toSearch);
                    if (searchResults.size() > 0) {
                        mResultSet.addAll(searchResults);
                        mResultsAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(mContext, "nothing found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, "empty search input", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialogBuilder.setView(layoutView);

        return dialogBuilder.create();
    }
}
