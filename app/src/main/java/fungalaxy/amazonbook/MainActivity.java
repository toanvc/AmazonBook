package fungalaxy.amazonbook;

import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fungalaxy.amazonbook.adapter.BookAdapter;
import fungalaxy.amazonbook.api.ApiHelper;
import fungalaxy.amazonbook.model.Book;
import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recycle_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.content)
    TextView mTextContent;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout mSwipeLayout;
    @BindView(R.id.fab)
    FloatingActionButton mFloatingActionBtn;

    private BookAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAdapter = new BookAdapter(this, new Book[]{});
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mFloatingActionBtn.attachToRecyclerView(mRecyclerView);

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeLayout.setRefreshing(true);
                getData();
            }
        });

        getData();
    }

    @OnClick(R.id.fab)
    public void backToTop(View view) {
//        mRecyclerView.smoothScrollToPosition(0);
        //scroll without animation for faster
        mRecyclerView.scrollToPosition(0);
    }

    private void getData() {
        ApiHelper.getInstance().getBookList().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<Book[]>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        onFail(e.toString());
                    }

                    @Override
                    public void onNext(Response<Book[]> response) {
                        onSuccess(response.body());
                    }
                });
    }


    private void onFail(String error) {
        dismissSwipe();
        mProgressBar.setVisibility(View.GONE);
        mTextContent.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mFloatingActionBtn.setVisibility(View.GONE);
        mTextContent.setText("Error: " + error);
        Snackbar.make(mRecyclerView, "Please try again!", Snackbar.LENGTH_SHORT).show();
    }


    private void onSuccess(Book[] books) {
        dismissSwipe();
        mAdapter.setBookList(books);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mTextContent.setVisibility(View.GONE);
        mFloatingActionBtn.setVisibility(View.VISIBLE);
    }

    private void dismissSwipe() {
        if (mSwipeLayout.isRefreshing()) {
            mSwipeLayout.setRefreshing(false);
        }
    }
}
