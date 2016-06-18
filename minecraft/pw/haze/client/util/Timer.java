package pw.haze.client.util;

public class Timer {

    private final long startTime;
    private long resetMS = -1L;

    public Timer() {
        resetMS = now();
        startTime = resetMS;
    }

    public long now() {
        return (long) (System.nanoTime() / 1E6);
    }

    public boolean hasDelayRun(long delay) {
        if (now() >= resetMS + delay)
            return true;
        return false;
    }

    public void addReset(long addedReset) {
        resetMS += addedReset;
    }

    public boolean isComplete(int milliseconds) {
        long d = getElapsedTime();
        return d > milliseconds;
    }

    public short convertToMS(float perSecond) {
        return (short) (int) (1000.0F / perSecond);
    }

    public void reset() {
        resetMS = now();
    }

    public long getElapsedTime() {
        return now() - resetMS;
    }

}
