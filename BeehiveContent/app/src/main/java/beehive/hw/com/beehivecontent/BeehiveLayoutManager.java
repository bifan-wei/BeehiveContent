package beehive.hw.com.beehivecontent;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bifan-wei
 */

public class BeehiveLayoutManager extends RecyclerView.LayoutManager {
    public static final int DEFAULT_GROUP_SIZE = 5;
    public  int FIRSTGROUP_MARGIN_SECONDGROUP = 50;//第一小组与第二小组间距
    public  int GROUP_PADDING = 120;//组距

    public Context mContext;
    private int mColumnSize;
    private int mHorizontalOffset;
    private int mVerticalOffset;
    private int mTotalWidth;
    private int mTotalHeight;
    private Pool<Rect> mItemFrames;

    public BeehiveLayoutManager(Context mContext) {
        this(DEFAULT_GROUP_SIZE,  mContext);
    }

    public void setGroupPadding(int padding){
        GROUP_PADDING = padding;
    }

    public void setFristMarginSecondGroup(int margin){
        FIRSTGROUP_MARGIN_SECONDGROUP = margin;
    }

    public BeehiveLayoutManager(int groupSize, Context mContext) {
        mColumnSize = groupSize;
        this.mContext = mContext;
        mItemFrames = new Pool<>(new Pool.New<Rect>() {
            @Override
            public Rect get() {
                return new Rect();
            }
        });
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() <= 0 || state.isPreLayout()) {
            return;
        }

        detachAndScrapAttachedViews(recycler);

        int rvwidth = getRecyclerViewWidth();//RecyclerView width

        int itemWidth = rvwidth / mColumnSize;//正方形宽度
        int itemHeight = itemWidth;

        int firstgroupnum = mColumnSize / 2 ;//第一小组最多显示个数
        int secondgroupnum = mColumnSize % 2 == 0 ? mColumnSize / 2 : mColumnSize / 2 + 1;//第二组最多显示个数

        float r = itemWidth / 2;//内切圆半径

        float fleft = (rvwidth - (itemWidth * firstgroupnum + r * (firstgroupnum - 1))) / 2;//第一组开始偏移量
        float firstgroupitemleftposition = 0;//第一组item左边位置

        float sright = fleft-itemWidth*3/4;//第二组开始偏移量
        float d = (float) (itemHeight / 4 * (2 - Math.sqrt(3)));//六边形到边到内切圆的距离
        float secondgroupmarginfirstgroup = (float) itemHeight/2 - d;
        float topmargin = 50;
        float toppositoion = 0;

        List<GroupData> groupDatas  = GetGroupData(firstgroupnum,secondgroupnum);

        for(int index =0;index<groupDatas.size();index++){
            toppositoion = index*itemHeight+topmargin+GROUP_PADDING*index;
            toppositoion = toppositoion-d*2*index;
            GroupData g = groupDatas.get(index);
            for(int firstgroupindex =0;firstgroupindex<g.FirstGroup.size();firstgroupindex++){
                int left = (int) (fleft+firstgroupindex*(itemWidth+r));
                int top = (int) toppositoion;
                int right = left+itemWidth;
                int bottom = top+itemHeight;
                Rect rect = g.FirstGroup.get(firstgroupindex).rect;
                rect.set(left,top,right,bottom);
            }
            toppositoion = toppositoion+secondgroupmarginfirstgroup+FIRSTGROUP_MARGIN_SECONDGROUP;
            for(int secondgroupindex =0;secondgroupindex<g.SecondGroup.size();secondgroupindex++){
                int left = (int) (sright+secondgroupindex*(itemWidth+r));
                int top = (int) toppositoion;
                int right = left+itemWidth;
                int bottom = top+itemHeight;
                Rect rect = g.SecondGroup.get(secondgroupindex).rect;
                rect.set(left,top,right,bottom);
            }
        }



        mTotalWidth = Math.max(firstgroupnum * itemWidth, getHorizontalSpace());

