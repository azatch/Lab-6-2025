import functions.*;
import functions.basic.Exp;
import functions.basic.Log;
import threads.*;


public class Main {


    private static void testIntegrate() {
        Function exp = new Exp();
        double theoreticalValue = Math.exp(1) - 1;

        System.out.println("Теоретическое значение интеграла e^x на [0, 1]: " + theoreticalValue);
        System.out.println();

        for (double step = 0.1; step >= 0.0001; step /= 10) {
            double result = Functions.integrate(exp, 0, 1, step);
            double error = Math.abs(result - theoreticalValue);
            System.out.printf("Шаг: %.4f, Результат: %.10f, Ошибка: %.2e%n",
                    step, result, error);
        }
    }


    private static void nonThread() {
        Task task = new Task();
        task.setTasksCount(100);

        for (int i = 0; i < task.getTasksCount(); i++) {
            double base = 1 + Math.random() * 9;
            double leftX = Math.random() * 100;
            double rightX = 100 + Math.random() * 100;
            double step = Math.random();

            task.setFunction(new Log(base));
            task.setLeftX(leftX);
            task.setRightX(rightX);
            task.setStep(step);

            System.out.printf("Source %.2f %.2f %.6f%n", leftX, rightX, step);

            double result = Functions.integrate(
                    task.getFunction(),
                    task.getLeftX(),
                    task.getRightX(),
                    task.getStep()
            );

            System.out.printf("Result %.2f %.2f %.6f %.6f%n",
                    task.getLeftX(), task.getRightX(), task.getStep(), result);
        }
    }


    private static void simpleThreads() {
        Task task = new Task();
        task.setTasksCount(100);

        Thread generator = new Thread(new SimpleGenerator(task), "Generator");
        Thread integrator = new Thread(new SimpleIntegrator(task), "Integrator");


        generator.start();
        integrator.start();

        try {
            generator.join();
            integrator.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Оба потока завершили работу");
    }

    private static void complicatedThreads() {
        Task task = new Task();
        task.setTasksCount(100);

        Semaphore semaphore = new Semaphore();

        Generator generator = new Generator(task, semaphore);
        Integrator integrator = new Integrator(task, semaphore);

        generator.setPriority(Thread.MIN_PRIORITY);
        integrator.setPriority(Thread.MAX_PRIORITY);

        generator.start();
        integrator.start();

        try {
            Thread.sleep(50);
            generator.interrupt();
            integrator.interrupt();

            generator.join();
            integrator.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Оба потока завершили работу (прерваны)");
    }

    public static void main(String[] args) {
        System.out.println("Задание 1 интегрирование e^x");
        testIntegrate();

        System.out.println("Задание 2 последовательная версия");
        nonThread();

        System.out.println("\nЗадание 3 многопоточная версия");
        simpleThreads();

        System.out.println("\nЗадание 4: с семафором");
        complicatedThreads();
    }
}
