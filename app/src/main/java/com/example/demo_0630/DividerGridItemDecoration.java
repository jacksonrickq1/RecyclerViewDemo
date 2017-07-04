package com.example.demo_0630;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;

/**
 * Created by Rick on 2017/7/3.
 */

/**
 * onDraw(Canvas c, RecyclerView parent, RecyclerView.State state)
 此方法需要我们计算出绘制的分割线的【位置和范围】，并绘制在Canvas上。主要的逻辑就是通过parent获取到child，然后从child中获取到Item的四个边的位置，从而计算出位置和范围。

 getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
 此方法主要是为了在每个Item的某一位置预留出分割线的空间 ，从而让Decoration绘制在预留的空间内。

 onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state)
 此方法和onDraw类同，不过触发时机不同，onDraw()是在绘制childView之前触发，而onDrawOver()是在绘制childView完成之后。主要用途是通过结合ValueAnimation实现分割线延时动画，目前很少应用到。当然也有其他用途，在此不多做介绍
 */

public class DividerGridItemDecoration extends RecyclerView.ItemDecoration {
    private String TAG="quan";
//    public int mCount=0;

    private static final int[] ATTRS=new int[] {
      android.R.attr.listDivider
    };

    public static final int HORIZONTAL_LIST= LinearLayoutManager.HORIZONTAL;
    public static final int VERTICAL_LIST=LinearLayoutManager.VERTICAL;

    private Drawable mDivider;
    private int mOrientation;
    public DividerGridItemDecoration(Context context)
    {
        final TypedArray a=context.obtainStyledAttributes(ATTRS);
        mDivider=a.getDrawable(0);
        a.recycle();
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent) {
        Log.d(TAG,"onDraw");
        drawHorizontal(c,parent);
        drawVertical(c,parent);
    }

    private int getSpanCount(RecyclerView parent)
    {
        int spanCount=-1;
        RecyclerView.LayoutManager layoutManager=parent.getLayoutManager();
        if(layoutManager instanceof GridLayoutManager)
        {
            spanCount=((GridLayoutManager)layoutManager).getSpanCount();
        }else if(layoutManager instanceof StaggeredGridLayoutManager)
        {
            spanCount=((StaggeredGridLayoutManager)layoutManager).getSpanCount();
        }
        return spanCount;
    }

    public void drawVertical(Canvas c, RecyclerView parent)
    {
        int childCount=parent.getChildCount();
        Log.d(TAG,"drawVertical childCount = "+childCount);
        for(int i=0;i<childCount;i++)
        {
            final View child=parent.getChildAt(i);
            final RecyclerView.LayoutParams params= (RecyclerView.LayoutParams) child.getLayoutParams();

            final int left=child.getRight()+params.rightMargin;
            final int right=left+mDivider.getIntrinsicWidth();
            final int top=child.getTop()-params.topMargin;
            final int bottom=child.getBottom()+params.bottomMargin;
            Log.d(TAG,"right space 2 = "+left+"-"+top+"-"+right+"-"+bottom);
            mDivider.setBounds(left,top,right,bottom);
            mDivider.draw(c);
//            Log.d(TAG,"drawVertical index = "+i);
        }
    }

    public void drawHorizontal(Canvas c, RecyclerView parent)
    {
        int childCount = parent.getChildCount();
        Log.d(TAG,"drawHorizontal childCount = "+childCount);
        for (int i = 0; i < childCount; i++)
        {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin
                    + mDivider.getIntrinsicWidth();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
//            Log.d(TAG,"drawHorizontal  index = "+i);
        }
    }

    private boolean isLastColumn(RecyclerView parent, int pos, int spanCount, int childCount)
    {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager)
        {
            if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
            {
                return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager)
        {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL)
            {
                Log.d(TAG,"isLastColumn = "+(pos + 1) % spanCount);
                if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
                {
                    return true;
                }
            } else
            {
                childCount = childCount - childCount % spanCount;
//                Log.d(TAG,"isLastColumn childCount= "+childCount);
                if (pos >= childCount)// 如果是最后一列，则不需要绘制右边
                    return true;
            }
        }
        return false;
    }

    private boolean isLastRow(RecyclerView parent, int pos, int spanCount,
                              int childCount)
    {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager)
        {
            childCount = childCount - childCount % spanCount;
            if (pos >= childCount)// 如果是最后一行，则不需要绘制底部
                return true;
        } else if (layoutManager instanceof StaggeredGridLayoutManager)
        {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL)
            {
                childCount = childCount - (childCount % spanCount==0?spanCount:childCount % spanCount);
//                childCount = childCount - childCount % spanCount;
                // 如果是最后一行，则不需要绘制底部
                Log.d(TAG,"isLastRow childCount= "+childCount+"-pos = "+pos);
                if (pos >= childCount)
                    return true;
            } else
            // StaggeredGridLayoutManager 且横向滚动
            {
                // 如果是最后一行，则不需要绘制底部
                if ((pos + 1) % spanCount == 0)
                {
                    return true;
                }
            }
        }
        return false;
    }

    /***
     * getItemOffsets方法设置的是item view的padding属性
     * @param outRect
     * @param view
     * @param parent
     * @param state
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int spanCount=getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();
        int itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        int column=itemPosition%spanCount;
        int right= mDivider.getIntrinsicWidth()-(column+1)*mDivider.getIntrinsicWidth()/spanCount;
        if (isLastRow(parent, itemPosition, spanCount, childCount))// 如果是最后一行，则不需要绘制底部
        {
            outRect.set(0, 0, right, 0);
        }else if (isLastColumn(parent, itemPosition, spanCount, childCount))// 如果是最后一列，则不需要绘制右边
        {
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
        } else
        {
            outRect.set(0, 0, right,
                    mDivider.getIntrinsicHeight());
        }
    }

//    @Override
//    public void getItemOffsets(Rect outRect, int itemPosition,
//                               RecyclerView parent)
//    {
//        int spanCount = getSpanCount(parent);
//        int childCount = parent.getAdapter().getItemCount();
//        int column=itemPosition%spanCount;
//        int right= mDivider.getIntrinsicWidth()-(column+1)*mDivider.getIntrinsicWidth()/spanCount;
//
////        int right= mDivider.getIntrinsicWidth();
//        Log.d(TAG,"getItemOffsets  childCount = "+childCount+"-itemPosition = "+itemPosition+"-spanCount = "+spanCount);
//        Log.d(TAG,"right space = "+mDivider.getIntrinsicWidth()+"-"+right);
//        if (isLastRow(parent, itemPosition, spanCount, childCount))// 如果是最后一行，则不需要绘制底部
//        {
//            outRect.set(0, 0, right, 0);
//        }else if (isLastColumn(parent, itemPosition, spanCount, childCount))// 如果是最后一列，则不需要绘制右边
//        {
//            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
//        } else
//        {
//            outRect.set(0, 0, right,
//                    mDivider.getIntrinsicHeight());
//        }
//    }
}
