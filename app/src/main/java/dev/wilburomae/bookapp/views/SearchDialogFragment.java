package dev.wilburomae.bookapp.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import dev.wilburomae.bookapp.R;
import dev.wilburomae.bookapp.adapters.SearchResultsAdapter;
import dev.wilburomae.bookapp.dataaccesslayer.Search;

public class SearchDialogFragment extends DialogFragment {
    private EditText mSearchInput;
    private RecyclerView.Adapter mResultsAdapter;
    private ArrayList<Search.SearchResult> mResultSet = new ArrayList<>();
    private Context mContext;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View layoutView = layoutInflater.inflate(R.layout.search, null);
        final ImageButton searchButton = (ImageButton) layoutView.findViewById(R.id.search_search);
        final LinearLayout linearLayout = (LinearLayout) layoutView.findViewById(R.id.search_results);
        linearLayout.setVisibility(View.GONE);
        mContext = getActivity().getApplicationContext();

        mSearchInput = (EditText) layoutView.findViewById(R.id.search_text);
        RecyclerView mResultsList = (RecyclerView) layoutView.findViewById(R.id.search_results_list);
        RecyclerView.LayoutManager mResultsLayoutManager = new LinearLayoutManager(mContext);
        mResultsAdapter = new SearchResultsAdapter(mContext, mResultSet, (View.OnClickListener) getActivity());
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
                        linearLayout.setVisibility(View.VISIBLE);
                        mResultSet.addAll(searchResults);
                        mResultsAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(mContext, "nothing found", Toast.LENGTH_SHORT).show();
                        linearLayout.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(mContext, "empty search input", Toast.LENGTH_SHORT).show();
                    linearLayout.setVisibility(View.GONE);
                }
            }
        });
        dialogBuilder.setView(layoutView);

        return dialogBuilder.create();
    }
}
