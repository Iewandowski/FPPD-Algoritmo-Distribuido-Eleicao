import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Manager implements Runnable {
    
    private Socket manager;
    private BufferedReader in;
    private PrintWriter out;
    private boolean done;

    @Override
    public void run() {
        try {
            
            manager = new Socket("127.0.0.1", 9999); //localhost on port 9999
            out = new PrintWriter(manager.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(manager.getInputStream()));

            InputHandler inHandler = new InputHandler();
            Thread t = new Thread(inHandler);

            t.start();

            String inMessage;

            while((inMessage = in.readLine()) != null) {
                System.out.println(inMessage);
            }

        } catch (IOException e) {
            shutdown();
        }
    }

    public void shutdown() {
        done = true;
        try {
            in.close();
            out.close();
            if (!manager.isClosed()) {
                manager.close();
            }
        } catch (IOException e) {
            // IGNORE
        }
    }

    class InputHandler implements Runnable {

        @Override
        public void run() {
            try {
                BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
                while (!done) {
                    String message = inReader.readLine();
                    if (message.equals("/quit")) {
                        out.println(message);
                        inReader.close();
                        shutdown();
                    } else {
                        out.println(message);
                    }
                }
            } catch (IOException e) {
                shutdown();
            }            
        }

    }

    public static void main(String[] args) {
        Manager manager = new Manager();
        manager.run();
    }

}
