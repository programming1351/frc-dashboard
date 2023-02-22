import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

import java.io.IOException;

public class LoggerInterface {
    private static NetworkTable table;
    public LoggerInterface() {

    }


    public static void create() throws IOException {

        NetworkTableInstance inst = NetworkTableInstance.getDefault();
        inst.startClient("10.13.51.2", 1735);
        table = inst.getTable("SmartDashboard");



        while(true) {
//            System.out.println((inst.isConnected()));
            System.out.println(table.getEntry("Test").getDouble(0.0));
        }
//        nt.startDSClient();

        //table = nt.getTable("SmartDashboard");
//        table = nt.getTable("SmartDashboard");
    }
    public static void printNumber(){
        //System.out.println(table.getValue("Test").getDouble());;
    }

    public static void main(String args[]) throws IOException {
        create();
        //printNumber();

    }
}
