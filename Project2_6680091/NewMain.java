/*
    Chananchida Phanumes       6680101
    Panisa Laohom              6680091
    Printitta Tangpongsirikul  6680152
    Theresa Rujipatanakul      6680211
    Wipavee Buttayothee        6680655
 */
package Project2_6680091;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

class AgencyThread extends Thread {

    private int max_ArrivalCustomers;
    private int NewArrival;
    private int remain_cus = 0;
    private ArrayList<Tour> sharedTourList;
    private CyclicBarrier a_barrier;
    private CyclicBarrier main_barrier;
    private int days = 0;
    Random rand = new Random();

    public AgencyThread(String name, int ma, ArrayList<Tour> stl) {
        super(name);
        max_ArrivalCustomers = ma;
        sharedTourList = stl;
    }

    public void setBarrier(CyclicBarrier m_ba, CyclicBarrier ba) {
        a_barrier = ba;
        main_barrier = m_ba;
    }

    public void setDays(int d) {
        days = d;
    }

    public void run() {

        try {
            for (int i = 0; i < days; i++) {
                main_barrier.await(); // wait until main prints day number
                Update_CusArri();
                a_barrier.await(); // wait until all AgencyThreads finish updating customers arrived
                Send_Tour(NewArrival);
                if (a_barrier.getNumberWaiting() == a_barrier.getParties() - 1) {
                    System.out.printf("%18s  >>  \n", Thread.currentThread().getName());
                }
                a_barrier.await(); // wait until all AgencyThreads finish sending customers
                main_barrier.await(); // let OperatorThreads start
                main_barrier.await(); // finish for the day 
            }
        } catch (BrokenBarrierException | InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }

    public synchronized void Update_CusArri() {
        NewArrival = rand.nextInt(1, max_ArrivalCustomers + 1); // Rand new customer arrival
        remain_cus += NewArrival;
        System.out.printf("%18s  >>  new arrival = %3d                 remaining customers = %3d\n", Thread.currentThread().getName(), NewArrival, remain_cus);
    }

    public synchronized void Send_Tour(int new_cus) {
        int send_cus = 0;
        int n = rand.nextInt(sharedTourList.size()); // Rand tour
        Tour t = sharedTourList.get(n);
        int available_seats = t.getavailable_seats();

        if (new_cus <= available_seats) {
            send_cus = new_cus;
        } else {
            send_cus = available_seats;
        }

        t.update_seats_taken(send_cus);
        remain_cus -= send_cus;
        System.out.printf("%18s  >>  send %3d customers to %-8s    seats taken   =%,5d\n", Thread.currentThread().getName(), send_cus, t.getName(), t.get_seats_taken());
    }
}

class Tour implements Comparable<Tour> {

    private int max_capacity;
    private int total_cus;
    private String name;
    private int seats_taken;

    public Tour(String n, int mc) {
        name = n;
        max_capacity = mc;
    }

    public String getName() {
        return name;
    }

    public int getavailable_seats() {
        return max_capacity - seats_taken;
    }

    public void update_seats_taken(int new_cus) {
        total_cus += new_cus;
        seats_taken += new_cus;
    }

    public void reset_new_day() {
        seats_taken = 0;
    }

    public int get_seats_taken() {
        return seats_taken;
    }

    public int get_total_cus() {
        return total_cus;
    }

    public int compareTo(Tour other) {
        if (this.total_cus != other.total_cus) {
            return Integer.compare(other.total_cus, this.total_cus);
        } else {
            return this.name.compareToIgnoreCase(other.name);
        }
    }
}

class OperatorThread extends Thread {

    private Tour assignedTour;
    private ArrayList<Place> sharedPlaceList;
    private CyclicBarrier main_barrier;
    private int days = 0;

    public OperatorThread(String name, Tour t, ArrayList<Place> spl) {
        super(name);
        assignedTour = t;
        sharedPlaceList = spl;
    }

