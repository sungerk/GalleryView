package org.net.sunger.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.hardware.SensorManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;

import sunger.net.org.galleryview.R;

/**
 * Created by sunger on 16/4/29
 */
public class GalleryView extends RecyclerView {
    private static final int MAX_VELOCITYX = 2000;
    private static final float INFLEXION = 0.35f;
    private static float DECELERATION_RATE = (float) (Math.log(0.78) / Math.log(0.9));
    private static double FRICTION = 0.84;
    private static final int TYPE_FOOTER_OR_HEAFER = 1024;
    private final RecyclerView.AdapterDataObserver mDataObserver = new DataObserver();
    private Adapter mWrapAdapter;
    private OnItemSelectedListener mOnItemSelectedListener;
    private LinearLayoutManager mLinearLayoutManager;
    private double deceleration;
    private boolean isLeftDirection = false;
    private int mItemWidth;
    private int mCompatLeftOffsetForStayInCenter = 0;
    private int count = 0;


    public GalleryView(Context context) {
        this(context, null);
    }

    public GalleryView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GalleryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttributes(context, attrs);
        init(context);
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GalleryView);
        int width = typedArray.getDimensionPixelSize(R.styleable.GalleryView_itemWidth, 0);
        this.setItemWidth(width);
        typedArray.recycle();
    }


    private void init(Context context) {
        mLinearLayoutManager = new LinearLayoutManager(context);
        mLinearLayoutManager.setOrientation(OrientationHelper.HORIZONTAL);
        setLayoutManager(mLinearLayoutManager);
        deceleration = SensorManager.GRAVITY_EARTH
                * 39.3700787
                * context.getResources().getDisplayMetrics().density * 160.0f * FRICTION;
    }


    public   int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }
    private void computeOffsetForStayInCenter() {
        int screenWidth = getScreenWidth();
        int maxFullVisibilityItemCount = screenWidth / mItemWidth;
        count = maxFullVisibilityItemCount + 1;
        if (screenWidth % mItemWidth == 0) {
            if (screenWidth / mItemWidth % 2 == 0) {
                mCompatLeftOffsetForStayInCenter = mItemWidth / 2;
            } else {
                mCompatLeftOffsetForStayInCenter = 0;
            }
        } else {
            mCompatLeftOffsetForStayInCenter = (screenWidth - maxFullVisibilityItemCount * mItemWidth) / 2;
            if (mCompatLeftOffsetForStayInCenter <= mItemWidth / 2) {
                mCompatLeftOffsetForStayInCenter = mItemWidth - (screenWidth - (maxFullVisibilityItemCount - 1) * mItemWidth) / 2;
                if (maxFullVisibilityItemCount % 2 == 1) {
                    mCompatLeftOffsetForStayInCenter += mItemWidth / 2;
                    ++count;
                }
            }
        }
    }

    public void setItemWidth(int width) {
        this.mItemWidth = width;
        computeOffsetForStayInCenter();
    }


    private double getSplineDeceleration(double velocity) {
        return Math.log(INFLEXION * Math.abs(velocity)
                / (ViewConfiguration.getScrollFriction() * deceleration));
    }


    private double getSplineFlingDistance(double velocity) {
        velocity = Math.abs(velocity);
        final double l = getSplineDeceleration(velocity);
        final double decelMinusOne = DECELERATION_RATE - 1.0;
        return ViewConfiguration.getScrollFriction() * deceleration
                * Math.exp(DECELERATION_RATE / decelMinusOne * l);
    }


    private int computeInertiaScrollDistance(int velocityX) {
        double inertiaDistance = getSplineFlingDistance(velocityX);
        int inertiaScrollItemSize = (int) (inertiaDistance / mItemWidth);
        int inertiaScrollOffset = inertiaScrollItemSize * mItemWidth;
        if (isLeftDirection) {
            return inertiaScrollOffset;
        } else {
            return -inertiaScrollOffset;
        }
    }


    private int getCurrentFirstItemLeftOffset(View firstVisibilityView) {
        int gestureLeftOffset = firstVisibilityView.getLeft();
        return Math.abs(gestureLeftOffset);
    }

    private View getFirstVisibilityView() {
        return mLinearLayoutManager.getChildAt(0);
    }


    private boolean isFirstItemStayInLeft(View firstVisibilityView) {
        return getLayoutManager().getPosition(firstVisibilityView) == 0;
    }

    private int computeOffsetForScrollLeft(View firstVisibilityView) {
        int currentFirstItemLeftOffset = getCurrentFirstItemLeftOffset(firstVisibilityView);
        if (currentFirstItemLeftOffset == mCompatLeftOffsetForStayInCenter)
            return 0;
        if (currentFirstItemLeftOffset < mCompatLeftOffsetForStayInCenter) {
            return mCompatLeftOffsetForStayInCenter - currentFirstItemLeftOffset;
        } else {
            return mCompatLeftOffsetForStayInCenter + firstVisibilityView.getRight();
        }
    }


    private int computeOffsetForScrollRight(View firstVisibilityView) {
        int currentFirstItemLeftOffset = getCurrentFirstItemLeftOffset(firstVisibilityView);
        if (currentFirstItemLeftOffset == mCompatLeftOffsetForStayInCenter)
            return 0;
        if (currentFirstItemLeftOffset < mCompatLeftOffsetForStayInCenter) {
            return mCompatLeftOffsetForStayInCenter - currentFirstItemLeftOffset - mItemWidth;
        } else {
            return mCompatLeftOffsetForStayInCenter - currentFirstItemLeftOffset;
        }
    }

    private int computeNormalCompatOffset(View firstVisibilityView) {
        if (isLeftDirection) {
            return computeOffsetForScrollLeft(firstVisibilityView);
        } else {
            return computeOffsetForScrollRight(firstVisibilityView);
        }
    }

    private int computeSpecialCompatOffset(View firstVisibilityView) {
        int currentFirstItemLeftOffset = getCurrentFirstItemLeftOffset(firstVisibilityView);
        int offsetCount = currentFirstItemLeftOffset / mItemWidth;
        int targetOffset = offsetCount * mItemWidth - currentFirstItemLeftOffset;
        if (isLeftDirection) {
            return mItemWidth + targetOffset;
        } else {
            return targetOffset;
        }
    }

    private int computeOffset() {
        View firstVisibilityView = getFirstVisibilityView();
        if (isFirstItemStayInLeft(firstVisibilityView)) {
            return computeSpecialCompatOffset(firstVisibilityView);
        } else {
            return computeNormalCompatOffset(firstVisibilityView);
        }
    }

    private int resetVelocityX(int velocityX) {
        if (velocityX == 0) {
            if (isLeftDirection) {
                velocityX = 1;
            } else {
                velocityX = -1;
            }
        }
        if (Math.abs(velocityX) > MAX_VELOCITYX) {
            if (velocityX > 0) {
                velocityX = MAX_VELOCITYX;
            } else {
                velocityX = -MAX_VELOCITYX;
            }
        }
        return velocityX;
    }


    @Override
    public boolean fling(int velocityX, int velocityY) {
        if (getChildCount() == 0)
            return true;
        velocityX = resetVelocityX(velocityX);
        int inertiaScrollDistance = computeInertiaScrollDistance(velocityX);
        int offset = computeOffset();
        smoothScrollBy(offset + inertiaScrollDistance, 0);
        return true;
    }


    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state != SCROLL_STATE_IDLE || mOnItemSelectedListener == null)
            return;
        int firstCompletelyVisibleItemPosition = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();
        if (firstCompletelyVisibleItemPosition <= 1) {
                 mOnItemSelectedListener.onItemSelected(computeHorizontalScrollOffset() / mItemWidth);
        }else{
            mOnItemSelectedListener.onItemSelected(firstCompletelyVisibleItemPosition + count / 2 - 2);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int action = e.getAction();
        boolean ret = super.onTouchEvent(e);
        if (getScrollState() != SCROLL_STATE_IDLE) {
            return ret;
        }
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            fling(0, 0);
        }
        return ret;
    }


    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        if (Math.abs(dx) < 3)
            return;
        if (dx > 0) {
            isLeftDirection = true;
        } else {
            isLeftDirection = false;
        }
    }

    public GalleryView.OnItemSelectedListener getOnItemSelectedListener() {
        return mOnItemSelectedListener;
    }

    public void setOnItemSelectedListener(GalleryView.OnItemSelectedListener onItemSelectedListener) {
        mOnItemSelectedListener = onItemSelectedListener;
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int position);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mWrapAdapter = new WrapAdapter(adapter);
        super.setAdapter(mWrapAdapter);
        adapter.registerAdapterDataObserver(mDataObserver);
        mDataObserver.onChanged();
    }

    private class WrapAdapter extends RecyclerView.Adapter<ViewHolder> {

        private RecyclerView.Adapter adapter;

        public WrapAdapter(RecyclerView.Adapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_FOOTER_OR_HEAFER) {
                int width = (getScreenWidth() - mItemWidth) / 2;
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width, 1);
                View view = new View(parent.getContext());
                view.setLayoutParams(params);
                return new SimpleViewHolder(view);
            }
            return adapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (getItemViewType(position) == TYPE_FOOTER_OR_HEAFER) {
                return;
            }
            int adjPosition = position - 1;
            int adapterCount;
            if (adapter != null) {
                adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    adapter.onBindViewHolder(holder, adjPosition);
                }
            }
        }

        @Override
        public int getItemCount() {
            if (adapter != null) {
                return 2 + adapter.getItemCount();
            } else {
                return 2;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == getItemCount() - 1)
                return TYPE_FOOTER_OR_HEAFER;
            int adjPosition = position - 1;
            int adapterCount;
            if (adapter != null) {
                adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    return adapter.getItemViewType(adjPosition);
                }
            }
            return -1;
        }

        @Override
        public long getItemId(int position) {
            if (adapter != null && position >= 1) {
                int adjPosition = position - 1;
                int adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    return adapter.getItemId(adjPosition);
                }
            }
            return -1;
        }

        @Override
        public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
            if (adapter != null) {
                adapter.unregisterAdapterDataObserver(observer);
            }
        }

        @Override
        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            if (adapter != null) {
                adapter.registerAdapterDataObserver(observer);
            }
        }

        private class SimpleViewHolder extends RecyclerView.ViewHolder {
            public SimpleViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    private class DataObserver extends RecyclerView.AdapterDataObserver {
        @Override
        public void onChanged() {
            mWrapAdapter.notifyDataSetChanged();
        }
        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mWrapAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    }

}

