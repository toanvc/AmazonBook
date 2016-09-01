package fungalaxy.amazonbook.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fungalaxy.amazonbook.R;
import fungalaxy.amazonbook.model.Book;
import fungalaxy.amazonbook.widget.AspectImageView;


/**
 * Created by Toan Vu on 8/23/16.
 */
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ItemViewHolder> {

    private ArrayList<Book> mItems;
    private List<Integer> mHeights = new ArrayList<>();
    private Context mContext;

    public BookAdapter(Context context, ArrayList<Book> books) {
        this.mContext = context;
        mItems = books;
    }

    public void setBookList(ArrayList<Book> books) {
        this.mItems = books;
    }

    public ArrayList<Book> getBookList() {
        if (mItems == null) {
            return new ArrayList<>();
        }
        return mItems;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_item_layout, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Book book = mItems.get(position);
        holder.title.setText("[" + (position + 1) + "]" + book.getTitle());
        if (book.getAuthor() == null) {
            holder.author.setVisibility(View.GONE);
        } else {
            holder.author.setVisibility(View.VISIBLE);
            holder.author.setText(book.getAuthor());
        }
        Picasso.with(mContext).load(book.getImageUrl())
                .placeholder(R.drawable.ic_book_black_48dp)
                .into(holder.thumbnail);

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.author)
        TextView author;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.thumbnail)
        AspectImageView thumbnail;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
