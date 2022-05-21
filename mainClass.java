
import java.util.*;

public class mainClass {
    final static mainClass classInstance = new mainClass();
    static int roundsPerformed = 0, threadsCheckedCurrRound = 0, threadsChangedCurrRound = 0;
    static int ids[]; // ids for threads
    static int NUM_OF_THREADS, NUM_OF_ROUNDS;

    public static void main(String[] args) {
        final int MIN_VALUE_FOR_ID = 1, MAX_VALUE_FOR_ID = 100;

        Scanner reader = new Scanner(System.in);

        System.out.println("Please enter number of threads");

         NUM_OF_THREADS = reader.nextInt();

        System.out.println("Please enter number of rounds");

        NUM_OF_ROUNDS = reader.nextInt();

        Thread[] threads = new Thread[NUM_OF_THREADS];

        ids = new int[NUM_OF_THREADS];

        for(int i =0 ; i < NUM_OF_THREADS ; i++) {
            ids[i] = getRandomNumber(MIN_VALUE_FOR_ID, MAX_VALUE_FOR_ID);
            final int currentThreadId = i;
            threads[i] = new Thread( () -> classInstance.threadMethod(currentThreadId));
        }

        printIDs();

        for(Thread t: threads)
            t.start();
    }

    synchronized void threadMethod(int index) {
        if(roundsPerformed >= NUM_OF_ROUNDS)
            return;

        int value = ids[index];

        int leftCell = index - 1, rightCell = index + 1; // set the leftCell and rightCell values to what they are "supposed" to be and then check to verify

        if (rightCell >= ids.length)
            rightCell = 0;

        if (leftCell <= 0)
            leftCell = ids.length - 1;

        int leftVal = ids[leftCell], rightVal = ids[rightCell];

        int change = 0;

        if (value < leftVal && value < rightVal)
            change = 1;

        else if (value > leftVal && value > rightVal)
            change = -1;

        threadsCheckedCurrRound++;

        while (threadsCheckedCurrRound < NUM_OF_THREADS) {
            try {
                notify();
                wait();
            }
            catch(Exception ignored) { }
        }

        /* All the values associated with the threads have been checked. we can now change their values */

        ids[index] += change;
        threadsChangedCurrRound++;

        /* we want to check if all the values have been changed before starting a new round */
        while (threadsChangedCurrRound < NUM_OF_THREADS) {
            try {
                notify();
                wait();
            }
            catch(Exception ignored) { }
        }

        if (threadsChangedCurrRound == NUM_OF_THREADS) { // so that only one thread will increase rounds and print a line
            threadsChangedCurrRound = threadsCheckedCurrRound = 0;
            roundsPerformed++;
            printIDs();
        }
        classInstance.threadMethod(index); // in order to start a new round
    }

    static void printIDs() {
        for(int i : ids )
            System.out.print(i + "\t");

        System.out.println();
    }

    static int getRandomNumber(int min, int max) { // a function for getting a random value between two integers.
        return (int) (Math.floor(Math.random() * (max-min+1)) ) + min;
    }
}
