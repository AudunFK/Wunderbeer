package com.example.s172860_mapp3;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.util.LruCache;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;



/**
 * class that takes a string and uses MetricAffectingSpan to alter the appearance of a string 
 *
 */
public class TypefaceSpan extends MetricAffectingSpan {
	
	private static LruCache<String, Typeface> sTypefaceCache =
			new LruCache<String, Typeface>(12);

	private Typeface mTypeface;

	
	public TypefaceSpan(Context context, String typefaceName) {
		mTypeface = sTypefaceCache.get(typefaceName);

		if (mTypeface == null) {
			mTypeface = Typeface.createFromAsset(context.getApplicationContext()
					.getAssets(), String.format("fonts/%s", typefaceName));

			// Cache the loaded Typeface
			sTypefaceCache.put(typefaceName, mTypeface);
		}
	}

	@Override
	public void updateMeasureState(TextPaint p) {
		p.setTypeface(mTypeface);

		// This flag is required for proper typeface rendering
		p.setFlags(p.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
	}

	@Override
	public void updateDrawState(TextPaint tp) {
		tp.setTypeface(mTypeface);

		// This flag is required for proper typeface rendering
		tp.setFlags(tp.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
	}
}