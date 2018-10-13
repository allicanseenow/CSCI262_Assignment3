package pkg262assignment3;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Main 
{
    //global variable, i guess
    public static List<VehicleData> GeneratedDataList = new ArrayList<VehicleData>();
    
    public static void main(String[] args) 
    {
        // code for Initial Input
        
        // code for Activity Engine and Logs
        int daysToSimulate = 10;
        //call ActivityEngine, for each day
        for (int i=0;i<daysToSimulate;i++)
        {
            System.out.println("Starting Simulation for Day " + (i+1));
            ActivityEngine(i+1);//activity engine should take a list of input stats as well as day number
        }
        
        
   
    }
    
    public static void ActivityEngine(int dayNumber)//activity engine should take stats list as parameter as well
    {
        //for(vehiclestats vehicle : vehiclestats)//for every vehicle in the list of vehicle statistics
        //{
            int numberOfVehicles;
            double numStandardDev = 2;//vehicle.NumberStandardDev
            double numMean = 5;//vehicle.NumberMean
            //generate random number of vehicles for this one
            Random r = new Random();
            numberOfVehicles = (int) Math.round(r.nextGaussian()*numStandardDev + numMean);
            //also get the speed mean to help generate times later
            double speedMean = 50;//vehicle.speedMean
            double speedStandardDev = 2;//vehicle.speedStandardDev
            double roadLength = 10;
            System.out.println("Number of Vehicles of Type (include name here) " + numberOfVehicles);
            //for each vehicle of this type, generate data
            for(int i=0;i<numberOfVehicles;i++)
            {
                //vehicle name
                String VName = "asdf";//vehicle.Name
                //get speed of vehicle first (gaussian random speed)
                double speed = r.nextGaussian()*speedStandardDev + speedMean;
                //average length of time spent on the road in minutes:
                int avTimeOnRoad = (int) Math.round( roadLength/(speed/60) );
                //get time in minutes (arrival and departure)
                int aTimeMin = ThreadLocalRandom.current().nextInt(0,1380+1);//1380 = 23*60
                int dTimeMin = ThreadLocalRandom.current().nextInt(aTimeMin,aTimeMin+avTimeOnRoad+1);//max time is min+time on road
                //arrival time, convert minutes to hrs and minutes
                int arrivalTime = ConvertMinutesToHrsMin(aTimeMin);
                //departuretime
                int departureTime = ConvertMinutesToHrsMin(dTimeMin);
                //EndRoadDeparture
                boolean EndRoadD;
                //get random number (1 or 0) to decide true or false for EndRoadDeparture
                int ranNum = ThreadLocalRandom.current().nextInt(0,1+1);
                //System.out.println("True or false: "+ranNum);
                if(ranNum==0)
                {
                    EndRoadD = false;
                }else
                {
                    EndRoadD = true;
                }
                //parking bool
                int ranNum1 = ThreadLocalRandom.current().nextInt(0,1+1);
                boolean parking;
                //System.out.println("True or false: "+ranNum1);
                if(ranNum1==0)
                {
                    parking = false;
                }else
                {
                    parking = true;
                }
                //parking start time and stop time
                int parkSTime,parkEndTime;
                if(parking==true)
                {//set up parking times
                    parkSTime = ThreadLocalRandom.current().nextInt(aTimeMin,aTimeMin+avTimeOnRoad+1); 
                    parkEndTime = ThreadLocalRandom.current().nextInt(parkSTime,aTimeMin+avTimeOnRoad+1);
                }else
                {//set parking times to 0
                    parkSTime = 0;
                    parkEndTime=0;
                }
                
                //initialise a VehicleData object
                VehicleData a = new VehicleData(VName,arrivalTime,
                                                departureTime,EndRoadD,
                                                parking,parkSTime,
                                                parkEndTime,speed,
                                                dayNumber);
                //add object to the list
                GeneratedDataList.add(a);
            }
                
        //}
    }
    
    public static int ConvertMinutesToHrsMin(int minutes)
    {
        //System.out.println("Minutes is " + minutes);
        //convert
        int Hours = minutes / 60;
        int Min = minutes % 60;
        Hours = Hours * 100;
        int FinalTime = Hours + Min;
        //System.out.println("In HH:MM we have " + FinalTime);
        return FinalTime;
    }
    public static int ConvertHHMMToMinutes(int time)
    {
        String strTime = Integer.toString(time);
        String Hr = strTime.substring(0,2);
        String Min = strTime.substring(2,4);
        //System.out.println(Hr + " : " + Min);
        int hour = Integer.parseInt(Hr);
        int minute = Integer.parseInt(Min);
        int FinalTime = hour * 60 + minute;
        //System.out.println("FinalTime: " + FinalTime);
        return FinalTime;
    }
    
}



//class for storing generated data
class VehicleData//for storing all the generated data
{
    public String VehicleName;
    public int ArrivalTime;//time is in HHMM
    public boolean EndRoadDeparture;//true if the vehicle departs via end road
    public boolean Parking;//true if vehicle parks at some point
    public int ParkingStartTime;//set to 0 if boolean is false
    public int ParkingStopTime;//set to 0 if boolean is false
    public int DepartureTime;
    public double Speed;
    public int DayNumber;
    
    //default constructor
    public VehicleData()
    {
        VehicleName = "";
        ArrivalTime = 0;
        EndRoadDeparture = true;
        Parking = false;//assumes no parking
        ParkingStartTime = 0;
        ParkingStopTime = 0;
        DepartureTime = 0;
        Speed = 0;
        DayNumber = 0;
    }
    //constructor
    public VehicleData(String VehicleName, int ArrivalTime, 
                       int DepartureTime, boolean EndRoadDeparture, 
                       boolean Parking, int ParkingStartTime, 
                       int ParkingStopTime, double Speed, int DayNumber)
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
    }
}