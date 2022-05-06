package com.duoduo.mark2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.duoduo.mark2.R;
import com.duoduo.mark2.models.Article;
import com.google.android.material.textview.MaterialTextView;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter {
    private final ArrayList<Article> articles = new ArrayList<>();

    private class MViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private MaterialTextView title;
        private ImageView avatar;
        private MaterialTextView comment_count;
        private MaterialTextView date;

        public MViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            avatar = itemView.findViewById(R.id.avatar);
            comment_count = itemView.findViewById(R.id.left);
            date = itemView.findViewById(R.id.right);
            itemView.setOnClickListener(this);
        }

        public void bindArticle(Article article) {
            title.setText(article.title);
            Glide
                    .with(avatar)
                    .load(article.relationships.user.avatar.original)
                    .into(avatar);
            comment_count.setText(String.format("%s 条回复", article.comment_count));
            DateFormat format = SimpleDateFormat.getDateInstance();
            date.setText(format.format(new Date(article.update_time * 1000)));
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null)
                clickListener.onItemClick(getAdapterPosition(), view);
        }

    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
        return new MViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        ((MViewHolder) holder).bindArticle(articles.get(position));
    }

    @Override
    public int getItemCount() {
        return this.articles.size();
    }

    public void addArticles(List<Article> articleList) {
        int old = articles.size();
        this.articles.addAll(articleList);
        this.notifyItemRangeInserted(old, articleList.size());
    }

    public void clear() {
        int old = articles.size();
        this.articles.clear();
        notifyItemRangeRemoved(0, old);
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    private ArticleAdapter.ClickListener clickListener;

    public void setClickListener(ArticleAdapter.ClickListener listener) {
        this.clickListener = listener;
    }

    public Article getArticle(int i) {
        return this.articles.get(i);
    }
}
