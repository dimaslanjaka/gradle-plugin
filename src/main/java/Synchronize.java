import java.util.Arrays;

public class Synchronize {

	private Tuple[] tuples;
	private int index;
	private double bestStart;
	private double bestEnd;

	public Synchronize() {
		tuples = new Tuple[10];
		index = 0;
		bestStart = 0;
		bestEnd = 0;
	}

	public int getIndex() {
		return this.index;
	}

	public Tuple[] getTuples() {
		return tuples;
	}

	public void setTuples(Tuple[] tuples) {
		this.tuples = tuples;
	}

	public void addTuple(Tuple t) {
		tuples[index] = t;
		index++;
	}

	public void printTuples() {
		for (Tuple t : tuples) {
			System.out.println("<" + String.format("%.6f", t.getT1()) + ","
							+ t.getT2() + ">");
		}
	}

	public void findBestStartEnd() {
		int best = 0;
		int current = 0;
		for (int i = 0; i < tuples.length; i++) {
			Tuple t = tuples[i];
			current = current - t.getT2();
			if (current > best) {
				best = current;
				bestStart = t.getT1();
				bestEnd = tuples[i + 1].getT1();
			}

		}
		double newTime = 0;
		// If best == 1 i.e. there is no overlap, I find the midpoints of each
		// range and average them.
		if (best == 1) {
			System.out.println("NO OVERLAP");
			double t1 = (tuples[1].getT1() + tuples[0].getT1()) / 2;
			double t2 = (tuples[3].getT1() + tuples[2].getT1()) / 2;
			double t3 = (tuples[5].getT1() + tuples[4].getT1()) / 2;
			double t4 = (tuples[7].getT1() + tuples[6].getT1()) / 2;
			double t5 = (tuples[9].getT1() + tuples[8].getT1()) / 2;
			newTime = (t1 + t2 + t3 + t4 + t5) / 5.0;
			System.out.println(String.format("%.3f",
							System.currentTimeMillis() / 1000.0));
			System.out.println(String.format("%.3f", newTime));
		} else {
			// System.out.println(String.format("%.6f", bestStart));
			// System.out.println(String.format("%.6f", bestEnd));
			newTime = (bestStart + bestEnd) / 2.0;

			String currTime = new java.text.SimpleDateFormat("HH:mm:ss.SSS")
							.format(new java.util.Date((System.currentTimeMillis())));
			System.out.println("Current Time: " + currTime);
			long x = (long) (newTime * 1000);
			String newTimeStr = new java.text.SimpleDateFormat("HH:mm:ss.SSS")
							.format(new java.util.Date(x));
			System.out.println("Updated Time: " + newTimeStr);
		}
	}

	public void sortTuples() {
		Arrays.sort(tuples);
	}

}
