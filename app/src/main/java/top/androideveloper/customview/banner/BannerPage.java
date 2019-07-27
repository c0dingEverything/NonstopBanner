package top.androideveloper.customview.banner;

import android.view.View;
import android.widget.ImageView;

public class BannerPage {
    private ImageView view;
    private String description;
    private View.OnClickListener onClickListener;

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public BannerPage(ImageView view, String description, View.OnClickListener l) {
        this.view = view;
        this.description = description;
        this.onClickListener = l;
    }

    public BannerPage() {
    }

    public BannerPage(ImageView view, String description) {
        this.view = view;
        this.description = description;
    }

    public ImageView getView() {
        return view;
    }

    public void setView(ImageView view) {
        this.view = view;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

