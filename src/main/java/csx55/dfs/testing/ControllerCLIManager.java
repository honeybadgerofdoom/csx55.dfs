package csx55.dfs.testing;

import csx55.dfs.replication.Controller;

import java.util.Scanner;

public class ControllerCLIManager implements Runnable {

    private Controller controller;

    public ControllerCLIManager(Controller controller) {
        this.controller = controller;
    }

    public void run() {
        String input = "";
        Scanner sc = new Scanner(System.in);
        while (!(input = sc.nextLine()).equals("quit")) {
            processInput(input);
        }
        sc.close();
        System.exit(0);
    }

    public void processInput(String input) {
        String[] parsedInput = input.split(" ");
        String command = parsedInput[0];
        switch(command) {
            case "poke":
                controller.pokeChunkServers();
                break;
            default:
                System.out.println("Invalid CLI Input: " + input);
        }
    }

}
