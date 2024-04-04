package csx55.dfs.replication;

import csx55.dfs.cli.ClientCLIManager;


/*
Client allows for uploading/downloading files
Sends control plane traffic to Controller
Sends data plane traffic to ChunkServers
 */
public class Client {

    private final String controllerIpAddress;
    private final int controllerPortNumber;

    public Client(String controllerIpAddress, int controllerPortNumber) {
        this.controllerIpAddress = controllerIpAddress;
        this.controllerPortNumber = controllerPortNumber;
    }


    /*
    Setup CLI
     */
    public void doWork() {
        manageCLI();
    }


    /*
    Upload a file
     */
    public void upload(String filepath) {
        System.out.println("Implement Upload: " + filepath);
    }


    /*
    Download a file
     */
    public void download(String filepath) {
        System.out.println("Implement Download: " + filepath);
    }


    /*
    Manage CLI input
     */
    public void manageCLI() {
        ClientCLIManager cliManager = new ClientCLIManager(this);
        Thread thread = new Thread(cliManager);
        thread.start();
    }


    /*
    Entrypoint
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            String registryIpAddress = args[0];
            int registryPortNumber = Integer.parseInt(args[1]);
            Client client = new Client(registryIpAddress, registryPortNumber);
            client.doWork();
        }
        else {
            System.err.println("Invalid Usage. Provide IP/Port of Registry");
        }
    }

}
