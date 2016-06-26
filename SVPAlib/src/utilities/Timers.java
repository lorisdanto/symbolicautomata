package utilities;

import org.sat4j.specs.TimeoutException;

public class Timers {
			
	private Timers() {
	}

	private static long timeout = Long.MAX_VALUE;
	private static long[] startTime = new long[3];
	private static long[] total = new long[3];
	private static boolean[] isRunning = new boolean[3];
	public static int exploredStates;
	public static int successfulSubs;
	
	
	public static void setNumberOfTimers(int n) {
		startTime = new long[n];
		total = new long[n];
		isRunning = new boolean[n];
		for (int i = 0; i < n; i++) {
			total[i] = 0;
			isRunning[i] = false;
		}
		exploredStates=0;
		successfulSubs=0;
	}
	
	public static void oneMoreState(){
		exploredStates++;
	}
	
	public static void oneMoreSub(){
		successfulSubs++;
	}
	
	public static void setTimeout(long to){
		timeout = to;
	}

	public static void resetAll() {
		setNumberOfTimers(startTime.length);
	}

	public static void reset(int i) {
		total[i] = 0;
		isRunning[i] = false;
	}

	public static void start(int i) {
//		if (isRunning[i])
//			throw new IllegalArgumentException("Timer was still running");

		startTime[i] = System.currentTimeMillis();
		isRunning[i] = true;
	}

	public static void stop(int i) {
//		if (!isRunning[i])
//			throw new IllegalArgumentException("Timer was not running");
		
		total[i] += System.currentTimeMillis() - startTime[i];
		isRunning[i] = false;
	}
	
	public static long getValue(int i) {
//		if (isRunning[i])
//			throw new IllegalArgumentException("Timer was still running");
		
		return total[i];
	}
	
	//For congruence
	private static final int full = 0;
	private static final int solver = 1;
	private static final int subsumption = 2;
	
	public static void setForCongruence(){
		setNumberOfTimers(3);
	}
	
	public static void startSolver() {
		start(solver);
	}
	
	public static void stopSolver() {
		stop(solver);
	}
	
	public static long getSolver() {
		return getValue(solver);
	}

	public static void startFull() {
		start(full);
	}
	
	public static void stopFull() {
		stop(full);
	}
	
	public static long getFull() {	
		return getValue(full);
	}
	
	public static void startSubsumption() {
		start(subsumption);
	}
	
	public static void stopSubsumption() {
		stop(subsumption);
	}
	
	public static long getSubsumption() {
		return getValue(subsumption);
	}
	
	public static boolean fullTO(){
		long tmp = total[full];
		if(isRunning[full])
			tmp += System.currentTimeMillis() - startTime[full];
		return tmp>timeout;
	}

	public static boolean fullTO(long timeout){
		long tmp = total[full];
		if(isRunning[full])
			tmp += System.currentTimeMillis() - startTime[full];
		return tmp>timeout;
	}
	
	public static void assertFullTO(long to) throws TimeoutException{
		if(fullTO(to))
			throw new TimeoutException("Timeout");
	}
	
	public static void assertFullTO() throws TimeoutException{
		if(fullTO())
			throw new TimeoutException("Timeout");
	}
	
}
