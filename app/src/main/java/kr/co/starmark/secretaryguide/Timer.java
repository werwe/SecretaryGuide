package kr.co.starmark.secretaryguide;

/**
 * Created by werwe on 2014-10-28.
 */
public class Timer {

    enum ORDER {INCREASE, DECREASE}

    private ORDER mOrder;

    private int mCountIndex = 0;
    private long mStartTime;
    private long mElapsedTime;

    private Thread mTimerThread;
    private boolean mRun = false;

    private int mTimeAmount = 10;
    private Runnable mDecreseCount = new Runnable() {
        @Override
        public void run() {
            mElapsedTime = (System.currentTimeMillis() - mStartTime);
            long count = mTimeAmount * 1000 - mElapsedTime;

            if (count > 0) {
                mCountIndex = (int) (count / 1000);
                if (mTimerCallback != null)
                    mTimerCallback.onTime(mCountIndex);
            } else {
                mRun = false;
                if (mTimerCallback != null) {
                    mTimerCallback.onTimeEnd();
                }
            }
        }
    };

    private Runnable mIncreaseCount = new Runnable() {
        @Override
        public void run() {
            mElapsedTime = (System.currentTimeMillis() - mStartTime);
            long count = mElapsedTime;

            if (count < (mTimeAmount + 1) * 1000) {
                mCountIndex = (int) (count / 1000);
                if (mTimerCallback != null)
                    mTimerCallback.onTime(mCountIndex);
            } else {
                mRun = false;
                if (mTimerCallback != null) {
                    mTimerCallback.onTimeEnd();
                }
            }
        }
    };

    public void stopTimer() {
        mRun = false;
        try {
            if (mTimerThread != null && mTimerThread.isAlive())
                mTimerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static interface TimerCallback {
        public void onTime(int seconds);
        public void onTimeEnd();
    }

    public synchronized void setTimerCallback(TimerCallback callback) {
        this.mTimerCallback = callback;
    }

    private TimerCallback mTimerCallback;

    /**
     * @param order
     */
    public Timer(ORDER order) {
        mOrder = order;
    }

    /**
     * @param timeAmount in second
     */
    public void startTimer(int timeAmount) {
        mRun = true;
        mTimeAmount = timeAmount;
        mStartTime = System.currentTimeMillis();
        mTimerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (mRun) {
                    try {
                        Thread.sleep(50);
                        if(mOrder == ORDER.INCREASE)
                            mIncreaseCount.run();
                        else
                            mDecreseCount.run();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mTimerThread.setDaemon(true);
        mTimerThread.start();
    }
}