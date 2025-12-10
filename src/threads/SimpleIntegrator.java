package threads;

import functions.Functions;

public class SimpleIntegrator implements Runnable {
    private final Task task;

    public SimpleIntegrator(Task task) {
        this.task = task;
    }

    @Override
    public void run() {

        for (int i = 0; i < task.getTasksCount(); i++) {

            double leftX, rightX, step, result;

            synchronized (task) {
                if (task.getFunction() == null) {
                    continue;
                }

                leftX = task.getLeftX();
                rightX = task.getRightX();
                step = task.getStep();
                result = Functions.integrate(task.getFunction(), leftX, rightX, step);
            }

            System.out.printf("Result %.2f %.2f %.6f %.6f%n",
                    leftX, rightX, step, result);

            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
