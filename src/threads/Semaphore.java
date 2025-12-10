package threads;

public class Semaphore {
    private boolean available = true;

    public synchronized void acquire() throws InterruptedException {
        while (!available) {
            wait();
        }
        available = false;
    }

    public synchronized void release() {
        available = true;
        notifyAll();
    }
}
