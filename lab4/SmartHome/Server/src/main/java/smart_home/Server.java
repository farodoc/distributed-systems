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
    private final Communicator communicator;
    private final ObjectAdapter adapter;
    private int status = 0;

    public Server(String[] args) {
        int id = Integer.parseInt(args[0]);
        this.communicator = Util.initialize(args);
        this.adapter = communicator.createObjectAdapter("Adapter" + id);

        if (id == 1) {
            try {
                IndoorCameraImpl inCamServant = new IndoorCameraImpl();
                OutdoorCameraImpl outCamServant = new OutdoorCameraImpl();
                AirConditionerImpl acServant1 = new AirConditionerImpl();

                adapter.add(inCamServant, new Identity("IndoorCamera1", "IndoorCamera"));
                adapter.add(outCamServant, new Identity("OutdoorCamera1", "OutdoorCamera"));
                adapter.add(acServant1, new Identity("AirConditioner1", "AirConditioner"));
            } catch (Exception e) {
                e.printStackTrace(System.err);
                status = 1;
            }
        } else if (id == 2) {
            try {
                COSensorImpl coSensorServant = new COSensorImpl();
                AirConditionerImpl acServant2 = new AirConditionerImpl();

                adapter.add(coSensorServant, new Identity("COSensor2", "COSensor"));
                adapter.add(acServant2, new Identity("AirConditioner2", "AirConditioner"));
            } catch (Exception e) {
                e.printStackTrace(System.err);
                status = 1;
            }
        } else {
            System.out.println("Invalid server ID. Use 1 or 2.");
            System.exit(status);
        }
    }

    public void run() {
        try {
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
        if (args.length != 2) {
            System.out.println("Usage: java Server <server_id> --Ice.Config=config.server");
            System.exit(1);
        }
        Server server = new Server(args);
        server.run();
    }
}
