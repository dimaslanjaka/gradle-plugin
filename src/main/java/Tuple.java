public class Tuple implements Comparable<Tuple> {
	private double t1;
	private int t2;

	public Tuple(double pair1, int pair2) {
		t1 = pair1;
		t2 = pair2;
	}

	public double getT1() {
		return t1;
	}

	public void setT1(double t) {
		this.t1 = t;
	}

	public int getT2() {
		return t2;
	}

	public void setT2(int t) {
		this.t2 = t;
	}

	@Override
	public int compareTo(Tuple o) {
		Tuple otherTuple = (Tuple) o;
		if (otherTuple.t1 < t1)
			return 1;
		else if (otherTuple.t1 > t2)
			return -1;
		else { // equal times
			if (otherTuple.t2 < t2)
				return 1;
			else
				return -1;
		}
	}
}
