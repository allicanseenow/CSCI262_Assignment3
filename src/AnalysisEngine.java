import java.io.*;
import java.util.*;

public class AnalysisEngine {
    private List<AnalysisDay> dayList;
    private int vehicleTypeCount;
    private double roadLength;
    private int speedLimit;
    private static final int finalMinute = 1440;
    private Map<Integer, List<AnalysisLine>> speedBreachMap;

    /**
     * Check if a vehicle breaches the speed limit
     * @param line
     */
    private void checkSpeedBreach(AnalysisLine line) {
        int day = line.getDay();
        boolean endRoadDep = line.getEndRoadDep();
        int arrTime = line.getArrTime();
        int depTime = line.getDepTime();
        String rego = line.getRego();

        if (endRoadDep) {
            int finalDepTime = depTime < AnalysisEngine.finalMinute ? depTime : 1440;
            double diffTime = finalDepTime - arrTime;
            // Measure unit: km/hour
            double averageSpeed = roadLength/(diffTime/60);
            // If this vehicle breaches the speed limit
            if (averageSpeed > speedLimit) {
                line.setBreachSpeed(averageSpeed);
                if (!speedBreachMap.containsKey(day)) {
                    speedBreachMap.put(day, new ArrayList<AnalysisLine>());
                }
                speedBreachMap.get(day).add(line);
            }
        }
    }

    private void readFirstLine(Scanner sc) {
        // Number of vehicle type
        vehicleTypeCount = sc.nextInt();
        roadLength = sc.nextDouble();
        speedLimit = sc.nextInt();
        sc.nextLine();
    }

    private void logBreachList() {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("breachSpeed.txt",false));
            out.write("----------------------------------------------------\n");
            out.write("----------------------------------------------------\n");

            Iterator<Map.Entry<Integer, List<AnalysisLine>>> it = speedBreachMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Integer, List<AnalysisLine>> entry = it.next();
                int day = entry.getKey();
                String a = String.format("Day %d\n", day);
                out.write(a);
                List<AnalysisLine> speedBreachVehicleList = entry.getValue();

                for (AnalysisLine b: speedBreachVehicleList) {
                    String temp = String.format("Vehicle type: %s; Registration Number: %s; Breaching speed: %f \n", b.getType(), b.getRego(), b.getBreachSpeed());
                    out.write(temp);
                }
            }
            out.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void clearLogFile() {
        try {
            FileWriter fwOb = new FileWriter("analysis.txt", false);
            PrintWriter pwOb = new PrintWriter(fwOb, false);
            pwOb.flush();
            pwOb.close();
            fwOb.close();
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    public void analyze(String logFileName) {
        dayList = new ArrayList<AnalysisDay>();
        try {
            Scanner sc = new Scanner(new File(logFileName));
            readFirstLine(sc);

            // The day we are reading in the log file. It starts at day 0
            int currentDay = 0;

            // Read data
            while (sc.hasNextLine()) {
                AnalysisLine nextLineData = new AnalysisLine(sc.nextLine());
                checkSpeedBreach(nextLineData);
                if (currentDay < nextLineData.getDay()) {
                    dayList.add(new AnalysisDay(currentDay + 1));
                }
                currentDay = nextLineData.getDay();
                dayList.get(currentDay - 1).put(nextLineData.getType(), nextLineData);
            }

            // Analyze this day
            for (AnalysisDay day: dayList) {
                day.analyzeThisDay();
                day.displayRecord();
                day.logRecord();
            }

            // Log breach list
            logBreachList();

            sc.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<AnalysisDay> getDayList() {
        return dayList;
    }

    public AnalysisEngine(String fileName) {
        speedBreachMap = new HashMap<Integer, List<AnalysisLine>>();
        clearLogFile();
        analyze(fileName);
    }

    public static void main(String[] args) {
        AnalysisEngine a = new AnalysisEngine("log.txt");
    }
}

class AnalysisDay {
    private int day;
    private Map<String, List<AnalysisLine>> map;
    private Map<String, Double> speedMeanList;
    private Map<String, Double> speedSDList;
    private Map<String, Double> volumeMeanList;
    private Map<String, Double> volumeSDList;

    public AnalysisDay(int day) {
        this.day = day;
        map = new HashMap<String, List<AnalysisLine>>();
        speedMeanList = new HashMap<String, Double>();
        speedSDList = new HashMap<String, Double>();
        volumeMeanList = new HashMap<String, Double>();
        volumeSDList = new HashMap<String, Double>();
    }
    public void put(String vehicleType, AnalysisLine lineData) {
        if (map.containsKey(vehicleType)) {
            map.get(vehicleType).add(lineData);
        }
        else {
            map.put(vehicleType, new ArrayList<AnalysisLine>());
            map.get(vehicleType).add(lineData);
        }
    }

    public void analyzeThisDay() {
        Iterator<Map.Entry<String, List<AnalysisLine>>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<AnalysisLine>> entry = it.next();
            String vehicleType = entry.getKey();
            List<AnalysisLine> analysisList = entry.getValue();
            speedMeanList.put(vehicleType, Calculator.calculateSpeedMean(analysisList));
            speedSDList.put(vehicleType, Calculator.calculateSD(analysisList));
            volumeMeanList.put(vehicleType, Calculator.calculateVolumnMean(analysisList));
            volumeSDList.put(vehicleType, Calculator.calculateVolumeSD(analysisList));
        }
    }

    public void displayRecord() {
        System.out.println("----------------------------------------------------");
        System.out.println("----------------------------------------------------");
        System.out.printf("Day %d\n", day);
        Iterator<Map.Entry<String, List<AnalysisLine>>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<AnalysisLine>> entry = it.next();
            String vehicleType = entry.getKey();
            System.out.println("Vehicle type: " + vehicleType);
            System.out.println("\tSpeed mean: " + speedMeanList.get(vehicleType));
            System.out.println("\tSpeed SD: " + speedSDList.get(vehicleType));
            System.out.println("\tVolume mean: " + volumeMeanList.get(vehicleType));
            System.out.println("\tVolume SD: " + volumeSDList.get(vehicleType));
            System.out.println();
        }
    }

    public void logRecord() {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("analysis.txt",true));
            out.write("----------------------------------------------------\n");
            out.write("----------------------------------------------------\n");
            String a = String.format("Day %d\n", day);
            out.write(a);
            Iterator<Map.Entry<String, List<AnalysisLine>>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, List<AnalysisLine>> entry = it.next();
                String vehicleType = entry.getKey();
                out.write("Vehicle type: " + vehicleType + "\n");
                out.write("\tSpeed mean: " + speedMeanList.get(vehicleType) + "\n");
                out.write("\tSpeed SD: " + speedSDList.get(vehicleType) + "\n");
                out.write("\tVolume mean: " + volumeMeanList.get(vehicleType) + "\n");
                out.write("\tVolume SD: " + volumeSDList.get(vehicleType) + "\n");
                out.write("\n");
            }
            out.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Map<String, List<AnalysisLine>> getMap() {
        return map;
    }

    public Map<String, Double> getSpeedMeanList() {
        return speedMeanList;
    }

    public Map<String, Double> getVolumeMeanList() {
        return volumeMeanList;
    }
}

