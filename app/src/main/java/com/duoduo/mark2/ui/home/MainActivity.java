package com.duoduo.mark2.ui.home;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.duoduo.mark2.R;
import com.duoduo.mark2.ui.home.FragmentAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new FragmentAdapter(this));
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                navigationView.getMenu().getItem(position).setChecked(true);
            }
        });

        navigationView = findViewById(R.id.nav);
        navigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.item_topic) {
                viewPager.setCurrentItem(0, true);
            } else if (item.getItemId() == R.id.item_my) {
                viewPager.setCurrentItem(1, true);
            }
            return false;
        });

        viewPager.setUserInputEnabled(false);
    }

    public ViewPager2 getViewPager() {
        return this.viewPager;
    }
}