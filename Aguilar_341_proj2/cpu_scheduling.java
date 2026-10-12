import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class cpu_scheduling {
    public static void main(String [] args) {
        // take input file
        Scanner scan = new Scanner(System.in);
        System.out.print("Please type the input text file name: ");
        String fileName = scan.nextLine();
        File file = new File(fileName);
        List<List<Integer>> matrix = new ArrayList<>();
        try (Scanner scan2 = new Scanner(file)) {
            while (scan2.hasNextLine()) {
                String line = scan2.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] numberStrings = line.split("\\s+");
                ArrayList<Integer> integers = new ArrayList<>();
                for (String numStr : numberStrings) {
                    integers.add(Integer.parseInt(numStr));
                }
                matrix.add(new ArrayList<>(integers));
            }
            scan2.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error: The file was not found.");
            return;
        }

        System.out.println("Scheduling Menu...\n" +
                            "   1. FCFS\n" +
                            "   2. SJF\n" +
                            "   3. nonpreemptive priority (a larger priority number implies a higher priority)\n" +
                            "   4. RR (quantum = 2)\n" +
                            "   5. Exit");
        System.out.print("Please choose an option (1-5): ");
        int choice = scan.nextInt();
        switch (choice) {
            case 1: 
                FirstComeFirstServe schedule1 = new FirstComeFirstServe(matrix);
                schedule1.display();
                break;
            case 2:
                ShortestJobFirst schedule2 = new ShortestJobFirst(matrix);
                schedule2.display();
                break;
            case 3: 
                NonpremptivePriority schedule3 = new NonpremptivePriority(matrix);
                schedule3.display();
                break;
            case 4:
                RoundRobin schedule4 = new RoundRobin(matrix);
                schedule4.display();
                break;
            case 5:
                break;
            default:
                throw new AssertionError();
        }
        scan.close();
    }
}

// scheduling classes / algorithms
// FCFS (completed)
class FirstComeFirstServe {
    List<List<Integer>> matrix = new ArrayList<>();
    List<List<Integer>> scheduledMatrix = new ArrayList<>();

    // constructor
    public FirstComeFirstServe(List<List<Integer>> matrix) {
        this.matrix = matrix;
        this.scheduledMatrix.addAll(matrix);
        firstComeFirstServe();
    }

    // scheduling algorithm
    public List<List<Integer>> firstComeFirstServe() {
        scheduledMatrix = matrix;
        return this.scheduledMatrix;
    }

    // average turnaround time (turnaround time = comppletion time - arrival time)
    public double avgTurnaroundTime() {
        double avgTurnaroundTime = 0;
        int turnaroundTime = 0;
        int sum = 0;

        // all processes arrive at same time
        for (int i = 0; i < scheduledMatrix.size(); i++) {
            turnaroundTime = turnaroundTime + scheduledMatrix.get(i).get(1);
            int temp = turnaroundTime;
            sum += temp; 
        }
        avgTurnaroundTime = ((double)sum / scheduledMatrix.size());

        return avgTurnaroundTime;
    }

    // average waiting time (waiting time = turnaround time - burst time)
    public double avgWaitingTime() {
        double avgWaitingTime = 0;
        int waitingTime = 0;
        int turnaroundTime = 0;
        int sum = 0;

        // all processes arrive at same time
        for (int i = 0; i < scheduledMatrix.size(); i++) {
            turnaroundTime = turnaroundTime + scheduledMatrix.get(i).get(1);
            waitingTime = turnaroundTime - scheduledMatrix.get(i).get(1);
            int temp = waitingTime;
            sum += temp; 
        }
        avgWaitingTime = ((double)sum / scheduledMatrix.size());

        return avgWaitingTime;
    }    

