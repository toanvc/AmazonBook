package fungalaxy.amazonbook.api;

import fungalaxy.amazonbook.model.Book;
import retrofit2.Response;
import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by Toan Vu on 8/23/16.
 */

public interface AmazonBookApi {
    @GET("books.json")
    Observable<Response<Book[]>> getBookList();
}
