package robot;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
        //You must call Comms.run before using any of the methods to initialize the Ip and set client mode 

public class Comms {
    private static final double DEGREE_OFFSET=-5;
    private static NetworkTable table;
    
    public static void init() {
        NetworkTable.setClientMode();
        NetworkTable.setIPAddress("roboRIO-2202-FRC.local");
        table=NetworkTable.getTable("database");
        setState(0);
    }
    
    public static void setAngle(float angleIn){
        angleIn+=DEGREE_OFFSET;
        System.out.println("Turning: "+angleIn+" degrees.");
        table.putNumber("degreesToTurn",angleIn);
    }
    public static void setDistance(double distanceIn){
        table.putNumber("distance", distanceIn);
    }
    public static void setState(int visionState){
        table.putNumber("shootingState", visionState);
    }
    public static double getState(){
        //default value for this might not be good
        return table.getNumber("shootingState", -999);
    }
    
    public static boolean shouldRunVision() {
        return getState()==1;//should take picture;
    }
}
//DEAR DAVID: the import above does work;