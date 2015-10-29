import java.util.Random;
import java.util.concurrent.Semaphore;

/**
 * Created by kylel on 10/27/2015.
 */
public class SleepingBarber extends Thread {
    private int customerNumber = 1;     // simple number to display nth customer
    Random random = new Random();
    private Semaphore barberAvailable = new Semaphore(0);   // is the barber available for a haircut?
    private Semaphore customersWaiting = new Semaphore(0);  // are there customers waiting?
    private Semaphore seatMutex = new Semaphore(1);         // can the number of seats be modified?
    private int seatsAvailable;

    public SleepingBarber(int N) {
        seatsAvailable = N;
        System.out.println("There are " + seatsAvailable + " seats available.");
        Barber barber = new Barber();
        barber.start();

        // create 100 customers for testing
        for (int i = 0; i < 100; i++) {
            try {
                int numberOfArrivals = random.nextInt(5);
                switch (numberOfArrivals) {
                    case 0:
                        break;
                    case 1:
                        new Customer().run();
                        customerNumber++;
                        break;
                    case 2:
                        new Customer().run();
                        customerNumber++;
                        new Customer().run();
                        customerNumber++;
                        break;
                    case 3:
                        new Customer().run();
                        customerNumber++;
                        new Customer().run();
                        customerNumber++;
                        new Customer().run();
                        customerNumber++;
                        break;
                    case 4:
                        new Customer().run();
                        customerNumber++;
                        new Customer().run();
                        customerNumber++;
                        new Customer().run();
                        customerNumber++;
                        new Customer().run();
                        customerNumber++;
                        break;

                }
                sleep(10);
            } catch (InterruptedException e) {

            }
        }
    }

    private class Barber extends Thread {

        public Barber () {
            barberAvailable.release();
        }

        public void run() {
            while (true) {
                try {
                    customersWaiting.acquire();
                    seatMutex.acquire();
                    seatsAvailable++;
                    barberAvailable.release();
                    seatMutex.release();
                    // barber cuts hair
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class Customer extends Thread {
        public void run() {
            try {
                seatMutex.acquire();
                if (seatsAvailable > 0) {
                    seatsAvailable--;
                    customersWaiting.release();
                    seatMutex.release();
                    barberAvailable.acquire();
                    // customer gets haircut
                    System.out.println("customer " + customerNumber + "'s hair was cut");
                } else {
                    System.out.println("Customer" + customerNumber + "was turned away");
                    seatMutex.release();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new SleepingBarber(1);
    }
}
