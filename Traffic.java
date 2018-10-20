package traffic;
import java.io.File;
import java.util.Arrays;
import java.util.Scanner;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.io.*;


public class Traffic {
    
    //public static List<VehicleData> GeneratedDataList = new ArrayList<VehicleData>();
    //variable for log file name
    public static String LogFileName = "log.txt";
    
    
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

    public Vehicle[] getVehicleList() {
        return vehicleList;
    }

    public Stats[] getStatsList() {
        return statsList;
    }

    public int getDays() {
        return days;
    }

    public int getNumberOfVehicles() {
        return numberOfVehicles;
    }

    public int getRoadLength() {
        return roadLength;
    }

    public int getSpeedLimit() {
        return speedLimit;
    }

    public int getParkingSpaceAvailable() {
        return parkingSpaceAvailable;
    }

    public static void main(String[] args) {
        
        // CODE FOR INITIAL INPUT
        try {
            String vehicleFileName = args[0];
            String statFileName = args[1];
            String days = args[2];
            int numOfDays = Integer.parseInt(days);
            Traffic a = new Traffic(vehicleFileName,statFileName,numOfDays);
            //and then to get the vehicle list and stats list
            Vehicle[] v = a.getVehicleList();
            Stats[] VehicleStats = a.getStatsList();
            int roadLength = a.getRoadLength();
            
            List<VehicleData> GeneratedDataList = new ArrayList<VehicleData>();
            // CODE FOR ACTIVITY ENGINE AND LOGS
            //call ActivityEngine, for each day
            for (int i=0;i<numOfDays;i++)
            {
                System.out.println("Starting Simulation for Day " + (i+1));
                //takes parameters vehicle[], stats[], day number, road length
                ActivityEngine(v,VehicleStats,i+1,roadLength,GeneratedDataList);
            }
            //sort generateddatalist
            Collections.sort(GeneratedDataList);
            //MANAGE PARKING SPACES HERE
            int availableParking = a.getParkingSpaceAvailable();
            //make array of available parking spaces
            int[] parkingSpaces = new int[availableParking];//parking spaces stores the last time it is occupied
            //set parking spaces to zero
            for (int i=0;i<availableParking;i++)
            {
                parkingSpaces[i]=0;
            }
            //create temporary variables to hold day numbers
            int prevDay, currentDay;
            prevDay=0;
            //loop through generated data (separated by day)
            for(VehicleData c: GeneratedDataList)
            {
                //set current day number
                currentDay=c.DayNumber;
                //first of all check if previous day num==current day num
                if(currentDay!=prevDay)
                {//if not, reset parking spaces for a new day
                    for (int i=0;i<availableParking;i++)
                    {
                        parkingSpaces[i]=0;
                    }
                }
                //process parking...
                int counter=0;
                //search through array to see if there are available spaces
                for (int i=0;i<availableParking;i++)
                {
                    if(c.ParkingStartTime>parkingSpaces[i])
                    {
                        parkingSpaces[i]=c.ParkingStopTime;
                        c.Parking = true;
                        break;
                    }else
                    {
                        counter++;
                    }
                }
                if(counter>=availableParking)
                {//if loop has gone through without a parking space
                    //change values of this vehicle so that it doesn't park
                    c.Parking = false;
                    //c.ParkingStartTime=0;
                    //c.ParkingStopTime=0;
                }
                //set prevDay number
                prevDay=c.DayNumber;
            }
            //double check that vehicles with false for parking do not have parking times
            for(VehicleData d: GeneratedDataList)
            {
                if(d.Parking==false)
                {
                    d.ParkingStartTime = 0;
                    d.ParkingStopTime = 0;
                }
            }
            //call WriteToLog to write in entries
            WriteToLog(GeneratedDataList);
            
            
            
            
            
            
            
            
        }
        catch (Exception ex) {
            System.out.println(ex);
        }
        
        
        
        
        
        
        
        
        
        
        //CODE FOR ANALYSIS ENGINE
        
        
        
        
        
        
        
        // CODE FOR ALERT ENGINE
    }
    
