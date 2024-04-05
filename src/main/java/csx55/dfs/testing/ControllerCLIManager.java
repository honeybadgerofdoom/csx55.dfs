package csx55.dfs.testing;

import csx55.dfs.replication.Controller;

import java.util.Scanner;


/*
CLI for the Controller node
For testing/debugging only
 */
public class ControllerCLIManager implements Runnable {

    private Controller controller;

    public ControllerCLIManager(Controller controller) {
        this.controller = controller;
    }


    /*
    Run method for the thread
     */
    public void run() {
        String input = "";
        Scanner sc = new Scanner(System.in);
        while (!(input = sc.nextLine()).equals("quit")) {
            processInput(input);
        }
        sc.close();
        System.exit(0);
    }


    /*
    Process CLI input
     */
    public void processInput(String input) {
        String[] parsedInput = input.split(" ");
        String command = parsedInput[0];
        switch(command) {
            case "poke":
                controller.pokeChunkServers();
                break;
            case "chunk-servers":
                if (parsedInput.length == 1) {
                    controller.printChunkServers();
                    break;
                }
                else {
                    String arg = parsedInput[1];
                    if (arg.equals("--chunks")) {
                        controller.printChunkServerChunks();
                        break;
                    }
                    else if (arg.equals("--metadata")) {
                        controller.printChunkMetadata();
                        break;
                    }
                    System.err.println("Invalid argument: '" + arg + "'\nValid options are '--chunks | --metadata'");
                }
                break;
            default:
                System.out.println("Invalid CLI Input: " + input);
        }
    }

}
