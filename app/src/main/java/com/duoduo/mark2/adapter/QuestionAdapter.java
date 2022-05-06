package com.duoduo.mark2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.duoduo.mark2.R;
import com.duoduo.mark2.models.Question;
import com.google.android.material.textview.MaterialTextView;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter {
    public interface OnClickListener {
        void onClick(Question question);
    }

    private OnClickListener listener;

    public void setOnClickListener(OnClickListener l) {
        this.listener = l;
    }

    private final ArrayList<Question> questions = new ArrayList<>();

    private class MViewHolder extends RecyclerView.ViewHolder{

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
        }

        public void bindQuestion(Question question) {
            title.setText(question.title);
            Glide
                    .with(avatar)
                    .load(question.relationships.user.avatar.original)
                    .into(avatar);
            comment_count.setText(String.format("%s 条回答", question.answer_count));
            DateFormat format = SimpleDateFormat.getDateInstance();
            date.setText(format.format(new Date(question.update_time * 1000)));
        }

    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question2, parent, false);
        return new MViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        ((MViewHolder) holder).bindQuestion(questions.get(position));
        holder.itemView.setOnClickListener(view -> {
            listener.onClick(questions.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return this.questions.size();
    }

    public void addQuestions(List<Question> questionList) {
        int old = questions.size();
        this.questions.addAll(questionList);
        this.notifyItemRangeInserted(old, questionList.size());
    }

    public void clear() {
        int old = questions.size();
        this.questions.clear();
        notifyItemRangeRemoved(0, old);
    }
}