        int totalHeight = getGroupSize() * itemHeight;
        if (!isItemInFirstLine(getItemCount() - 1)) {
            totalHeight += itemHeight / 2;
        }
        mTotalHeight = Math.max(totalHeight, getVerticalSpace());
        fill(recycler, state);
    }

    /** 将数据转化为组数据
     * @param firstgroupnum 第一小组最多显示个数
     * @param secondgroupnum 第二小组最多显示个数
     * @return
     */
    private List<GroupData> GetGroupData(int firstgroupnum, int secondgroupnum) {
        int groupnums = getItemCount()/(firstgroupnum+secondgroupnum);
        if(getItemCount()%(firstgroupnum+secondgroupnum)!=0){
            groupnums++;
        }
        List<GroupData> groupdata = new ArrayList<>();
        int ItemIndex = 0;
        for(int index  = 0;index<groupnums;index++){
            GroupData g = new GroupData();
            for(int i=0;i<firstgroupnum;i++){
                GroupItem item = new GroupItem();
                item.itemindex = ItemIndex++;
                item.rect = mItemFrames.get(item.itemindex);
                g.FirstGroup.add(item);
            }

            for(int i=0;i<secondgroupnum;i++){
                GroupItem item = new GroupItem();
                item.itemindex = ItemIndex++;
                item.rect = mItemFrames.get(item.itemindex);
                g.SecondGroup.add(item);
            }
            groupdata.add(g);
        }
        return groupdata;
    }
    public int getItemWidth(){
        return getRecyclerViewWidth()/mColumnSize;
    }

    private int getRecyclerViewWidth() {
        int width = getWidth();
        if (width <= 0) {
            WindowManager m = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics met = new DisplayMetrics();
            m.getDefaultDisplay().getMetrics(met);
            width = met.widthPixels;
        }
        return width;
    }

    private void fill(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() <= 0 || state.isPreLayout()) {
            return;
        }
        Rect displayRect = new Rect(mHorizontalOffset, mVerticalOffset,
                getHorizontalSpace() + mHorizontalOffset,
                getVerticalSpace() + mVerticalOffset);


        for (int i = 0; i < getItemCount(); i++) {
            Rect frame = mItemFrames.get(i);
            if (Rect.intersects(displayRect, frame)) {
                View scrap = recycler.getViewForPosition(i);
                addView(scrap);
                measureChildWithMargins(scrap, 0, 0);
                layoutDecorated(scrap, frame.left - mHorizontalOffset, frame.top - mVerticalOffset,
                        frame.right - mHorizontalOffset, frame.bottom - mVerticalOffset);
            }
        }
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);
        if (mVerticalOffset + dy < 0) {
            dy = -mVerticalOffset;
        } else if (mVerticalOffset + dy > mTotalHeight - getVerticalSpace()) {
            dy = mTotalHeight - getVerticalSpace() - mVerticalOffset;
        }

        offsetChildrenVertical(-dy);
        fill(recycler, state);
        mVerticalOffset += dy;
        return dy;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);
        if (mHorizontalOffset + dx < 0) {
            dx = -mHorizontalOffset;
        } else if (mHorizontalOffset + dx > mTotalWidth - getHorizontalSpace()) {
            dx = mTotalWidth - getHorizontalSpace() - mHorizontalOffset;
        }

        offsetChildrenHorizontal(-dx);
        fill(recycler, state);
        mHorizontalOffset += dx;
        return dx;
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public boolean canScrollHorizontally() {
        return false;
    }

    private boolean isItemInFirstLine(int index) {
        int firstLineSize = mColumnSize / 2 ;
        return index < firstLineSize || (index >= mColumnSize && index % mColumnSize < firstLineSize);
    }

    private int getGroupSize() {
        return (int) Math.ceil(getItemCount() / (float) mColumnSize);
    }
    private int getHorizontalSpace() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }
    private int getVerticalSpace() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    private class GroupData{
        int itemIndex = 0;
        List<GroupItem> FirstGroup = new ArrayList<>();
        List<GroupItem> SecondGroup = new ArrayList<>();
    }

    private class GroupItem{
        Rect rect = null;
        int itemindex = 0;
    }
}
