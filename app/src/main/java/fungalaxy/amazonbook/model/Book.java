package fungalaxy.amazonbook.model;

import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;

import java.util.Date;

/**
 * Created by Toan Vu on 8/23/16.
 */
@Entity(indexes = {
        @Index(value = "id ASC", unique = true)
})
public class Book {
    @Id
    private Long id = null;
    @NotNull
    private String title;
    @SerializedName("imageURL")
    private String imageUrl;
    private String author;
    private java.util.Date date;

    @Generated(hash = 1078631058)
    public Book(Long id, @NotNull String title, String imageUrl, String author,
            java.util.Date date) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.author = author;
        this.date = date;
    }

    @Generated(hash = 1839243756)
    public Book() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
