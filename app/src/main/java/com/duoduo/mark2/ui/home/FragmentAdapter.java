package com.duoduo.mark2.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import org.jetbrains.annotations.NotNull;

public class FragmentAdapter extends FragmentStateAdapter {

    public FragmentAdapter(@NonNull @NotNull AppCompatActivity activity) {
        super(activity);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return IndexFragment.newInstance();
            case 1:
                return MyFragment.newInstance();
            default:
                throw new IllegalArgumentException("invalid position");
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