class AnalysisLine {
    private int day;
    private String type;
    private int arrTime;
    private int depTime;
    private double speed;
    private boolean endRoadDep;
    private String rego;
    private int volume;
    private boolean didBreachSpeed;
    private double breachSpeed;

    public AnalysisLine(String line) {
        String[] data = line.split("-");
        day = Integer.parseInt(data[0].trim().split(":")[1]);
        type = data[1].trim().split(":")[1];
        arrTime = Integer.parseInt(data[2].trim().split(":")[1]);
        depTime = Integer.parseInt(data[3].trim().split(":")[1]);
        speed = Double.parseDouble(data[7].trim().split(":")[1]);
        endRoadDep = Boolean.parseBoolean(data[8].trim().split(":")[1]);
        rego = data[9].trim().split(":")[1];
        volume = Integer.parseInt(data[10].trim().split(":")[1]);
        didBreachSpeed = false;
    }

    public int getDay() {
        return day;
    }

    public String getType() {
        return type;
    }

    public int getArrTime() {
        return arrTime;
    }

    public int getDepTime() {
        return depTime;
    }

    public double getSpeed() {
        return speed;
    }

    public boolean getEndRoadDep() {
        return endRoadDep;
    }

    public String getRego() {
        return rego;
    }

    public int getVolume() {
        return volume;
    }

    public boolean didBreachSpeed() {
        return didBreachSpeed;
    }

    public void setBreachSpeed(double speed) {
        didBreachSpeed = true;
        breachSpeed = speed;
    }

    public double getBreachSpeed() {
        return breachSpeed;
    }
}

class Calculator {
    public static double calculateSpeedMean(List<AnalysisLine> analysisLineList) {
        double sum = 0.0;
        for(AnalysisLine num : analysisLineList) {
            sum += num.getSpeed();
        }
        return sum/analysisLineList.size();
    }

    public static double calculateVolumnMean(List<AnalysisLine> analysisLineList) {
        double sum = 0.0;
        for(AnalysisLine num : analysisLineList) {
            sum += num.getVolume();
        }
        return sum/analysisLineList.size();
    }

    public static double calculateSD(List<AnalysisLine> analysisLineList) {
        double sum = 0.0, standardDeviation = 0.0;
        int length = analysisLineList.size();

        for(AnalysisLine num : analysisLineList) {
            sum += num.getSpeed();
        }

        double mean = sum/length;

        for(AnalysisLine num: analysisLineList) {
            standardDeviation += Math.pow(num.getSpeed() - mean, 2);
        }

        return Math.sqrt(standardDeviation/length);
    }

    public static double calculateVolumeSD(List<AnalysisLine> analysisLineList) {
        double sum = 0.0, standardDeviation = 0.0;
        int length = analysisLineList.size();

        for(AnalysisLine num : analysisLineList) {
            sum += num.getVolume();
        }

        double mean = sum/length;

        for(AnalysisLine num: analysisLineList) {
            standardDeviation += Math.pow(num.getVolume() - mean, 2);
        }

        return Math.sqrt(standardDeviation/length);
    }
}