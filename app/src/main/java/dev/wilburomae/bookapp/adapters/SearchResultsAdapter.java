package dev.wilburomae.bookapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import dev.wilburomae.bookapp.R;
import dev.wilburomae.bookapp.dataaccesslayer.Search;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {
    private ArrayList<Search.SearchResult> mDataSet;
    private LayoutInflater mLayoutInflater;
    private View.OnClickListener mOnClickListener;

    public SearchResultsAdapter(Context context, ArrayList<Search.SearchResult> dataSet, View.OnClickListener onClickListener) {
        mDataSet = dataSet;
        mLayoutInflater = LayoutInflater.from(context);
        mOnClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.main_recycler_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position >= 0 && position < mDataSet.size()) {
            Search.SearchResult searchResult = mDataSet.get(position);
            holder.txtTitle.setText(searchResult.getRef());
            holder.txtIntro.setText(searchResult.getSnippet());
            holder.cardView.setTag(searchResult.getChapterNumber());
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView txtTitle;
        private TextView txtIntro;

        private ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.main_recycler_card);
            txtTitle = itemView.findViewById(R.id.main_recycler_card_title);
            txtIntro = itemView.findViewById(R.id.main_recycler_card_intro);
            cardView.setOnClickListener(mOnClickListener);
        }
    }
}
