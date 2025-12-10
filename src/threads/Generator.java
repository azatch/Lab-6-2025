package threads;

import functions.basic.Log;
import functions.Functions;

public class Generator extends Thread {
    private final Task task;
    private final Semaphore semaphore;

    public Generator(Task task, Semaphore semaphore) {
        this.task = task;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < task.getTasksCount(); i++) {
                if (Thread.interrupted()) {
                    System.out.println("Generator прерван");
                    break;
                }

                double base = 1 + Math.random() * 9;
                double leftX = Math.random() * 100;
                double rightX = 100 + Math.random() * 100;
                double step = Math.random();

                semaphore.acquire();

                task.setFunction(new Log(base));
                task.setLeftX(leftX);
                task.setRightX(rightX);
                task.setStep(step);

                System.out.printf("Source %.2f %.2f %.6f%n", leftX, rightX, step);

                semaphore.release();

                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Generator InterruptedException");
        }
    }
}
