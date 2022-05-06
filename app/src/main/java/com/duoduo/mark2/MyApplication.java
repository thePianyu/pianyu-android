package com.duoduo.mark2;

import android.app.Application;
import com.zy.multistatepage.MultiState;
import com.zy.multistatepage.MultiStateConfig;
import com.zy.multistatepage.MultiStatePage;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        MultiStateConfig config = new MultiStateConfig.Builder()
                .alphaDuration(150)
                .loadingMsg("Loading...")
                .build();
        MultiStatePage.config(config);
    }

}
