package com.vdzon.windapp.db;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.vdzon.windapp.R;
import com.vdzon.windapp.pojo.Images;

public class ImageStorage {

	private static Images mImages = null;
	private static Map<Integer, Bitmap> mSpotImages= new HashMap<Integer, Bitmap>();

	public static Images getImages(Context context){
		if (mImages==null){
			mImages = new Images();
			Bitmap arrowBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.arrow);
			Bitmap okBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ok);
			Bitmap goodBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.good);
			Bitmap sadFaceBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.sadface);
			Bitmap warningBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.warning);
			Bitmap dangerBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.danger);
			Bitmap unknowmBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.unknown);
			mImages.setArrowBitmap(arrowBitmap);
			mImages.setAdviceBitmaps(okBitmap,goodBitmap,sadFaceBitmap,warningBitmap,dangerBitmap,unknowmBitmap);
		}
		return mImages;

	}

	public static Bitmap getSpotImage(Context context, int spotId){
		Bitmap result = mSpotImages.get(new Integer(spotId));
		if (result!=null) return result;
		result = BitmapFactory.decodeFile(context.getFileStreamPath("spot_"+spotId+".png").getAbsolutePath());
		mSpotImages.put(new Integer(spotId), result);
		return result;


	}

}