    //METHOD FOR WRITING TO LOG FILE
    public static void WriteToLog(List<VehicleData> GeneratedDataList)
    {//take a variable of List<VehicleData> as parameter
        //try to write stuff to log
        try
        {
            BufferedWriter out = new BufferedWriter(new FileWriter(LogFileName,false));
            //NOTE: true for append, false for overwrite
            for(VehicleData v : GeneratedDataList)
            {
                out.write("Day:"+v.DayNumber);
                out.write(":VType:"+v.VehicleName);
                out.write(":ArrTime:"+v.ArrivalTime);
                out.write(":DepTime:"+v.DepartureTime);
                if(v.Parking==true)
                {
                    out.write(":Parking:y");
                }else
                {
                    out.write(":Parking:n");
                }
                out.write(":ParkStart:"+v.ParkingStartTime);
                out.write(":ParkEnd:"+v.ParkingStopTime);
                out.write(":Speed:"+v.Speed);
                out.write(":FinalSpeed:"+v.FinalSpeed);
                out.write("\n");
                //log file entries are as below
                //Day:XX:VType:XX:ArrTime:XX:DepTime:XX:Parking:y/n:ParkStart:XX:ParkEnd:XX:Speed:XX:FinalSpeed:XX
            }
            out.close();
        }catch(Exception e)//Exception is for general exceptions
        {
            System.out.println(e);
        }
    }
    
    public static void ActivityEngine(Vehicle[] vStats,Stats[] VehicleStats, int dayNumber,int road, List<VehicleData> GeneratedDataList)
    {//this generates the vehicles for a single day
        double roadLength = Double.valueOf(road);
        for(Stats vehicle : VehicleStats)
        {//for every vehicle in the list of vehicle statistics
            //declare variable for number of vehicles
            int numberOfVehicles;
            //set standard dev and mean for num of vehicles
            double numStandardDev = vehicle.numberStandardDeviation;
            double numMean = vehicle.numberMean;
            //generate random number of vehicles for this one
            Random r = new Random();
            numberOfVehicles = (int) Math.round(r.nextGaussian()*numStandardDev + numMean);
            //also get the speed mean to help generate times later
            double speedMean = vehicle.speedMean;
            double speedStandardDev = vehicle.speedStandardDeviation;
            //vehicle name
            String VName = vehicle.name;
            System.out.println("Number of Vehicles of Type '"+VName+"': " + numberOfVehicles);
            //first check if parking flag from car stats allows for parking
            boolean parkingAllowed = false;//set to false as default
            //determine format of registration
            String regoFormat = "LLDD";//just an example, set to proper value below:
            //loop through Vehicle[] until name matches each other
            for(Vehicle ve:vStats)
            {
                if(VName.compareTo(ve.name)==0)
                {
                    //set parking and regoFormat
                    parkingAllowed = ve.parkingFlag;
                    regoFormat = ve.registrationForm;
                }
            }
            
            //for each vehicle of this type, generate data
            for(int i=0;i<numberOfVehicles;i++)
            {
                //generate registration plate
                String Registration = GenerateRego(regoFormat, dayNumber,GeneratedDataList);
                //vehicle name

                //get speed of vehicle first (gaussian random speed)
                double speed = r.nextGaussian()*speedStandardDev + speedMean;
                //average length of time spent on the road in minutes:
                int avTimeOnRoad = (int) Math.round( roadLength/(speed/60) );
                //get time in minutes (arrival only)
                int aTimeMin = ThreadLocalRandom.current().nextInt(0,1380+1);//1380 = 23*60
                
                
                //departure time
                int dTimeMin = (int) Math.round(r.nextGaussian() + (avTimeOnRoad+aTimeMin));
                
                
                
                //EndRoadDeparture
                boolean EndRoadD;
                //get random number (1 or 0) to decide true or false for EndRoadDeparture
                int ranNum = ThreadLocalRandom.current().nextInt(0,1+1);
                if(ranNum==0)
                {
                    EndRoadD = false;
                }else
                {
                    EndRoadD = true;
                }
                //parking bool
                boolean parking;
                //parking start time and stop time
                int parkSTime,parkEndTime;
                if(parkingAllowed)
                {//if parking is allowed
                    //set randum number1 to see if this vehicle will park
                    int ranNum1 = ThreadLocalRandom.current().nextInt(0,1+1);
                    
                    if(ranNum1==0)
                    {
                        parking = false;
                    }else
                    {
                        parking = true;
                    }
                    
                    if(parking==true)
                    {//set up parking times
                        parkSTime = ThreadLocalRandom.current().nextInt(aTimeMin,aTimeMin+avTimeOnRoad+1); 
                        parkEndTime = ThreadLocalRandom.current().nextInt(parkSTime,parkSTime+15);//up to 15 mins parking
                        //redo departure time 
                        dTimeMin = (int) Math.round(r.nextGaussian() + (aTimeMin+parkEndTime));
                    }else
                    {//set parking times to 0
                        parkSTime = 0;
                        parkEndTime=0;
                    }
                }else
                {//vehicle is not allowed to park at all
                    parking=false;
                    //don't generate parking times
                    parkSTime = 0;
                    parkEndTime=0;
                }
                
                
                //also check if departure exceeds 1440 (a whole 24 hours)
                if(dTimeMin>=1440)
                {
                    //departure time is past midnight, so it is set to midnight
                    //i.e. it disappears at midnight
                    dTimeMin=1440;
                }
                //check if departure time is smaller than parking time
                if(dTimeMin<=parkEndTime)
                {
                    dTimeMin = parkEndTime+2;
                }
                //arrival time, convert minutes to readable string format
                String arrivalTime = ConvertMinutesToHrsMin(aTimeMin);
                //do the same for departuretime
                String departureTime = ConvertMinutesToHrsMin(dTimeMin);
                //calculate average speed over road
                double hoursSpent = (Double.valueOf(dTimeMin) - Double.valueOf(aTimeMin))/60;
                double FinalSpeed = roadLength/hoursSpent;
                //initialise a VehicleData object
                VehicleData a = new VehicleData(VName,arrivalTime,
                                                departureTime,EndRoadD,
                                                parking,parkSTime,
                                                parkEndTime,speed,
                                                dayNumber, Registration,
                                                FinalSpeed);
                //add object to the list
                GeneratedDataList.add(a);
            }
                
        }
    }
    
