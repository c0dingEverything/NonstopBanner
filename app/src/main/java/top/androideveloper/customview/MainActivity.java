package top.androideveloper.customview;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import top.androideveloper.customview.banner.BannerPage;
import top.androideveloper.customview.banner.NonstopBanner;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private boolean mNonstop = false;
    private boolean mAutoPlay = false;
    private NonstopBanner mBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<BannerPage> list = new ArrayList<>();
        ImageView imageView = new ImageView(this);
        imageView.setBackgroundResource(R.drawable.page1);
        list.add(new BannerPage(imageView, "描述" + 1, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int postion = (int) view.getTag(R.id.position);
                Toast.makeText(MainActivity.this, "" + postion, Toast.LENGTH_SHORT).show();
            }
        }));

        imageView = new ImageView(this);
        imageView.setBackgroundResource(R.drawable.page2);
        list.add(new BannerPage(imageView, "描述" + 2));

        imageView = new ImageView(this);
        imageView.setBackgroundResource(R.drawable.page3);
        list.add(new BannerPage(imageView, "描述" + 3));

        imageView = new ImageView(this);
        imageView.setBackgroundResource(R.drawable.page4);
        list.add(new BannerPage(imageView, "描述" + 4));

        imageView = new ImageView(this);
        imageView.setBackgroundResource(R.drawable.page5);
        list.add(new BannerPage(imageView, "描述" + 5));

        mBanner = findViewById(R.id.banner);
        mBanner.setPages(list);


    }

}
