package fungalaxy.amazonbook.api;

import fungalaxy.amazonbook.model.Book;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Created by Toan Vu on 8/23/16.
 */
public class ApiHelper {
    private AmazonBookApi mApi;

    private static final String BASE_URL = "http://de-coding-test.s3.amazonaws.com/";

    private static ApiHelper mInstance;

    public static ApiHelper getInstance() {
        if (mInstance == null) {
            mInstance = new ApiHelper();
        }
        return mInstance;
    }

    private ApiHelper() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient.build())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mApi = retrofit.create(AmazonBookApi.class);

    }

    public Observable<Response<Book[]>> getBookList() {
        return mApi.getBookList();
    }

}
