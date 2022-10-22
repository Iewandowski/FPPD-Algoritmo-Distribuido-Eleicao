import java.util.concurrent.TimeUnit;

public class Process {

    private boolean isActive;
    private Process next;
    private int id;
    private RingPipe ring;
    
    public Process(int id, RingPipe ring) {
        this.id = id;
        this.isActive = true;
        this.next = null;
        this.ring = ring;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public Process getNext() {
        return next;
    }

    public void setNext(Process next) {
        this.next = next;
    }

    public int getId() {
        return this.id;
    }

    public void sendMessageToNextActiveProcess() {
        try {

            String actualStatus = this.ring.getStatus();
            
            System.out.println("Process: " + getId() + " [Status: " + actualStatus + "]");
    
            TimeUnit.SECONDS.sleep(2);

            getNext().sendMessageToNextActiveProcess();

        } catch (Exception e) {
            // TODO: handle exception
        }

    }

}
