package top.androideveloper.customview.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

import top.androideveloper.customview.R;

public class NonstopBanner extends FrameLayout {
    private static final String TAG = "NonstopBanner";
    private ViewPager mPageContainer;
    private LinearLayout mDotContainer;
    private BannerPagerAdapter mPagerAdapter;
    private Context mContext;
    private List<BannerPage> mBannerPages;
    private TextView mDescription;
    private int mPrePosition;
    private boolean nonstop;
    private boolean autoPlay;
    private int autoPlayDelay;
    private Handler mHandler;
    private int nonstopPosition;
    private int backgroundRes;
    private int marginLeft;
    private int textSize;
    private int maskBackgroundColor;
    private boolean enableDesc;
    private boolean enableMask;
    private boolean enableDots;

    public void setAutoPlayDelay(int autoPlayDelay) {
        this.autoPlayDelay = autoPlayDelay;
    }

    public boolean isAutoPlay() {
        return autoPlay;
    }

    public boolean isNonstop() {
        return nonstop;
    }

  /*  public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }

    public void setNonstop(boolean nonstop) {
        this.nonstop = nonstop;
        mPagerAdapter.notifyDataSetChanged();
        initFirstPage();
        mPageContainer.setAdapter(mPagerAdapter);
    }*/

    public NonstopBanner(Context context) {
        super(context);
        init(context);
    }

    public NonstopBanner(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public NonstopBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttribute(context, attrs);
        init(context);
    }

    private void initAttribute(Context context, AttributeSet attrs) {
        float density = context.getResources().getDisplayMetrics().density;
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NonstopBanner);
        autoPlay = typedArray.getBoolean(R.styleable.NonstopBanner_enableAutoPlay, true);
        nonstop = typedArray.getBoolean(R.styleable.NonstopBanner_enableNonstopLoop, true);
        enableDesc = typedArray.getBoolean(R.styleable.NonstopBanner_enableDesc, true);
        enableDots = typedArray.getBoolean(R.styleable.NonstopBanner_enableDots, true);
        backgroundRes = typedArray.getResourceId(R.styleable.NonstopBanner_customSelector, R.drawable.selector_dot_bg);
        marginLeft = (int) (typedArray.getDimensionPixelSize(R.styleable.NonstopBanner_dotGapSpace, 8));
        textSize = (int) (typedArray.getDimensionPixelSize(R.styleable.NonstopBanner_descTextSize, 20) / scaledDensity);
        autoPlayDelay = typedArray.getInteger(R.styleable.NonstopBanner_interval, 4000);
        maskBackgroundColor = typedArray.getColor(R.styleable.NonstopBanner_maskBackgroundColor, 0x44000000);//#44000000

