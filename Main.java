package pkg262assignment3;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.io.*;
import java.nio.*;
public class Main 
{
     //global variable, for storing all generated data
    public static List<VehicleData> GeneratedDataList = new ArrayList<VehicleData>();
    //variable for log file name
    public static String LogFileName = "log.txt";
    public static void main(String[] args) 
    {
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
            
            
            // CODE FOR ACTIVITY ENGINE AND LOGS
            //call ActivityEngine, for each day
            for (int i=0;i<numOfDays;i++)
            {
                System.out.println("Starting Simulation for Day " + (i+1));
                //takes parameters vehicle[], stats[], day number, road length
                ActivityEngine(v,VehicleStats,i+1,roadLength);
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
            WriteToLog();
            
            
            
            
            
            
            
            
        }
        catch (Exception ex) {
            System.out.println(ex);
        }
        
        
        
        
        
        
        
        
        
        
        //CODE FOR ANALYSIS ENGINE
        
        
        
        
        
        
        
        // CODE FOR ALERT ENGINE
        
    }
    
    //METHOD FOR WRITING TO LOG FILE
    public static void WriteToLog()
    {
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
                
                System.out.print("Day:"+v.DayNumber);
                System.out.print(":VType:"+v.VehicleName);
                System.out.print(":ArrTime:"+v.ArrivalTime);
                System.out.print(":DepTime:"+v.DepartureTime);
                if(v.Parking==true)
                {
                    System.out.print(":Parking:y");
                }else
                {
                    System.out.print(":Parking:n");
                }
                System.out.print(":ParkStart:"+v.ParkingStartTime);
                System.out.print(":ParkEnd:"+v.ParkingStopTime);
                System.out.print(":Speed:"+v.Speed);
                System.out.print(":FinalSpeed:"+v.FinalSpeed);
                System.out.println();
                
                
                
            }
            out.close();
        }catch(Exception e)//Exception is for general exceptions
        {
            System.out.println(e);
        }
    }
    
    public static void ActivityEngine(Vehicle[] vStats,Stats[] VehicleStats, int dayNumber,int road)//activity engine should take stats list as parameter as well
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
            String regoFormat = "LLDD";//just an example, set to proper value later
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
                String Registration = GenerateRego(regoFormat, dayNumber);
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
    public static String GenerateRego(String format, int dayNumber)
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
                //do something to get a random letter, preferrably upper case
                //get random int within range of alphabet
                int position = ThreadLocalRandom.current().nextInt(0,25+1);

                //set tempLetter to equal thingy in string
                tempLetter=letters.charAt(position);
                //concat it to temp
                temp = temp + Character.toString(tempLetter);
            }else if(str.equals("d"))
            {
                //get a random digit
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
        //linear search through array list, if day number and rego are the same
        //call this method again
        if(alreadyExists==true)
        {
            System.out.println("Vehicle appears twice - generating new registration plate");
            GenerateRego(format,dayNumber);
        }

        System.out.println(temp);
        return temp;
    }
    
}



//class for storing generated data
class VehicleData implements Comparable<VehicleData>
{
    //fields
    public String VehicleName;
    public String ArrivalTime;//time is in HHMM
    public boolean EndRoadDeparture;//true if the vehicle departs via end road
    public boolean Parking;//true if vehicle parks at some point
    public int ParkingStartTime;//set to 0 if boolean is false
    public int ParkingStopTime;//set to 0 if boolean is false
    public String DepartureTime;//departure time in HHMM
    public double Speed;//km/hr
    public int DayNumber;//which day it is
    public String Registration;//rego number
    public double FinalSpeed;//km/hr
    
    //default constructor
    public VehicleData()
    {
        VehicleName = "";
        ArrivalTime = "";
        EndRoadDeparture = true;
        Parking = false;//assumes no parking
        ParkingStartTime = 0;
        ParkingStopTime = 0;
        DepartureTime = "";
        Speed = 0;
        DayNumber = 0;
        Registration = "";
        FinalSpeed = 0;
    }
    
    //constructor
    public VehicleData(String VehicleName, String ArrivalTime, 
                       String DepartureTime, boolean EndRoadDeparture, 
                       boolean Parking, int ParkingStartTime, 
                       int ParkingStopTime, double Speed, int DayNumber, String Registration, double FinalSpeed)
    {
        this.VehicleName = VehicleName;
        this.ArrivalTime = ArrivalTime;
        this.DepartureTime = DepartureTime;
        this.EndRoadDeparture = EndRoadDeparture;
        this.Parking = Parking;
        this.ParkingStartTime = ParkingStartTime;
        this.ParkingStopTime = ParkingStopTime;
        this.Speed = Speed;
        this.DayNumber = DayNumber;
        this.Registration = Registration;
        this.FinalSpeed = FinalSpeed;
    }
    
    //for sorting
    @Override
    public int compareTo(VehicleData v)
    {
        //compare by day number
        int value1 = this.DayNumber - v.DayNumber;
        if(value1==0)
        {//if same day
            //compare by arrival time
            int value2 = this.ArrivalTime.compareTo(v.ArrivalTime);
            
            if(value2==0)
            {//if same arrival time
                //compare by vehicle name
                int value3 =this.VehicleName.compareTo(v.VehicleName);
                return value3;
            }else
            {
                return value2;
            }
            
        }else
        {
            return value1;
        }
    }
}
