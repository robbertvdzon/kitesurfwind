package com.vdzon.windapp.pojo;

import android.graphics.Bitmap;

public class Images {

	private Bitmap arrowBitmap = null;
	private Bitmap okBitmap = null;
	private Bitmap goodBitmap = null;
	private Bitmap sadFaceBitmap = null;
	private Bitmap warningBitmap = null;
	private Bitmap dangerBitmap = null;
	private Bitmap unknowmBitmap = null;

	public void setAdviceBitmaps(Bitmap okBitmap,Bitmap goodBitmap,Bitmap sadFaceBitmap,Bitmap warningBitmap,Bitmap dangerBitmap,Bitmap unknowmBitmap){
		this.okBitmap=okBitmap;
		this.goodBitmap=goodBitmap;
		this.sadFaceBitmap=sadFaceBitmap;
		this.warningBitmap=warningBitmap;
		this.dangerBitmap=dangerBitmap;
		this.unknowmBitmap=unknowmBitmap;
	}

	public Bitmap getArrowBitmap() {
		return arrowBitmap;
	}

	public void setArrowBitmap(Bitmap arrowBitmap) {
		this.arrowBitmap = arrowBitmap;
	}

	public Bitmap getOkBitmap() {
		return okBitmap;
	}

	public void setOkBitmap(Bitmap okBitmap) {
		this.okBitmap = okBitmap;
	}

	public Bitmap getGoodBitmap() {
		return goodBitmap;
	}

	public void setGoodBitmap(Bitmap goodBitmap) {
		this.goodBitmap = goodBitmap;
	}

	public Bitmap getSadFaceBitmap() {
		return sadFaceBitmap;
	}

	public void setSadFaceBitmap(Bitmap sadFaceBitmap) {
		this.sadFaceBitmap = sadFaceBitmap;
	}

	public Bitmap getWarningBitmap() {
		return warningBitmap;
	}

	public void setWarningBitmap(Bitmap warningBitmap) {
		this.warningBitmap = warningBitmap;
	}

	public Bitmap getDangerBitmap() {
		return dangerBitmap;
	}

	public void setDangerBitmap(Bitmap dangerBitmap) {
		this.dangerBitmap = dangerBitmap;
	}

	public Bitmap getUnknowmBitmap() {
		return unknowmBitmap;
	}

	public void setUnknowmBitmap(Bitmap unknowmBitmap) {
		this.unknowmBitmap = unknowmBitmap;
	}


}
