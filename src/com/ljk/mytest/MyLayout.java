package com.ljk.mytest;

import com.ljk.bss_mymqtt.R;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyLayout extends LinearLayout {
	private ViewDragHelper mDragHelper;
	private View drawView1;
	private View drawView2;
	private TextView showTextView;
	public MyLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mDragHelper=ViewDragHelper.create(this,1.0f,new DragHelperCallback());
	}
	public MyLayout(Context context) {
		this(context,null);
		// TODO Auto-generated constructor stub
	}
	public MyLayout(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}
	@Override
	protected void onFinishInflate() {
		drawView1=findViewById(R.id.myDragView1);
		drawView2=findViewById(R.id.myDragView2);
		showTextView=(TextView) findViewById(R.id.showtext);
	}
	private class DragHelperCallback extends ViewDragHelper.Callback{

		@Override
		public boolean tryCaptureView(View arg0, int arg1) {
			// TODO Auto-generated method stub
			return arg0==drawView1;
		}
		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			// TODO Auto-generated method stub
/*			在DragHelperCallback中实现clampViewPositionHorizontal方法，
			并且返回一个适当的数值就能实现横向拖动效果，
			clampViewPositionHorizontal的第二个参数是指当前拖动子view应该到达的x坐标。
			所以按照常理这个方法原封返回第二个参数就可以了，但为了让被拖动的view遇到边界之后就不在拖动，
			对返回的值做了更多的考虑*/
			mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
			final int leftBounds=getPaddingLeft();//23
			final int rightBounds=getWidth()-drawView1.getWidth()-getPaddingRight();//575
			final int newleft=Math.min(Math.max(left,leftBounds), rightBounds);
			Log.i("TAG","2--"+left);//22
			return newleft;
		}
		@Override
		public int clampViewPositionVertical(View child, int top, int dy) {
			// TODO Auto-generated method stub
			final int topBounds=getPaddingTop();
			final int bottomBounds=getHeight()-drawView1.getHeight();
			final int newtop=Math.min(Math.max(top,topBounds), bottomBounds);
			return newtop;
		}
		@Override  
		public void onEdgeTouched(int edgeFlags, int pointerId) {  
		    super.onEdgeTouched(edgeFlags, pointerId);  
		    Toast.makeText(getContext(), "edgeTouched", Toast.LENGTH_SHORT).show();  
		} 
		@Override  
		public void onEdgeDragStarted(int edgeFlags, int pointerId) {  
		    mDragHelper.captureChildView(drawView2, pointerId);  
		}
		
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		final int action=MotionEventCompat.getActionMasked(ev);
		if (action==MotionEvent.ACTION_CANCEL||action==MotionEvent.ACTION_UP) {
			mDragHelper.cancel();
			return false;
		}

		return mDragHelper.shouldInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mDragHelper.processTouchEvent(event);
		return true;
	}
	

}
