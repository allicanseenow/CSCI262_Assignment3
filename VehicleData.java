package traffic;

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
