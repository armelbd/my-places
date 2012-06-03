package net.esial.myplaces.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

public class ActionBarAutoCompleteTextView extends AutoCompleteTextView {

	public ActionBarAutoCompleteTextView(Context context) {
		super(context);
	}
	
	public ActionBarAutoCompleteTextView(Context context, AttributeSet attrs) {
		super(context,attrs);
	}
	
	public ActionBarAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context,attrs,defStyle);
	}
	
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        widthMode = MeasureSpec.EXACTLY;
        super.onMeasure(MeasureSpec.makeMeasureSpec(width, widthMode), heightMeasureSpec);
    }

}