    // display scheduling
    public void display() {
        System.out.println("FCFS Scheduling");
        for (int i = 0; i < scheduledMatrix.size(); i++) {
            System.out.println("Round " + (i+1) + "\n" +
                                "Pid: " + scheduledMatrix.get(i).get(0) + 
                                ", Burst: " + scheduledMatrix.get(i).get(1) + 
                                ", Priority: " + scheduledMatrix.get(i).get(2));
        }
        System.out.println("The average waiting time is: " + avgWaitingTime() + " ms.");
        System.out.println("The average turnaround time is: " + avgTurnaroundTime() + " ms.");
    }
}

//SJF (one issue - repeated burst time)
class ShortestJobFirst {
    List<List<Integer>> matrix = new ArrayList<>();
    List<List<Integer>> scheduledMatrix = new ArrayList<>();


    public ShortestJobFirst(List<List<Integer>> matrix) {
        this.matrix = matrix;
        shortestJobFirst();
    }

    // scheduling algorithm
    public List<List<Integer>> shortestJobFirst() {
        scheduledMatrix.clear();

        // compare burst times and order new matrix based off of this
        int[] orderedIndex = new int[matrix.size()];
        for (int i = 0; i < matrix.size(); i++) {
            orderedIndex[i] = matrix.get(i).get(1);
        }
        Arrays.sort(orderedIndex);

        for (int i = 0; i < orderedIndex.length; i++) {
            for (int j = 0; j < matrix.size(); j++)
            if (matrix.get(j).get(1).equals(orderedIndex[i])) {
                if (scheduledMatrix.contains(matrix.get(j))) {
                    break;
                }
                scheduledMatrix.add(matrix.get(j));
            }
        }
        return this.scheduledMatrix;
    }

    // average turnaround time (turnaround time = comppletion time - arrival time)
    public double avgTurnaroundTime() {
        double avgTurnaroundTime = 0;
        int turnaroundTime = 0;
        int sum = 0;

        // all processes arrive at same time
        for (int i = 0; i < scheduledMatrix.size(); i++) {
            turnaroundTime = turnaroundTime + scheduledMatrix.get(i).get(1);
            int temp = turnaroundTime;
            sum += temp; 
        }
        avgTurnaroundTime = ((double)sum / scheduledMatrix.size());

        return avgTurnaroundTime;
    }

    // average waiting time (waiting time = turnaround time - burst time)
    public double avgWaitingTime() {
        double avgWaitingTime = 0;
        int waitingTime = 0;
        int turnaroundTime = 0;
        int sum = 0;

        // all processes arrive at same time
        for (int i = 0; i < scheduledMatrix.size(); i++) {
            turnaroundTime = turnaroundTime + scheduledMatrix.get(i).get(1);
            waitingTime = turnaroundTime - scheduledMatrix.get(i).get(1);
            int temp = waitingTime;
            sum += temp; 
        }
        avgWaitingTime = ((double)sum / scheduledMatrix.size());

        return avgWaitingTime;
    }    

    // display scheduling
    public void display() {
        System.out.println("SJF Scheduling");
        for (int i = 0; i < scheduledMatrix.size(); i++) {
            System.out.println("Round " + (i+1) + "\n" +
                                "Pid: " + scheduledMatrix.get(i).get(0) + 
                                ", Burst: " + scheduledMatrix.get(i).get(1) + 
                                ", Priority: " + scheduledMatrix.get(i).get(2));
        }
        System.out.println("The average waiting time is: " + avgWaitingTime() + " ms.");
        System.out.println("The average turnaround time is: " + avgTurnaroundTime() + " ms.");
    }
}

// (one issue - repeated priority times)
// nonpreemptive priority (a larger priority number implies a higher priority)
class NonpremptivePriority {
    List<List<Integer>> matrix = new ArrayList<>();
    List<List<Integer>> scheduledMatrix = new ArrayList<>();


    // constructor
    public NonpremptivePriority(List<List<Integer>> matrix) {
        this.matrix = matrix;
        nonpreemptivePriority();
    }

