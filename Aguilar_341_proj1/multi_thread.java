import java.util.Arrays;
import java.util.concurrent.*;

public class multi_thread {
    public static void main(String[] args) {
        // take the 
        if (args.length == 0) {
            System.out.println("Error: missing arguments.");
            return;
        }

        int[] numbers = new int[args.length];

        for (int i = 0; i < args.length; i++) {
            numbers[i] = Integer.parseInt(args[i]);
        }

        Arrays.sort(numbers);

        ExecutorService pool = Executors.newFixedThreadPool(4);
        Future<Integer> avgResult = pool.submit(new Average(numbers));
        Future<Integer> minResult = pool.submit(new Minimum(numbers));
        Future<Integer> maxResult = pool.submit(new Maximum(numbers));
        Future<Integer> medResult = pool.submit(new Medium(numbers));

        try {
            System.out.println("The average value is " + avgResult.get());
            System.out.println("The minimum value is " + minResult.get());
            System.out.println("The maximum value is " + maxResult.get());
            System.out.println("The medium value is " + medResult.get());
        } catch (InterruptedException | ExecutionException ie) { 
            ie.printStackTrace();
        } finally {
            pool.shutdown();
        }
    }
}

class Average implements Callable<Integer> {
    private int[] numbers; 

    public Average(int[] numbers) {
        this.numbers = numbers;
    }

    /* the thread will execute in this method */
    @Override 
    public Integer call() {
        int sum = 0;
        for (int i = 0; i < numbers.length; i++) {
            sum += numbers[i];
        }
        return (int)Math.floor((double)sum / numbers.length);
    }
}

class Minimum implements Callable<Integer> {
    private int[] numbers; 

    public Minimum(int[] numbers) {
        this.numbers = numbers;
    }

    /* the thread will execute in this method */
    @Override 
    public Integer call() {
        return numbers[0];
    }
}

class Maximum implements Callable<Integer> {
    private int[] numbers; 

    public Maximum(int[] numbers) {
        this.numbers = numbers;
    }

    /* the thread will execute in this method */
    @Override 
    public Integer call() {
        return numbers[numbers.length - 1];
    }
}

class Medium implements Callable<Integer> {
    private int[] numbers; 

    public Medium(int[] numbers) {
        this.numbers = numbers;
    }

    /* the thread will execute in this method */
    @Override 
    public Integer call() {
        int index = 0;
        if (numbers.length % 2 == 0) {
            index = numbers.length / 2;
            int index2 = index - 1;
            int medium = (numbers[index] + numbers[index2]) / 2;
            return medium;
        }
        else {
            index = numbers.length / 2;
            return numbers[index];
        }
    }
}