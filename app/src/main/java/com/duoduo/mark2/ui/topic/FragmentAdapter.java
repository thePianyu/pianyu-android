package com.duoduo.mark2.ui.topic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import org.jetbrains.annotations.NotNull;

public class FragmentAdapter extends FragmentStateAdapter {

    private final int topic_id;

    public FragmentAdapter(@NonNull @NotNull AppCompatActivity activity, int topic_id) {
        super(activity);
        this.topic_id = topic_id;
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return ArticlesFragment.newInstance(topic_id);
            case 1:
                return QuestionsFragment.newInstance(topic_id);
            default:
                throw new IllegalArgumentException("invalid position");
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
