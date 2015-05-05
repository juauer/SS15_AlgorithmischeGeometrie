package geometry;

import java.awt.Color;
import java.awt.Graphics;

import geometry.test.Dimensions;
import geometry.test.Drawable;

public class Points implements Drawable{
	public final Point[] points;
	
	public Points(Point... points) {
		this.points = points;
	}
	
	@Override
	public void paint(Graphics g, Dimensions dimensions, Color color) {
		 for(int i = 0; i < points.length - 1; ++i) {
			 points[1].paint(g, dimensions, color);
		 }
	}
}