    // scheduling algorithm
    public List<List<Integer>> nonpreemptivePriority() {
        scheduledMatrix.clear();

        // compare priority and order new matrix based off of this
        Integer[] orderedIndex = new Integer[matrix.size()];
        for (int i = 0; i < matrix.size(); i++) {
            orderedIndex[i] = matrix.get(i).get(2);
        }
        Arrays.sort(orderedIndex, Collections.reverseOrder());

        for (int i = 0; i < orderedIndex.length; i++) {
            for (int j = 0; j < matrix.size(); j++)
            if (matrix.get(j).get(2).equals(orderedIndex[i])) {
                if (scheduledMatrix.contains(matrix.get(j))) {
                    break;
                }
                scheduledMatrix.add(matrix.get(j));
            }
        }
        return this.scheduledMatrix;
    }

    // average turnaround time (turnaround time = comppletion time - arrival time)
    public double avgTurnaroundTime() {
        double avgTurnaroundTime = 0;
        int turnaroundTime = 0;
        int sum = 0;

        // all processes arrive at same time
        for (int i = 0; i < scheduledMatrix.size(); i++) {
            turnaroundTime = turnaroundTime + scheduledMatrix.get(i).get(1);
            int temp = turnaroundTime;
            sum += temp; 
        }
        avgTurnaroundTime = ((double)sum / scheduledMatrix.size());

        return avgTurnaroundTime;
    }

    // average waiting time (waiting time = turnaround time - burst time)
    public double avgWaitingTime() {
        double avgWaitingTime = 0;
        int waitingTime = 0;
        int turnaroundTime = 0;
        int sum = 0;

        // all processes arrive at same time
        for (int i = 0; i < scheduledMatrix.size(); i++) {
            turnaroundTime = turnaroundTime + scheduledMatrix.get(i).get(1);
            waitingTime = turnaroundTime - scheduledMatrix.get(i).get(1);
            int temp = waitingTime;
            sum += temp; 
        }
        avgWaitingTime = ((double)sum / scheduledMatrix.size());

        return avgWaitingTime;
    }    

    // display scheduling
    public void display() {
        System.out.println("Nonpreemptive priority Scheduling");
        for (int i = 0; i < scheduledMatrix.size(); i++) {
            System.out.println("Round " + (i+1) + "\n" +
                                "Pid: " + scheduledMatrix.get(i).get(0) + 
                                ", Burst: " + scheduledMatrix.get(i).get(1) + 
                                ", Priority: " + scheduledMatrix.get(i).get(2));
        }
        System.out.println("The average waiting time is: " + avgWaitingTime() + " ms.");
        System.out.println("The average turnaround time is: " + avgTurnaroundTime() + " ms.");
    }
}

// RR (quantum = 2) (completed)
class RoundRobin {
    private static final int QUANTUM = 2;
    List<List<Integer>> matrix = new ArrayList<>();
    List<List<Integer>> scheduledMatrix = new ArrayList<>();
    List<Integer> completionTimes = new ArrayList<>();

    public RoundRobin(List<List<Integer>> matrix) {
        this.matrix = matrix;
        for (List<Integer> row : matrix) {
            this.scheduledMatrix.add(new ArrayList<>(row));
        }
        roundRobin();
    }

    // scheduling algorithm
    public List<List<Integer>> roundRobin() {

        for (int i = 0; i < scheduledMatrix.size(); i++) {
            scheduledMatrix.get(i).add(-1);
            scheduledMatrix.get(i).add(scheduledMatrix.get(i).get(1));
        }
        
        for (int i = 0; i < scheduledMatrix.size(); i++) {
            if (scheduledMatrix.get(i).get(4) != 0) {
                int timeRan = 0;
                int remainingTime = 0;
                int burst = scheduledMatrix.get(i).get(4);
                if (burst > QUANTUM) {
                    timeRan = QUANTUM;
                    remainingTime = burst - QUANTUM;
                    scheduledMatrix.get(i).set(3, timeRan);
                    scheduledMatrix.get(i).set(4, remainingTime);
                    if (remainingTime > 0) {
                        List<Integer> copyOfRow = new ArrayList<>(scheduledMatrix.get(i));
                        copyOfRow.set(3, -1); // It has not run in its next round yet.
                        scheduledMatrix.add(copyOfRow);
                    }
                }
                else if (burst <= QUANTUM && burst > 0){
                    timeRan = burst;
                    remainingTime = 0;
                    scheduledMatrix.get(i).set(3, timeRan);
                    scheduledMatrix.get(i).set(4, remainingTime);
                    if (remainingTime > 0) {
                        List<Integer> copyOfRow = new ArrayList<>(scheduledMatrix.get(i));
                        copyOfRow.set(3, -1); // It has not run in its next round yet.
                        scheduledMatrix.add(copyOfRow);
                    }
                }
            }
        }
        
        return this.scheduledMatrix;
    }

