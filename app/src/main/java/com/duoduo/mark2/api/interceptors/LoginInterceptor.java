package com.duoduo.mark2.api.interceptors;

import android.content.Context;
import android.util.Log;
import com.duoduo.mark2.api.exception.FailureResponseException;
import com.duoduo.mark2.api.exception.LoginException;
import com.duoduo.mark2.models.BaseResponse;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import kotlin.text.Charsets;
import okhttp3.*;
import okio.BufferedSource;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;

public class LoginInterceptor implements Interceptor {

    WeakReference<Context> contextWeakReference; // 防止内存泄漏

    private final Gson gson;

    public LoginInterceptor(Context context) {
        gson = new Gson();
        contextWeakReference = new WeakReference<>(context);
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        String token = "";
        if (contextWeakReference.get() != null) {
            token = contextWeakReference.get().getSharedPreferences("MDCLUB", Context.MODE_PRIVATE).getString("TOKEN", "");
        }

        Request request = chain.request();
        request = request.newBuilder()
                .addHeader("token", token) // 添加token
                .build();

        Response response = chain.proceed(request);
        ResponseBody body = response.body();

        if (!response.isSuccessful() || body == null || body.contentLength() == 0L) return response;

        MediaType contentType = body.contentType();
        Charset charset;
        if (contentType == null)
            charset = Charsets.UTF_8;
        else
            charset = contentType.charset(Charsets.UTF_8);

        BufferedSource source = body.source();
        source.request(Long.MAX_VALUE);
        InputStreamReader inputStreamReader = new InputStreamReader(source.getBuffer().clone().inputStream(), charset);
        BaseResponse<?> obj;
        try {
            obj = gson.fromJson(gson.newJsonReader(inputStreamReader), BaseResponse.class);
        } catch (JsonIOException | JsonSyntaxException e) {
            Log.e("LoginInterceptor", "", e);
            return response;
        } finally {
            inputStreamReader.close();
        }

        if (obj != null)
            if (obj.code == 201001) {
                // 用户未登录
                throw new LoginException("未登录或登录过期，请重新登录");
            } else if (obj.code != 0) { // 返回代码不是0
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("Error%s", obj.code));
                if (obj.extra_message != null)
                    sb.append("\nExtra: ").append(obj.extra_message);
                if (obj.errors != null)
                    for (String key : obj.errors.keySet()) {
                        sb.append("\n").append(obj.errors.get(key));
                    }
                throw new FailureResponseException(sb.toString());
            }

        return response;
    }
}
