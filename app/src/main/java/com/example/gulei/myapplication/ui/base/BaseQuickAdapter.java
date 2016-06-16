
package com.example.gulei.myapplication.ui.base;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.example.gulei.myapplication.ui.animation.AlphaInAnimation;
import com.example.gulei.myapplication.ui.animation.BaseAnimation;
import com.example.gulei.myapplication.ui.animation.ScaleInAnimation;
import com.example.gulei.myapplication.ui.animation.SlideInBottomAnimation;
import com.example.gulei.myapplication.ui.animation.SlideInLeftAnimation;
import com.example.gulei.myapplication.ui.animation.SlideInRightAnimation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;


/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public abstract class BaseQuickAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //是否只有初次加载item的时候才有动画
    private boolean mFirstOnlyEnable = true;
    //动画效果的开关
    private boolean mOpenAnimationEnable = false;
    //空布局的状态
    private boolean mEmptyEnable;
    //插值器  赋值是线性的
    private Interpolator mInterpolator = new LinearInterpolator();
    //item载入动画的时间
    private int mDuration = 300;
    //最新执行动画的position
    private int mLastPosition = -1;
    //自定义的item点击事件监听
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;
    //自定义的item长按事件监听
    private OnRecyclerViewItemLongClickListener onRecyclerViewItemLongClickListener;
    //用户自定义的动画
    @AnimationType
    private BaseAnimation mCustomAnimation;
    //默认的动画
    private BaseAnimation mSelectAnimation = new AlphaInAnimation();
    //一页加载的数量
    private int pageSize = -1;
    //普通的item内容布局
    private View mContentView;
    //空内容时的布局
    private View mEmptyView;

    protected static final String TAG = BaseQuickAdapter.class.getSimpleName();
    //上下文
    protected Context mContext;
    //普通的item内容布局资源id
    protected int mLayoutResId;
    protected LayoutInflater mLayoutInflater;
    //数据源
    protected List<T> mData;
    //item类型 内容为空时的view
    protected static final int EMPTY_VIEW = 0x00000555;

    @IntDef({ALPHAIN, SCALEIN, SLIDEIN_BOTTOM, SLIDEIN_LEFT, SLIDEIN_RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AnimationType {
    }

    /**
     * Use with {@link #openLoadAnimation}
     */
    public static final int ALPHAIN = 0x00000001;
    /**
     * Use with {@link #openLoadAnimation}
     */
    public static final int SCALEIN = 0x00000002;
    /**
     * Use with {@link #openLoadAnimation}
     */
    public static final int SLIDEIN_BOTTOM = 0x00000003;
    /**
     * Use with {@link #openLoadAnimation}
     */
    public static final int SLIDEIN_LEFT = 0x00000004;
    /**
     * Use with {@link #openLoadAnimation}
     */
    public static final int SLIDEIN_RIGHT = 0x00000005;

    /**
     * setting up the size to decide the loading more data funcation whether enable
     * enable if the data size than pageSize,or diable
     * 当前每页size的设置
     *
     * @param pageSize
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * return the value of pageSize
     *
     * @return
     */
    public int getPageSize() {
        return this.pageSize;
    }

    /**
     * 自定义item点击事件的监听 设置
     * @param onRecyclerViewItemClickListener
     */
    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    /**
     * 自定义的点击事件响应
     */
    public interface OnRecyclerViewItemClickListener {
        public void onItemClick(View view, int position);
    }

    /**
     * 自定义item长按事件的监听设置
     * @param onRecyclerViewItemLongClickListener
     */
    public void setOnRecyclerViewItemLongClickListener(OnRecyclerViewItemLongClickListener onRecyclerViewItemLongClickListener) {
        this.onRecyclerViewItemLongClickListener = onRecyclerViewItemLongClickListener;
    }

    /**
     * 自定义item长按事件的监听
     */
    public interface OnRecyclerViewItemLongClickListener {
        public boolean onItemLongClick(View view, int position);
    }

    //viewHolder中每个子view的点击事件
    private OnRecyclerViewItemChildClickListener mChildClickListener;
    /**
     * 通过外部添加子view点击事件的监听的集合
     * @param childClickListener
     */
    public void setOnRecyclerViewItemChildClickListener(OnRecyclerViewItemChildClickListener childClickListener) {
        this.mChildClickListener = childClickListener;
    }

    /**
     * ViewHolder中每个子view的点击事件的集合
     */
    public interface OnRecyclerViewItemChildClickListener {
        void onItemChildClick(BaseQuickAdapter adapter, View view, int position);
    }

    /**
     *  ViewHolder中每个子view的点击事件的封装 通过这层封装，
     *  可以将各个子view的点击事件放到一个方法中处理
     *  形式： switch (view.getId()) {
     *           case R.id.tweetAvatar:
     *              content = "img:" + status.getUserAvatar();
     *              break;
     *          case R.id.tweetName:
     *              content = "name:" + status.getUserName();
     *              break;
     *          }
     */
    public class OnItemChildClickListener implements View.OnClickListener {
        public int position;

        @Override
        public void onClick(View v) {
            if (mChildClickListener != null)
                mChildClickListener.onItemChildClick(BaseQuickAdapter.this, v, position);
        }
    }


    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param context     The context.
     * @param layoutResId The layout resource id of each item.
     * @param data        A new list is created out of this one to avoid mutable list
     */
    public BaseQuickAdapter(Context context, int layoutResId, List<T> data) {
        this.mData = data == null ? new ArrayList<T>() : new ArrayList<T>(data);
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        if (layoutResId != 0) {
            this.mLayoutResId = layoutResId;
        }
    }

    public BaseQuickAdapter(Context context, List<T> data) {
        this(context, 0, data);
    }

    public BaseQuickAdapter(Context context, View contentView, List<T> data) {
        this(context, 0, data);
        mContentView = contentView;
    }

    public BaseQuickAdapter(Context context) {
        this(context, null);
    }

    /**
     * 删除一条数据
     * @param position
     */
    public void remove(int position) {
        mData.remove(position);
        notifyItemRemoved(position);

    }

    /**
     * 在指定位置添加数据
     * @param position 位置
     * @param item
     */
    public void add(int position, T item) {
        mData.add(position, item);
        notifyItemInserted(position);
    }


    /**
     * setting up a new instance to data;
     * 重新设置数据源
     * @param data
     */
    public void setNewData(List<T> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    /**
     * additional data;
     * 在数据源末尾添加一条数据
     * @param data
     */
    public void addData(List<T> data) {
        this.mData.addAll(data);
        notifyDataSetChanged();
    }


    public List getData() {
        return mData;
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    public T getItem(int position) {
        return mData.get(position);
    }

    public int getmEmptyViewCount() {
        return mEmptyView == null ? 0 : 1;
    }

    @Override
    public int getItemCount() {
        int count = mData.size();
        mEmptyEnable = false;
        if (count == 0) {
            mEmptyEnable = true;
            count += getmEmptyViewCount();
        }
        return count;
    }

    /**
     * 根据position确定item的type
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if (mEmptyView != null && mData.size() == 0 && mEmptyEnable) {
            return EMPTY_VIEW;
        }
        return getDefItemViewType(position);
    }

    protected int getDefItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder baseViewHolder = null;
        switch (viewType) {
            case EMPTY_VIEW:
                baseViewHolder = new BaseViewHolder(mContext, mEmptyView);
                break;
            default:
                baseViewHolder = onCreateDefViewHolder(parent, viewType);
        }
        return baseViewHolder;

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int positions) {

        switch (holder.getItemViewType()) {
            case 0:
                convert((BaseViewHolder) holder, mData.get(positions),positions);
                initItemClickListener(holder, (BaseViewHolder) holder);
                addAnimation(holder);
                break;
            case EMPTY_VIEW:
                break;
            default:
                convert((BaseViewHolder) holder, mData.get(positions),positions);
                onBindDefViewHolder((BaseViewHolder) holder, mData.get(positions));
                initItemClickListener(holder, (BaseViewHolder) holder);
                break;
        }

    }

    protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        return createBaseViewHolder(parent, mLayoutResId);
    }

    protected BaseViewHolder createBaseViewHolder(ViewGroup parent, int layoutResId) {
        if (mContentView == null) {
            return new BaseViewHolder(mContext, getItemView(layoutResId, parent));
        }
        return new BaseViewHolder(mContext, mContentView);
    }

    /**
     * Sets the view to show if the adapter is empty
     */
    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
    }

    /**
     * When the current adapter is empty, the BaseQuickAdapter can display a special view
     * called the empty view. The empty view is used to provide feedback to the user
     * that no data is available in this AdapterView.
     *
     * @return The view to show if the adapter is empty.
     */
    public View getEmptyView() {
        return mEmptyView;
    }

    /**
     * 因为mData是adapter自己管理的list，所以更新ui的时候必须调用此方法
     * @param data
     */
    public void notifyDataChanged(List<T> data) {
        mData.addAll(data);
        notifyDataSetChanged();
    }

    /**
     * 整个数据源的更新，会先clear
     * @param data
     */
    public void notifyDataReset(List<T> data){
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }
    private void initItemClickListener(final RecyclerView.ViewHolder holder, BaseViewHolder baseViewHolder) {
        if (onRecyclerViewItemClickListener != null) {
            baseViewHolder.convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRecyclerViewItemClickListener.onItemClick(v, holder.getLayoutPosition());
                }
            });
        }
        if (onRecyclerViewItemLongClickListener != null) {
            baseViewHolder.convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return onRecyclerViewItemLongClickListener.onItemLongClick(v, holder.getLayoutPosition());
                }
            });
        }
    }

    private void addAnimation(RecyclerView.ViewHolder holder) {
        if (mOpenAnimationEnable) {
            if (!mFirstOnlyEnable || holder.getLayoutPosition() > mLastPosition) {
                BaseAnimation animation = null;
                if (mCustomAnimation != null) {
                    animation = mCustomAnimation;
                } else {
                    animation = mSelectAnimation;
                }
                for (Animator anim : animation.getAnimators(holder.itemView)) {
                    anim.setDuration(mDuration).start();
                    anim.setInterpolator(mInterpolator);
                }
                mLastPosition = holder.getLayoutPosition();
            }
        }
    }

    public View getItemView(int layoutResId, ViewGroup parent) {
        return mLayoutInflater.inflate(layoutResId, parent, false);
    }


    /**
     * Two item type can override it
     *
     * @param holder
     * @param item
     */
    @Deprecated
    protected void onBindDefViewHolder(BaseViewHolder holder, T item) {
    }

    public interface RequestLoadMoreListener {

        void onLoadMoreRequested();
    }


    /**
     * Set the view animation type.
     *
     * @param animationType One of {@link #ALPHAIN}, {@link #SCALEIN}, {@link #SLIDEIN_BOTTOM}, {@link #SLIDEIN_LEFT}, {@link #SLIDEIN_RIGHT}.
     */
    public void openLoadAnimation(@AnimationType int animationType) {
        this.mOpenAnimationEnable = true;
        mCustomAnimation = null;
        switch (animationType) {
            case ALPHAIN:
                mSelectAnimation = new AlphaInAnimation();
                break;
            case SCALEIN:
                mSelectAnimation = new ScaleInAnimation();
                break;
            case SLIDEIN_BOTTOM:
                mSelectAnimation = new SlideInBottomAnimation();
                break;
            case SLIDEIN_LEFT:
                mSelectAnimation = new SlideInLeftAnimation();
                break;
            case SLIDEIN_RIGHT:
                mSelectAnimation = new SlideInRightAnimation();
                break;
            default:
                break;
        }
    }

    /**
     * Set Custom ObjectAnimator
     *
     * @param animation ObjectAnimator
     */
    public void openLoadAnimation(BaseAnimation animation) {
        this.mOpenAnimationEnable = true;
        this.mCustomAnimation = animation;
    }

    public void openLoadAnimation() {
        this.mOpenAnimationEnable = true;
    }


    public void isFirstOnly(boolean firstOnly) {
        this.mFirstOnlyEnable = firstOnly;
    }

    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * @param helper A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    protected abstract void convert(BaseViewHolder helper, T item,int position);


    @Override
    public long getItemId(int position) {
        return position;
    }


}
