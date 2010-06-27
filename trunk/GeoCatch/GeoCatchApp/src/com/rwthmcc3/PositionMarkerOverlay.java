package com.rwthmcc3;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class PositionMarkerOverlay extends ItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private GeoPoint prePoint=null,currentPoint=null;

	public PositionMarkerOverlay(GeoPoint prePoint, GeoPoint currentPoint, Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		this.prePoint = prePoint;
		this.currentPoint = currentPoint;
	}

	@Override
	protected OverlayItem createItem(int i) {
	  return mOverlays.get(i);
	}

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {

        super.draw(canvas, mapView, shadow);

        if(prePoint != null){
	        Paint paint=new Paint();
	        Point screenCoords=new Point();
	        Point screenCoords1=new Point();
	
	        mapView.getProjection().toPixels(prePoint, screenCoords);
	        int x1=screenCoords.x;
	        int y1=screenCoords.y;
	
	        mapView.getProjection().toPixels(currentPoint, screenCoords1);
	        int x2=screenCoords1.x;
	        int y2=screenCoords1.y;
	
	        paint.setStrokeWidth(3);
	        canvas.drawLine(x1, y1, x2, y2, paint);
        }
    }
	
	
	@Override
	public int size() {
		return mOverlays.size();
	}
	
	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}	

}