    public void setBarrier(CyclicBarrier m_ba) {
        main_barrier = m_ba;
    }

    public void setDays(int d) {
        days = d;
    }

    public void run() {
        try {
            for (int i = 0; i < days; i++) {
                main_barrier.await(); // wait until main prints day number
                main_barrier.await(); // wait until all AgencyThreads finish sending customers.

                int seat = assignedTour.get_seats_taken();
                if (seat <= 0) {
                    System.out.printf("%18s  >>  no customer\n", Thread.currentThread().getName(), assignedTour.getName());
                } else {
                    transport_Cus(seat);
                    assignedTour.reset_new_day();
                }
                main_barrier.await(); // finish for the day
            }
        } catch (BrokenBarrierException | InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }

    public synchronized void transport_Cus(int cus) {
        synchronized (sharedPlaceList) {
            Random rand = new Random();
            int destination = rand.nextInt(sharedPlaceList.size());
            sharedPlaceList.get(destination).update_visitor(cus);

            System.out.printf("%18s  >>  take %3d customers to %-8s    visitor count =%,5d\n", Thread.currentThread().getName(), cus, sharedPlaceList.get(destination).getName(), sharedPlaceList.get(destination).get_visitor());
        }
    }

}

class Place {

    private String name;
    private int visitors = 0;

    public Place(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public synchronized void update_visitor(int new_vit) {
        visitors += new_vit;
    }

    public synchronized int get_visitor() {
        return visitors;
    }
}

public class NewMain {

