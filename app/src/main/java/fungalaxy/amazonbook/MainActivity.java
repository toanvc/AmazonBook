package fungalaxy.amazonbook;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

import org.greenrobot.greendao.rx.RxQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fungalaxy.amazonbook.adapter.BookAdapter;
import fungalaxy.amazonbook.api.ApiHelper;
import fungalaxy.amazonbook.model.Book;
import fungalaxy.amazonbook.model.BookDao;
import fungalaxy.amazonbook.model.DaoSession;
import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final int PAGE_SIZE = 20;
    private static final long TIME_TEST_LOADING = 500;

    enum Style {
        LINEAR, STAGGERED_GRID
    }

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
    @BindView(R.id.textbar)
    TextView mTextCountItems;

    private BookAdapter mAdapter;
    private Book[] mBookArr;
    private int current = 0;
    private ProgressDialog mDialog;
    private Handler mHandler = new Handler();
    private Style mStyle;


    private BookDao bookDao;
    private RxQuery<Book> booksQuery;

    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAdapter = new BookAdapter(this, new ArrayList<Book>());
        mStaggeredGridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mStyle = Style.LINEAR;

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    //check for scroll right
                    loadMoreItems();
                }
            }
        });
        mFloatingActionBtn.attachToRecyclerView(mRecyclerView);

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeLayout.setRefreshing(true);
                getData();
            }
        });
        mDialog = new ProgressDialog(MainActivity.this);

        mDialog.setMessage("Load more...");
        mDialog.getWindow().setGravity(Gravity.BOTTOM);


        // get the Rx variant of the book DAO
        DaoSession daoSession = ((MyApp) getApplication()).getDaoSession();
        bookDao = daoSession.getBookDao();
        booksQuery = daoSession.getBookDao().queryBuilder().rx();
        loadFromDatabase();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacksAndMessages(null);
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
                        mBookArr = response.body();
                        addBooksToDb(mBookArr);
                        onSuccess();
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


    private void onSuccess() {
        mAdapter.getBookList().clear();
        current = 0;
        dismissSwipe();
        updateToAdapter();
//        mAdapter.setBookList(books);
//        mAdapter.notifyDataSetChanged();
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

    //======
    private void updateToAdapter() {
        int leng = mBookArr.length;
        if (current >= leng) {
            return;
        }
        ArrayList<Book> adapterList = mAdapter.getBookList();
        for (int i = 0; i < PAGE_SIZE; i++) {
            if (current >= leng) {
                break;
            }

            adapterList.add(mBookArr[current]);
            current++;

        }
//        mAdapter.setBookList(adapterList);
        mTextCountItems.setText(current + "/" + leng);
        mAdapter.notifyDataSetChanged();
        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    private void loadMoreItems() {
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int pastVisibleItems = getFirstVisibleItem(mStyle, layoutManager);
        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
            load();
        }
    }


    private void load() {
        if (!mDialog.isShowing()) {
            mDialog.show();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateToAdapter();
                }
            }, TIME_TEST_LOADING);

        }
    }

    private void loadFromDatabase() {
        // query all book, sorted by date
        booksQuery.list()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<Book>>() {
                    @Override
                    public void call(List<Book> books) {
                        mBookArr = books.toArray(new Book[books.size()]);
                        if (mBookArr.length == 0) {
                            getData();
                        } else {
                            onSuccess();
                        }
                    }
                });


    }


    //TODO: add to Db takes time, need handle this case:
    //during adding to db, stop this activity, this also stops.
    private void addBooksToDb(Book[] books) {
        //delete all when swipe (refresh) case
        bookDao.deleteAll();
        rx.Observable.from(books)
//                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Book>() {
                    @Override
                    public void onCompleted() {
                        Log.d("MainActivity", "Adding database completed!");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Book book) {
                        book.setDate(new Date());
                        bookDao.insert(book);
                    }
                });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.switch_layout:
                int pos = getFirstVisibleItem(mStyle, mRecyclerView.getLayoutManager());
                if (mStyle == Style.LINEAR) {
                    mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
                    mStyle = Style.STAGGERED_GRID;
                    item.setIcon(R.drawable.ic_menu_white_24dp);
                } else {
                    mRecyclerView.setLayoutManager(mLinearLayoutManager);
                    mStyle = Style.LINEAR;
                    item.setIcon(R.drawable.ic_apps_white_24dp);
                }
                mRecyclerView.scrollToPosition(pos);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private int getFirstVisibleItem(Style style, RecyclerView.LayoutManager layoutManager) {
        int pastVisibleItems = 0;
        if (style == Style.LINEAR) {
            pastVisibleItems = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();

        } else {

            int[] firstVisibleItems = null;
            firstVisibleItems = ((StaggeredGridLayoutManager) layoutManager)
                    .findFirstVisibleItemPositions(firstVisibleItems);
            if (firstVisibleItems != null && firstVisibleItems.length > 0) {
                pastVisibleItems = firstVisibleItems[0];
            }
        }
        return pastVisibleItems;
    }

}
