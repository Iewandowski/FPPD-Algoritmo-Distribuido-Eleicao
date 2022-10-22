import java.util.ArrayList;
import java.util.List;

public class RingPipe extends Thread{

    private long counterState = 0;
    private List<Process> processList;
    private String status;

    public RingPipe() {
        this.status = "NORMAL";
        setProcessList();
    }

    @Override
    public void run() {
        try {
            
            this.processList.get(0).sendMessageToNextActiveProcess();

            // while (!done) {
            //     System.out.println(this.currentProcessId);
                
            //     TimeUnit.SECONDS.sleep(2);
            // }

        } catch (Exception e) {
            // IGNORE
        }

    }

    public void setProcessList() {
        this.processList = new ArrayList<>();

        this.processList.add(new Process(1, this));
        this.processList.add(new Process(2, this));
        this.processList.add(new Process(3, this));
        this.processList.add(new Process(4, this));

        this.processList.get(0).setNext(this.processList.get(1));
        this.processList.get(1).setNext(this.processList.get(2));
        this.processList.get(2).setNext(this.processList.get(3));
        this.processList.get(3).setNext(this.processList.get(0));
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getCounterState() {
        return counterState;
    }

    public void setCounterState(long counterState) {
        this.counterState = counterState + 1;
    }
}