    public static void main(String[] args) {

        int days = 0, num_travelagencies = 0, num_maxdailyarrival = 0, num_tour = 0,
                num_tourcapacity = 0, num_place = 0;

        try {
            NewMain program = new NewMain();
            File inFile = program.OpenFile();

            Scanner fileScan = new Scanner(inFile);
            while (fileScan.hasNext()) {
                String line = fileScan.nextLine();
                String[] cols = line.split(",");
                switch (cols[0].trim()) {
                    case "days":
                        days = Integer.parseInt(cols[1].trim());
                        break;
                    case "agency_num_arrival":
                        num_travelagencies = Integer.parseInt(cols[1].trim());
                        num_maxdailyarrival = Integer.parseInt(cols[2].trim());
                        break;
                    case "tour_num_capacity":
                        num_tour = Integer.parseInt(cols[1].trim());
                        num_tourcapacity = Integer.parseInt(cols[2].trim());
                        break;
                    case "place_num":
                        num_place = Integer.parseInt(cols[1].trim());
                        break;
                }
            }
            fileScan.close();
        } catch (FileNotFoundException | NumberFormatException e) {
            System.err.println("An error occurs. End program.");
            System.err.println(e);
            // System.exit(-1);
        }

        ArrayList<Tour> allTours = new ArrayList<>();
        for (int i = 0; i < num_tour; i++) {
            allTours.add(new Tour("Tour_" + i, num_tourcapacity));
        }

        ArrayList<AgencyThread> allAgencies = new ArrayList<>();
        for (int i = 0; i < num_travelagencies; i++) {
            allAgencies.add(
                    new AgencyThread("AgencyThread_" + i, num_maxdailyarrival, allTours));
        }

        ArrayList<Place> PlaceList = new ArrayList<>();
        for (int i = 0; i < num_place; i++) {
            PlaceList.add(new Place("Place_" + i));
        }

        ArrayList<OperatorThread> allOperators = new ArrayList<>();
        for (int i = 0; i < num_tour; i++) {
            allOperators.add(new OperatorThread("OperatorThread_" + i, allTours.get(i), PlaceList));
        }

        CyclicBarrier a_barrier = new CyclicBarrier(allAgencies.size());
        CyclicBarrier main_barrier = new CyclicBarrier(allOperators.size() + allAgencies.size() + 1);

        for (AgencyThread a : allAgencies) {
            a.setBarrier(main_barrier, a_barrier);
            a.setDays(days);
        }

        for (AgencyThread a : allAgencies) {
            a.start();
        }

        for (OperatorThread o : allOperators) {
            o.setBarrier(main_barrier);
            o.setDays(days);
        }
        for (OperatorThread o : allOperators) {
            o.start();
        }

        //print header
        System.out.printf("%17s  >>  %s Parameters %s\n", Thread.currentThread().getName(), "=".repeat(20), "=".repeat(20));
        System.out.printf("%17s  >>  %-18s = %d\n", Thread.currentThread().getName(), "Days of simulation", days);
        System.out.printf("%17s  >>  %-18s = %d\n", Thread.currentThread().getName(), "Max arrival", num_maxdailyarrival);
        System.out.printf("%17s  >>  %-18s = [", Thread.currentThread().getName(), "AgencyThreads");
        for (int i = 0; i < allAgencies.size(); i++) {
            if (i == allAgencies.size() - 1) {
                System.out.printf("%s]\n", allAgencies.get(i).getName());
            } else {
                System.out.printf("%s, ", allAgencies.get(i).getName());
            }
        }
        System.out.printf("%17s  >>  %-18s = %d\n", Thread.currentThread().getName(), "Tour capacity", num_tourcapacity);
        System.out.printf("%17s  >>  %-18s = [", Thread.currentThread().getName(), "OperatorThreads");

        for (int i = 0; i < allOperators.size(); i++) {
            if (i == allOperators.size() - 1) {
                System.out.printf("%s]\n", allOperators.get(i).getName());
            } else {
                System.out.printf("%s, ", allOperators.get(i).getName());
            }
        }
        System.out.printf("%17s  >>  %-18s = [", Thread.currentThread().getName(), "Places");

        for (int i = 0; i < num_place; i++) {
            if (i == num_place - 1) {
                System.out.printf("%s]\n", PlaceList.get(i).getName());
            } else {
                System.out.printf("%s, ", PlaceList.get(i).getName());
            }
        }
        System.out.printf("%17s  >>  \n", Thread.currentThread().getName());

        try {
            for (int i = 0; i < days; i++) {
                System.out.printf("%18s  >>  %s\n", Thread.currentThread().getName(), "=".repeat(52));
                System.out.printf("%18s  >>  Day %d\n", Thread.currentThread().getName(), i + 1);
                System.out.printf("%18s  >>  \n", Thread.currentThread().getName());
                main_barrier.await(); // let AgencyThreads start
                main_barrier.await(); // let OperatorThreads start
                main_barrier.await(); // wait until all threads finish for the day
                System.out.printf("%18s  >>  \n", Thread.currentThread().getName());
            }
        } catch (BrokenBarrierException | InterruptedException e) {
            System.err.println(e.getMessage());
        }
        Collections.sort(allTours);
        //print summary
        System.out.printf("%18s  >>  %s\n", Thread.currentThread().getName(), "=".repeat(52));
        System.out.printf("%18s  >>  %s\n", Thread.currentThread().getName(), "Summary");
        for (int i = 0; i < num_tour; i++) {
            System.out.printf("%18s  >>  %-10s total customers = %5d \n", Thread.currentThread().getName(), allTours.get(i).getName(), allTours.get(i).get_total_cus());
        }
    }

    public File OpenFile() {
        String path = "src/main/Java/Project2_6680091/";
        String fileName = "config.txt";
        Scanner input = new Scanner(System.in);

        boolean opensuccess = false;
        while (!opensuccess) {
            try {
                File file = new File(path + fileName);

                if (!file.exists()) {
                    throw new FileNotFoundException();
                }

                opensuccess = true;
            } catch (FileNotFoundException e) {
                System.err.println(e + ": " + path + fileName + " (The system cannot find the file specified)");
                System.err.println("New file name: ");
                fileName = input.nextLine();
            }
        }
        String filepath = path + fileName;
        File inFile = new File(filepath);
        return inFile;

    }
}
