package fungalaxy.amazonbook.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Toan Vu on 8/23/16.
 */

public class Book {
    private String title;
    @SerializedName("imageURL")
    private String imageUrl;
    private String author;

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
}