    public static String ConvertMinutesToHrsMin(int minutes)
    {
        String FinalTime,strH,strM;
        //convert
        int Hours = minutes / 60;
        int Min = minutes % 60;
        if(Hours<10)
        {
            strH = "0" + Integer.toString(Hours);
        }else
        {
            strH = Integer.toString(Hours);
        }
        
        if(Min<10)
        {
            strM = "0"+Integer.toString(Min);
        }else
        {
            strM = Integer.toString(Min);
        }
        FinalTime=strH+strM;
        return FinalTime;
    }
    public static int ConvertHHMMToMinutes(String time)
    {
        String Hr = time.substring(0,2);
        String Min = time.substring(2,4);
        int hour = Integer.parseInt(Hr);
        int minute = Integer.parseInt(Min);
        int FinalTime = hour * 60 + minute;
        return FinalTime;
    }
    public static String GenerateRego(String format, int dayNumber, List<VehicleData> GeneratedDataList)
    {
        int length = format.length();
        String temp = "";
        //convert format to lower case
        format = format.toLowerCase();
        
        //set of letters to choose from
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZA";
        //char to store letter in
        char tempLetter;
        //int to store digit in
        int tempDigit;
        String str;
        //go through format and generate rego
        for (int i=0; i<length;i++)
        {
            str = Character.toString(format.charAt(i));
            if(str.equals("l"))
            {
                //get random int within range of alphabet
                int position = ThreadLocalRandom.current().nextInt(0,25+1);

                //set tempLetter to equal letter in particular position of string
                tempLetter=letters.charAt(position);
                //concat it to temp
                temp = temp + Character.toString(tempLetter);
            }else if(str.equals("d"))
            {
                //get a random digit, between 0 and 9
                tempDigit = ThreadLocalRandom.current().nextInt(0,9+1);;
                //concat it to temp and convert digit to string
                temp = temp + Integer.toString(tempDigit);
            }
        }
        boolean alreadyExists = false;
        //check if rego already exists
        for(VehicleData d : GeneratedDataList)
        {
            if(dayNumber == d.DayNumber && temp.equals(d.Registration))
            {//same day, same car shows up twice
                alreadyExists = true;
            }
        }
        //call this GenerateRego again if registration already exists for a given day
        if(alreadyExists==true)
        {
            System.out.println("Vehicle appears twice - generating new registration plate");
            GenerateRego(format,dayNumber,GeneratedDataList);
        }

        System.out.println(temp);
        return temp;
    }
}
