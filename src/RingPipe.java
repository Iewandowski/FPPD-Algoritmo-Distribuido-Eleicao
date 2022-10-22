import java.util.concurrent.TimeUnit;

public class RingPipe extends Thread{

    private long counterState = 0;
    private boolean done;

    public RingPipe() {
        this.done = false;
    }

    public long getCounterState() {
        return counterState;
    }

    public void setCounterState(long counterState) {
        this.counterState = counterState + 1;
    }

    @Override
    public void run() {
        try {
            
            while (!done) {
                System.out.println(getCounterState());
                
                setCounterState(this.counterState);
                
                TimeUnit.SECONDS.sleep(2);
            }

        } catch (Exception e) {
            shutdown();
        }

    }

    public void shutdown() {
        this.done = true;
    }
}
