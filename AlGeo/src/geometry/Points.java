package geometry;

import java.awt.Color;
import java.awt.Graphics;

import geometry.test.Dimensions;
import geometry.test.Drawable;

public class Points implements Drawable {
	public final Point[] points;
	
	public Points(Point... points) {
		this.points = points;
	}
	
	public Point getMinY() {
		Point min = points[0];
		for(Point p : points) {
			if( min.getY() <= p.getY() ) {
				if(min.getY() == p.getY()) {
					// Compare x-values
					if(min.getX() > p.getX() ){
						min = p;
					}
				}
			} else {
				min = p;
			}
        }
		return min;
	}
	
	@Override
	public void paint(Graphics g, Dimensions dimensions, Color color) {
		 for(int i = 0; i < points.length - 1; ++i) {
			 points[1].paint(g, dimensions, color);
		 }
	}
}
