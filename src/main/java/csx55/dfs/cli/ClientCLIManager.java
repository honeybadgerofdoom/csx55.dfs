package csx55.dfs.cli;

import csx55.dfs.replication.Client;

import java.util.Scanner;


/*
Manage the CLI for a Client
 */
public class ClientCLIManager implements Runnable {

    private Client client;

    public ClientCLIManager(Client client) {
        this.client = client;
    }


    /*
    This thread's run() method reads from a Scanner object listening on System.in
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
    Processing CLI input on carriage return
     */
    public void processInput(String input) {
        String[] parsedInput = input.split(" ");
        String command = parsedInput[0];
        switch(command) {
            case "upload":
                if (parsedInput.length == 3) {
                    String source = parsedInput[1];
                    String destination = parsedInput[2];
                    client.upload(source, destination);
                }
                else System.err.println("Invalid usage. Please provide a source and destination. EX: upload /desktop/input/test.txt /desktop/downloads/test.txt");
                break;
            case "download":
                if (parsedInput.length == 3) {
                    String source = parsedInput[1];
                    String destination = parsedInput[2];
                    client.download(source, destination);
                }
                else System.err.println("Invalid usage. Please provide a source and destination. EX: upload /desktop/input/test.txt /desktop/downloads/test.txt");
                break;
            default:
                System.out.println("Invalid CLI Input: " + input);
        }
    }

}