    // average turnaround time (turnaround time = completion time - arrival time)
    public double avgTurnaroundTime() {
        double avgTurnaroundTime = 0;
        int turnaroundTime = 0;
        int sum = 0;

        completionTimes.clear();

        for (int i = 0; i < matrix.size(); i++) {
            int completionTime = 0;
            int pid = matrix.get(i).get(0);
            for (int j = scheduledMatrix.size() - 1; j >= 0; j--) {
                int current = scheduledMatrix.get(j).get(0);
                if (current == pid) {
                    completionTime = 0;
                    for (int k = 0; k <= j; k++) {
                        completionTime += scheduledMatrix.get(k).get(3);
                    }
                    break;
                }
            }
            completionTimes.add(completionTime);
        }

        // all processes arrive at same time (zero)
        int arrivalTime = 0;
        for (int i = 0; i < completionTimes.size(); i++) {
            turnaroundTime = completionTimes.get(i) - arrivalTime;
            int temp = turnaroundTime;
            sum += temp; 
        }
        avgTurnaroundTime = ((double)sum / completionTimes.size());

        return avgTurnaroundTime;
    }

    // average waiting time (waiting time = turnaround time - burst time)
    public double avgWaitingTime() {
        double avgWaitingTime = 0;
        int waitingTime = 0;
        int turnaroundTime = 0;
        int sum = 0;

        completionTimes.clear();

        for (int i = 0; i < matrix.size(); i++) {
            int completionTime = 0;
            int pid = matrix.get(i).get(0);
            for (int j = scheduledMatrix.size() - 1; j >= 0; j--) {
                int current = scheduledMatrix.get(j).get(0);
                if (current == pid) {
                    completionTime = 0;
                    for (int k = 0; k <= j; k++) {
                        completionTime += scheduledMatrix.get(k).get(3);
                    }
                    break;
                }
            }
            completionTimes.add(completionTime);
        }

        // all processes arrive at same time
        int arrivalTime = 0;
        for (int i = 0; i < completionTimes.size(); i++) {
            turnaroundTime = completionTimes.get(i) - arrivalTime;
            waitingTime = turnaroundTime - matrix.get(i).get(1);
            int temp = waitingTime;
            sum += temp; 
        }
        avgWaitingTime = ((double)sum / completionTimes.size());

        return avgWaitingTime;
    }    

    // display scheduling
    public void display() {
        System.out.println("RR (quantum = 2) Scheduling");
        for (int i = 0; i < scheduledMatrix.size(); i++) {
            System.out.println("Round " + (i+1) + "\n" +
                                "Pid: " + scheduledMatrix.get(i).get(0) + 
                                ", Burst: " + scheduledMatrix.get(i).get(1) + 
                                ", Priority: " + scheduledMatrix.get(i).get(2) + 
                                " (Ran for " + scheduledMatrix.get(i).get(3) +
                                "ms, " + scheduledMatrix.get(i).get(4) +
                                "ms remaining)");
        }
        System.out.println("The average waiting time is: " + avgWaitingTime() + " ms.");
        System.out.println("The average turnaround time is: " + avgTurnaroundTime() + " ms.");
    }
}