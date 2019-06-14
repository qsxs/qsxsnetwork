package com.six.network.download;

import android.util.Log;
import androidx.annotation.NonNull;
import com.six.network.BaseSimpleObserver;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

import java.io.*;
import java.util.concurrent.TimeUnit;

/**
 * Description: 下载工具类
 * Created by jia on 2017/11/30.
 * 人之所以能，是相信能
 */
public class DownloadUtils {

    private static final String TAG = "DownloadUtils";

    private static final int DEFAULT_TIMEOUT = 15000;

    private Retrofit retrofit;

    private JsDownloadListener listener;

    private String downloadUrl;
    private long timeout = DEFAULT_TIMEOUT;

    public DownloadUtils() {
        this(DEFAULT_TIMEOUT, null);
    }

    public DownloadUtils(JsDownloadListener listener) {
        this(DEFAULT_TIMEOUT, listener);
    }

    public DownloadUtils(long timeout, JsDownloadListener listener) {

        String baseUrl = "http://www.baidu.com/";
        this.listener = listener;
        this.timeout = timeout;

        JsDownloadInterceptor mInterceptor = new JsDownloadInterceptor(listener);
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(@NonNull String message) {
                Log.i(TAG, message);
            }
        });
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(mInterceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(httpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    /**
     * 开始下载
     */
    public void download(@NonNull String url, final String filePath, BaseSimpleObserver<InputStream> subscriber) {
        download(url, filePath, subscriber, null);
    }

    public void download(@NonNull String url, final String filePath, BaseSimpleObserver<InputStream> subscriber, JsDownloadListener listener) {
        if (listener != null) {
            this.listener = listener;
        }
        if (this.listener != null) {
            this.listener.onStartDownload();
        }

        // subscribeOn()改变调用它之前代码的线程
        // observeOn()改变调用它之后代码的线程
        retrofit.create(DownloadService.class)
                .download(url)
                .subscribeOn(Schedulers.io())
//                .unsubscribeOn(Schedulers.io())
                .map(new Function<ResponseBody, InputStream>() {
                    @Override
                    public InputStream apply(ResponseBody responseBody) {
                        return responseBody.byteStream();
                    }
                })
                .observeOn(Schedulers.io()) // 用于计算任务
                .doOnNext(new Consumer<InputStream>() {
                    @Override
                    public void accept(InputStream inputStream) {
                        writeFile(inputStream, filePath);
                    }
                })
//                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

    }

    /**
     * 将输入流写入文件
     */
    private void writeFile(InputStream inputString, String filePath) {
        Log.i(TAG, "writeFile: " + filePath);
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        if (file.exists()) {
            file.delete();
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);

            byte[] b = new byte[1024];

            int len;
            while ((len = inputString.read(b)) != -1) {
                fos.write(b, 0, len);
            }
        } catch (FileNotFoundException e) {
            if (listener != null) {
                listener.onFail("download fail");
            }
        } catch (IOException e) {
            if (listener != null) {
                listener.onFail("save fail");
            }
        } finally {
            try {
                inputString.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public interface DownloadService {
        @Streaming
        @GET
        Observable<ResponseBody> download(@Url String url);
    }
}
