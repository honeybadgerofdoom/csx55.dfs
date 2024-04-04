package csx55.chord.cli;

import csx55.chord.Discovery;

import java.util.Scanner;

public class DiscoveryCLIManager implements Runnable, CLIManager {

    private Discovery node;

    public DiscoveryCLIManager(Discovery node) {
        this.node = node;
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
        switch (command) {
            case "peer-nodes":
                this.node.listPeerNodes();
                break;
            default:
                System.out.println("Invalid command to the Registry: " + command);
        }
    }

}
