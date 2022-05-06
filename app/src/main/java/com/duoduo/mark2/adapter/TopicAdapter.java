package com.duoduo.mark2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.duoduo.mark2.R;
import com.duoduo.mark2.models.Topic;
import com.google.android.material.textview.MaterialTextView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter {

    private ClickListener clickListener;

    private class MViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView title;
        private final ImageView imageView;
        private final TextView desc;

        public MViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.title = itemView.findViewById(R.id.title);
            this.imageView = itemView.findViewById(R.id.img);
            this.desc = itemView.findViewById(R.id.desc);
        }

        public void bindTopic(Topic topic) {
            title.setText(topic.name);
            desc.setText(topic.description);
            Glide
                    .with(imageView)
                    .load(topic.cover.original)
                    .transition(DrawableTransitionOptions.withCrossFade(800))
                    .into(this.imageView);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null)
                clickListener.onItemClick(getAdapterPosition(), view);
        }
    }

    private final ArrayList<Topic> topics = new ArrayList<>();

    public void addTopics(List<Topic> topicList) {
        int old = topics.size();
        this.topics.addAll(topicList);
        this.notifyItemRangeInserted(old - 1, topicList.size());
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_topic, parent, false);
        return new MViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        ((MViewHolder) holder).bindTopic(topics.get(position));
    }

    @Override
    public int getItemCount() {
        return this.topics.size();
    }

    public Topic getTopicAt(int pos) {
        return this.topics.get(pos);
    }

    public void setClickListener(ClickListener listener) {
        this.clickListener = listener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

}
