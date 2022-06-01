
import java.util.*;

public class mainClass {
    static mainClass classInstance = new mainClass(); // for synchronization stuff, since it works per an object intrinsic key
    static int roundsPerformed = 0, threadsCheckedCurrRound = 0, threadsChangedCurrRound = 0;
    static int ids[]; // array to hold the values associated with the threads
    static int NUM_OF_THREADS, NUM_OF_ROUNDS;

    public static void main(String[] args) {
        final int MIN_VALUE_FOR_ID = 1, MAX_VALUE_FOR_ID = 100; // constants that represent the min and max values that can be associated with a thread.

        /* Get user input regarding number of threads and number of rounds */

        Scanner reader = new Scanner(System.in);

        System.out.println("Please enter number of threads");

         NUM_OF_THREADS = reader.nextInt();

        System.out.println("Please enter number of rounds");

        NUM_OF_ROUNDS = reader.nextInt();
        /* */

        Thread[] threads = new Thread[NUM_OF_THREADS]; // create an array of threads

        ids = new int[NUM_OF_THREADS]; // create an array of values associated with threads

        /* Assign each thread an associated value, and create the threads */
        for(int i =0 ; i < NUM_OF_THREADS ; i++) {
            ids[i] = getRandomNumber(MIN_VALUE_FOR_ID, MAX_VALUE_FOR_ID);
            final int currentThreadId = i;
            threads[i] = new Thread( () -> classInstance.threadMethod(currentThreadId));
        }

        printIDs(); // print the values associated with the threads before starting round number 1

        for(Thread t: threads)
            t.start(); // start threads
    }

    public synchronized void threadMethod(int index) {
        if(roundsPerformed >= NUM_OF_ROUNDS) // if our job is done, return
            return;

        int value = ids[index]; // get the current thread's associated value

        int leftCell = index - 1, rightCell = index + 1; // set the leftCell and rightCell values to what they are "supposed" to be and then check to verify

        /* Make the adjustments needed in specific cases where the value associated with the current thread is in the
        * beginning/end of the values associated with threads array  */
        if (rightCell >= ids.length)
            rightCell = 0;

        if (leftCell <= 0)
            leftCell = ids.length - 1;
        /* */

        int leftVal = ids[leftCell], rightVal = ids[rightCell]; // get the values associated with the current thread's "neighbors"

        int change = 0;

        /* Set the change needed according to the instructions */
        if (value < leftVal && value < rightVal)
            change = 1;

        else if (value > leftVal && value > rightVal)
            change = -1;
        /* */

        threadsCheckedCurrRound++; // increase the number of threads that have been checked this current round

        /* the value associated with the current thread has been checked, now we want to make sure all the values associated
        with all the threads have been checked before proceeding
         */
        while (threadsCheckedCurrRound < NUM_OF_THREADS) {
            try {
                notifyAll();
                wait();
            }
            catch(Exception e) {
                System.out.println(e);
            }
        }

        /* All the values associated with the threads have been checked. we can now change their values */

        ids[index] += change; // change the value associated with the current. add the change needed (could be negative)
        threadsChangedCurrRound++; // increase the numbers of threads that their associated value has been changed

        /* we want to check if all the values have been changed before starting a new round */

        while (threadsChangedCurrRound < NUM_OF_THREADS) {
            try {
                notifyAll();
                wait();
            }
            catch(Exception e) {
                System.out.println(e);
            }
        }

        /* All the values have changed accordingly, we may start a new round */

        if (threadsChangedCurrRound == NUM_OF_THREADS) { // so that only one thread will increase rounds and print the values associated with the threads
            /* Reset threads checked current round and threads changed current round */
            threadsCheckedCurrRound = 0;
            threadsChangedCurrRound = 0;

            roundsPerformed++; // increase numbers of rounds performed
            printIDs(); // print the values associated with the threads
        }
        classInstance.threadMethod(index); // in order to start a new round. this is basically like restarting each thread (only that we create a new one instead)
    }

    public static void printIDs() { // print the values associated with each thread
        for(int i : ids )
            System.out.print(i + "\t");

        System.out.println();
    }

    public static int getRandomNumber(int min, int max) { // a function for getting a random value between two integers.
        return (int) (Math.floor(Math.random() * (max-min+1)) ) + min;
    }
}
