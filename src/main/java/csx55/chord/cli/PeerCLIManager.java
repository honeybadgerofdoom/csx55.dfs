package csx55.chord.cli;

import csx55.chord.Peer;

import java.util.Scanner;

public class PeerCLIManager implements Runnable, CLIManager {

    private Peer node;

    public PeerCLIManager(Peer node) {
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
        switch(command) {
            case "neighbors":
                node.neighbors();
                break;
            case "files":
                node.files();
                break;
            case "finger-table":
                node.printFingerTable();
                break;
            case "upload":
                if (parsedInput.length == 2) {
                    String filepath = parsedInput[1];
                    node.upload(filepath);
                }
                else System.err.println("Invalid usage. Please provide a file path. EX: upload desktop/input/test.txt");
                break;
            case "download":
                if (parsedInput.length == 2) {
                    String filepath = parsedInput[1];
                    node.download(filepath);
                }
                else System.err.println("Invalid usage. Please provide a file path. EX: upload desktop/input/test.txt");
                break;
            case "exit":
                node.exit();
                break;
            case "poke":
                node.poke();
                break;
            case "self":
                System.out.println(node);
                break;
            default:
                System.out.println("Invalid CLI Input: " + input);
        }
    }

}
