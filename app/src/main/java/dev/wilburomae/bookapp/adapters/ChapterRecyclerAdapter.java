package dev.wilburomae.bookapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import dev.wilburomae.bookapp.R;
import dev.wilburomae.bookapp.dataaccesslayer.models.Chapter;
import dev.wilburomae.bookapp.views.ReaderActivity;

/**
 * Created by WILBUR OMAE on 30/03/2018.
 */
public class ChapterRecyclerAdapter extends RecyclerView.Adapter<ChapterRecyclerAdapter.ViewHolder> {

    private Chapter[] dataSet;
    private LayoutInflater inflater;

    public ChapterRecyclerAdapter(Context context, Chapter[] _dataSet) {
        dataSet = _dataSet;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.main_recycler_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position >= 0 && position < dataSet.length) {
            Chapter ch = dataSet[position];
            holder.txtTitle.setText(ch.getChapterTitle());
            holder.txtIntro.setText(ch.getChapterIntro());
            holder.linearLayout.setTag(position);
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout linearLayout;
        private TextView txtTitle;
        private TextView txtIntro;

        ViewHolder(View view) {
            super(view);
            linearLayout = view.findViewById(R.id.main_recycler_card);
            txtTitle = view.findViewById(R.id.main_recycler_card_title);
            txtIntro = view.findViewById(R.id.main_recycler_card_intro);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent nextActivity = new Intent(context, ReaderActivity.class);
                    nextActivity.putExtra("POSITION", (int) linearLayout.getTag());
                    context.startActivity(nextActivity);
                }
            });
        }
    }
}