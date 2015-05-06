package geometry;

public class MyAnstieg {
	
	Double m;
	
	public MyAnstieg(Double m) {
		this.m = m;
	}
	
	@Override
	public boolean equals(Object obj) {
		Double objm = (Double) obj;
		if (Math.abs(this.m - objm) > C.E) {
			System.out.println("nicht drin");
			return false;
		}
		else {
			System.out.println("schon drin");
			return true;
		}
	}
}
