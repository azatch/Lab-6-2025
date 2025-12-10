package threads;

import functions.Functions;

public class Integrator extends Thread {
    private final Task task;
    private final Semaphore semaphore;

    public Integrator(Task task, Semaphore semaphore) {
        this.task = task;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < task.getTasksCount(); i++) {
                if (Thread.interrupted()) {
                    System.out.println("Integrator прерван");
                    break;
                }

                double leftX, rightX, step, result;

                semaphore.acquire();

                if (task.getFunction() == null) {
                    semaphore.release();
                    continue;
                }

                leftX = task.getLeftX();
                rightX = task.getRightX();
                step = task.getStep();
                result = Functions.integrate(task.getFunction(), leftX, rightX, step);

                semaphore.release();

                System.out.printf("Result %.2f %.2f %.6f %.6f%n",
                        leftX, rightX, step, result);

                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Integrator InterruptedException");
        }
    }
}
