package threads;

import functions.basic.Log;
import functions.Functions;

public class SimpleGenerator implements Runnable {
    private final Task task;

    public SimpleGenerator(Task task) {
        this.task = task;
    }

    @Override
    public void run() {
        for (int i = 0; i < task.getTasksCount(); i++) {

            double base = 1 + Math.random() * 9;
            double leftX = Math.random() * 100;
            double rightX = 100 + Math.random() * 100;
            double step = Math.random();

            synchronized (task) {
                task.setFunction(new Log(base));
                task.setLeftX(leftX);
                task.setRightX(rightX);
                task.setStep(step);

                System.out.printf("Source %.2f %.2f %.6f%n", leftX, rightX, step);
            }

            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
