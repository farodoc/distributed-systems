package smart_home;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Identity;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;
import smart_home.devices.AirConditionerImpl;
import smart_home.devices.COSensorImpl;
import smart_home.devices.IndoorCameraImpl;
import smart_home.devices.OutdoorCameraImpl;

public class Server {
    public void run(String[] args) {
        int status = 0;
        Communicator communicator = null;

        try {
            communicator = Util.initialize(args);
            ObjectAdapter adapter = communicator.createObjectAdapter("Adapter1");

            IndoorCameraImpl inCamServant = new IndoorCameraImpl();
            OutdoorCameraImpl outCamServant = new OutdoorCameraImpl();
            COSensorImpl coSensorServant = new COSensorImpl();
            AirConditionerImpl acServant1 = new AirConditionerImpl();
            AirConditionerImpl acServant2 = new AirConditionerImpl();

            adapter.add(inCamServant, new Identity("IndoorCamera1", "IndoorCamera"));
            adapter.add(outCamServant, new Identity("OutdoorCamera1", "OutdoorCamera"));
            adapter.add(coSensorServant, new Identity("COSensor1", "COSensor"));
            adapter.add(acServant1, new Identity("AirConditioner1", "AirConditioner"));
            adapter.add(acServant2, new Identity("AirConditioner2", "AirConditioner"));

            adapter.activate();

            System.out.println("Entering event processing loop...");

            communicator.waitForShutdown();

        } catch (Exception e) {
            e.printStackTrace(System.err);
            status = 1;
        }
        if (communicator != null) {
            try {
                communicator.destroy();
            } catch (Exception e) {
                e.printStackTrace(System.err);
                status = 1;
            }
        }
        System.exit(status);
    }


    public static void main(String[] args) {
        Server server = new Server();
        server.run(args);
    }
}
