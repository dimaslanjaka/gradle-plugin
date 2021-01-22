import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class TimeSync {

	private static DatagramPacket s1;
	private static DatagramPacket r1;
	private static DatagramPacket s2;
	private static DatagramPacket r2;
	private static DatagramPacket s3;
	private static DatagramPacket r3;
	private static DatagramPacket s4;
	private static DatagramPacket r4;
	private static DatagramPacket s5;
	private static DatagramPacket r5;
	private static Synchronize synchronizer;

	private static String ip1 = "";
	private static InetAddress inet1;
	private static int port1 = 5000;
	private static DatagramSocket socket1;

	private static String ip2 = "";
	private static InetAddress inet2;
	private static int port2 = 5000;
	private static DatagramSocket socket2;

	private static String ip3 = "";
	private static InetAddress inet3;
	private static int port3 = 5000;
	private static DatagramSocket socket3;

	private static String ip4 = "";
	private static InetAddress inet4;
	private static int port4 = 5000;
	private static DatagramSocket socket4;

	private static String ip5 = "";
	private static InetAddress inet5;
	private static int port5 = 12291;
	private static DatagramSocket socket5;

	public static void main(String[] args) {
		synchronizer = new Synchronize();
		connectSockets();
		fullTimeRequest();
		Scanner scan = new Scanner(System.in);
		String readString = scan.nextLine();
		while (!readString.equals("q")) {
			synchronizer = new Synchronize();
			fullTimeRequest();
			readString = scan.nextLine();
		}
		scan.close();
	}

	public static void connectSockets() {
		try {
			socket1 = new DatagramSocket();
			socket2 = new DatagramSocket();
			socket3 = new DatagramSocket();
			socket4 = new DatagramSocket();
			socket5 = new DatagramSocket();

			inet1 = InetAddress.getByName(ip1);
			inet2 = InetAddress.getByName(ip2);
			inet3 = InetAddress.getByName(ip3);
			inet4 = InetAddress.getByName(ip4);
			inet5 = InetAddress.getByName(ip5);

			byte[] a = "ping".getBytes();
			byte[] b = new byte[17];
			s1 = new DatagramPacket(a, a.length);
			r1 = new DatagramPacket(b, 17);

			byte[] c = "ping".getBytes();
			byte[] d = new byte[17];
			s2 = new DatagramPacket(c, c.length);
			r2 = new DatagramPacket(d, 17);

			byte[] e = "ping".getBytes();
			byte[] f = new byte[17];
			s3 = new DatagramPacket(e, e.length);
			r3 = new DatagramPacket(f, 17);

			byte[] g = "ping".getBytes();
			byte[] h = new byte[17];
			s4 = new DatagramPacket(g, g.length);
			r4 = new DatagramPacket(h, 17);

			byte[] i = "ping".getBytes();
			byte[] j = new byte[17];
			s5 = new DatagramPacket(i, i.length);
			r5 = new DatagramPacket(j, 17);

			socket1.connect(inet1, port1);
			socket2.connect(inet2, port2);
			socket3.connect(inet3, port3);
			socket4.connect(inet4, port4);
			socket5.connect(inet5, port5);

		} catch (Exception e) {
			System.out.println();
		}

	}

	public static void fullTimeRequest() {
		Runnable run1 = new Runnable() {
			public void run() {
				try {
					timeRequest(1);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		Runnable run2 = new Runnable() {
			public void run() {
				try {
					timeRequest(2);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		Runnable run3 = new Runnable() {
			public void run() {
				try {
					timeRequest(3);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		Runnable run4 = new Runnable() {
			public void run() {
				try {
					timeRequest(4);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		Runnable run5 = new Runnable() {
			public void run() {
				try {
					timeRequest(5);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(run1).start();
		new Thread(run2).start();
		new Thread(run3).start();
		new Thread(run4).start();
		new Thread(run5).start();
	}

	public static void timeRequest(int currentServer) {

		String receivedTime = "";
		double timeA = System.nanoTime() / 1000000000.0;
		try {
			switch (currentServer) {
				case 1:
					socket1.send(s1);
					socket1.receive(r1);
					receivedTime = new String(r1.getData(), 0, r1.getLength());
					break;
				case 2:
					socket2.send(s2);
					socket2.receive(r2);
					receivedTime = new String(r2.getData(), 0, r2.getLength());
					break;
				case 3:
					socket3.send(s3);
					socket3.receive(r3);
					receivedTime = new String(r3.getData(), 0, r3.getLength());
					break;
				case 4:
					socket4.send(s4);
					socket4.receive(r4);
					receivedTime = new String(r4.getData(), 0, r4.getLength());
					break;
				case 5:
					socket5.send(s5);
					socket5.receive(r5);
					receivedTime = new String(r5.getData(), 0, r5.getLength());
					break;
			}

			long rTime = (long) (Double.parseDouble(receivedTime) * 1000);
			String newTimeStr = new java.text.SimpleDateFormat("HH:mm:ss.SSS")
							.format(new java.util.Date(rTime));
			System.out.println("Server " + currentServer + " serverTime: "
							+ newTimeStr);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double timeB = System.nanoTime() / 1000000000.0;
		double rtt = timeB - timeA;
		System.out.printf("Server " + currentServer + " rtt: %.6f\n", timeB
						- timeA);
		double newTime = Double.parseDouble(receivedTime);
		synchronizer.addTuple(new Tuple(newTime, -1));
		synchronizer.addTuple(new Tuple(newTime + rtt, +1));
		if (synchronizer.getIndex() == 10) {
			synchronizer.sortTuples();
			// synchronizer.printTuples();
			synchronizer.findBestStartEnd();
		}

	}

}
