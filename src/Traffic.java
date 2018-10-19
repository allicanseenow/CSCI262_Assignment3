import java.io.File;
import java.util.Arrays;
import java.util.Scanner;

public class Traffic {
    private int numberOfVehicles;
    private Vehicle[] vehicleList;

    private int stats_vehicleCount, roadLength, speedLimit, parkingSpaceAvailable;
    private Stats[] statsList;

    private int days;
    /*
        ----------------------------------------------------------
        Read vehicles.txt
     */

    private void readFirstVehicleLine(Scanner sc) {
        numberOfVehicles = sc.nextInt();
        vehicleList = new Vehicle[numberOfVehicles];
        sc.nextLine();
    }

    private void readEachNextVehicleLine(Scanner sc) {
        String[] line;
        int currentIndex = 0;
        while (sc.hasNextLine()) {
            line = sc.nextLine().split(":");
            // If this line is corrupted, continue with the next line
            try {
                String vehicleName = line[0];
                boolean parkingFlag = (Integer.parseInt(line[1]) != 0);
                String registrationFormat = line[2];
                int volume = Integer.parseInt(line[3]);
                int weight = Integer.parseInt(line[4]);
                vehicleList[currentIndex++] = new Vehicle(vehicleName, parkingFlag, registrationFormat, volume, weight);
                System.out.println("This line is " + Arrays.toString(line));
            }
            catch(Exception ex) { currentIndex++; }
        }
    }

    public void readVehicleFile(String fileName) {
        try (Scanner sc = new Scanner(new File(fileName))) {
            readFirstVehicleLine(sc);
            readEachNextVehicleLine(sc);
        }
        catch (Exception ex) {
            System.err.println("Error while reading file " + fileName);
            ex.printStackTrace();
        }
    }

    /*
        ----------------------------------------------------------
        Read Stats.txt
     */

    private void readFirstStatsLine(Scanner sc) {
        stats_vehicleCount = sc.nextInt();
        roadLength = sc.nextInt();
        speedLimit = sc.nextInt();
        parkingSpaceAvailable = sc.nextInt();
        statsList = new Stats[stats_vehicleCount];
        sc.nextLine();
    }

    private void readEachNextStatsLine(Scanner sc) {
        String[] line;
        int currentIndex = 0;
        while (sc.hasNextLine()) {
            line = sc.nextLine().split(":");
            // If this line is corrupted, continue with the next line
            try {
                statsList[currentIndex] = new Stats(line[0], Double.parseDouble(line[1]), Double.parseDouble(line[2]), Double.parseDouble(line[3]), Double.parseDouble(line[4]));
                System.out.println(statsList[currentIndex]);
                currentIndex++;
            }
            catch (Exception ex) { currentIndex++; }
        }
    }

    public void readStatsFile(String fileName) {
        try (Scanner sc = new Scanner(new File(fileName))) {
            readFirstStatsLine(sc);
            readEachNextStatsLine(sc);
        }
        catch (Exception ex) {
            System.err.println("Error while reading file " + fileName);
            ex.printStackTrace();
        }
    }

    public void readFile(String[] args) {
        try {
            String vehicleFileName = args[0];
            String statFileName = args[1];
            String days = args[2];
            this.days = Integer.parseInt(days);
            readVehicleFile(vehicleFileName);
            readStatsFile(statFileName);
        }
        catch (Exception ex) {
            System.exit(1);
        }
    }

    public Traffic(String vehicleFileName, String statFileName, int days) {
        try {
            readVehicleFile(vehicleFileName);
            readStatsFile(statFileName);
            this.days = days;
        }
        catch (Exception ex) {
            System.exit(1);
        }
    }

    public Traffic() {

    }

    public static void main(String[] args) {
        Traffic a = new Traffic();
        a.readFile(args);
    }

    public Vehicle[] getVehicleList() {
        return vehicleList;
    }

    public Stats[] getStatsList() {
        return statsList;
    }

    public int getDays() {
        return days;
    }
}