        Log.d(TAG, String.format("autoPlay=%s--nonstop=%s--backgroundRes=%s--marginLeft=%s--textSize=%s--autoPlayDelay=%s--maskBackgroundColor=%s--enableDots=%s",
                autoPlay, nonstop, backgroundRes, marginLeft, textSize, autoPlayDelay, maskBackgroundColor, enableDots));
        typedArray.recycle();
    }


    public void setPages(List<BannerPage> bannerPages) {
        mBannerPages = bannerPages;
        initDots(bannerPages.size()); //初始化指示器

        if (mPagerAdapter == null) {
            mPagerAdapter = new BannerPagerAdapter();
        }
        mPageContainer.setAdapter(mPagerAdapter);

        initFirstPage(); //初始化第一个界面

        if (autoPlay && mHandler != null) {  //如果开启了自动轮播，则开始轮播
            mHandler.sendEmptyMessageDelayed(0, autoPlayDelay);
        }
    }

    private void initFirstPage() {
        if (nonstop) {
            mPrePosition = Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % mBannerPages.size();
            Log.d(TAG, "initFirstPage: " + mPrePosition);
            mPageContainer.setCurrentItem(mPrePosition);
            mPrePosition %= mBannerPages.size();
        }
        mDescription.setText(mBannerPages.get(mPrePosition).getDescription());
    }

    private void start2Play() {
        if (autoPlay) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler.sendEmptyMessageDelayed(0, autoPlayDelay);
        }
    }

    private void stopPlaying() {
        if (autoPlay) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    private void initDots(int size) {
        if (!enableDots) {
            mDotContainer.setVisibility(GONE);
        } else {
            int childCount = mPageContainer.getChildCount();
            int diff = size - childCount;
            if (diff > 0) {
                for (int i = childCount; i < childCount + diff; i++) {
                    ImageView view = new ImageView(mContext);
                    view.setBackgroundResource(backgroundRes);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    if (i != 0) {
                        params.leftMargin = marginLeft;
                    }
                    view.setEnabled(i == 0);
                    mDotContainer.addView(view, i, params);
                }
            } else {
                for (int i = 0; i < -diff; i++) {
                    mDotContainer.removeViewAt(mDotContainer.getChildCount() - 1);
                }
            }
        }
    }


    private void init(Context context) {
        mContext = context;
        initHandler();
        initViews(context);
        mPageContainer.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (nonstop) {
                    position %= mBannerPages.size();
                    nonstopPosition = position;
                }
                if (enableDesc) {
                    mDescription.setText(mBannerPages.get(position).getDescription());
                }
                if (enableDots) {
                    mDotContainer.getChildAt(mPrePosition).setEnabled(false);
                    mDotContainer.getChildAt(position).setEnabled(true);
                }
                mPrePosition = position;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (!autoPlay) {
                    return;
                }
                switch (state) {
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        stopPlaying();
                        break;

                    case ViewPager.SCROLL_STATE_IDLE:
                        start2Play();
                        break;
                }

            }
        });

    }


    private void initViews(Context context) {
        NonstopBanner layout = (NonstopBanner) LayoutInflater.from(context).inflate(R.layout.view_nonstop_banner, this);
        mDotContainer = layout.findViewById(R.id.banner_dot_container);
        layout.findViewById(R.id.banner_mask).setBackgroundColor(maskBackgroundColor);
        mDescription = layout.findViewById(R.id.banner_text);
        if (enableDesc) {
            mDescription.setTextSize(textSize);
        } else {
            mDescription.setVisibility(GONE);
        }
        mPageContainer = layout.findViewById(R.id.banner_view_pager);
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        Log.d(TAG, "onVisibilityChanged: " + visibility + "  " + changedView);
        if (visibility == INVISIBLE && autoPlay && mHandler != null) {
            stopPlaying();
        } else if (visibility == VISIBLE && autoPlay && mHandler != null) {
            start2Play();
        }
        super.onVisibilityChanged(changedView, visibility);
    }


    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);

                // 无限轮播的时候：1，不自动播放，则在最后一个item的时候停止发送。2，自动播放，到了极限值之后，停止播放
                if (!nonstop && (mPageContainer.getCurrentItem() == mBannerPages.size() - 1 || nonstopPosition == Integer.MAX_VALUE)) {
                    removeCallbacksAndMessages(null);
                    Log.d(TAG, "handleMessage: " + "停止自动轮播");
                    return;
                }

                mPageContainer.setCurrentItem(mPageContainer.getCurrentItem() + 1);
                mHandler.sendEmptyMessageDelayed(0, autoPlayDelay);
            }
        };
    }


    private class BannerPagerAdapter extends PagerAdapter {

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            if (nonstop) {
                position %= mBannerPages.size();
            }

            BannerPage bannerPage = mBannerPages.get(position);
            ImageView imageView = bannerPage.getView();

            if (mBannerPages.get(position).getOnClickListener() != null) {
                imageView.setTag(R.id.position,position);
                imageView.setOnClickListener(mBannerPages.get(position).getOnClickListener());
            }

            imageView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (!autoPlay) {
                        return false;
                    }
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            stopPlaying();
                            break;
                        case MotionEvent.ACTION_UP:
                            start2Play();
                            break;
//                        case MotionEvent.ACTION_CANCEL:
//                             不能在此处处理滑动BUG，要判断viewPager的状态
//                           mHandler.removeCallbacksAndMessages(null);
//                            mHandler.sendEmptyMessageDelayed(0, autoPlayDelay);
//                            break;

                    }
                    return false;
                }
            });
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            if (nonstop) {
                return Integer.MAX_VALUE;
            } else {
                return mBannerPages.size();
            }
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }
}
