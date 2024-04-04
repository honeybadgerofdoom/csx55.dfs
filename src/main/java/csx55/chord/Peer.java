package csx55.chord;

import csx55.chord.cli.PeerCLIManager;
import csx55.chord.node.Node;
import csx55.chord.node.PeerRef;
import csx55.chord.util.FingerTable;
import csx55.chord.node.PeerInfo;
import csx55.chord.wireformats.*;
import csx55.chord.transport.TCPReceiverThread;
import csx55.chord.transport.TCPSender;

import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Peer implements Node {

    private ServerSocket serverSocket;
    private String ipAddress;
    private int portNumber;
    private int id;
    private final String registryIpAddress;
    private final int registryPortNumber;
    private Socket socketToRegistry;
    private PeerInfo predecessor;
    private FingerTable fingerTable;
    private String filepath;

    public Peer(String registryIpAddress, int registryPortNumber) {
        this.registryIpAddress = registryIpAddress;
        this.registryPortNumber = registryPortNumber;
    }

    public void doWork() {
        assignIpAddress();
        assignServerSocketAndPort();
        createDirectory();
        startTCPServerThread();
        connectToRegistry();
        registerSelf();
        manageCLI();
    }

    private void createDirectory() {
        try {
            this.filepath = "/tmp/" + id + "/";
            Files.createDirectories(Paths.get(filepath));
        } catch (IOException e) {
            System.err.println("Failed to create temp directory " + e);
        }
    }

    private void assignIpAddress() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            this.ipAddress = addr.getHostAddress();
        } catch (UnknownHostException e) {
            System.err.println("ERROR Failed to get MessagingNode IP Address...\n" + e);
        }
    }

    private void assignServerSocketAndPort() {
        try {
            this.serverSocket = new ServerSocket(0);
            this.portNumber = this.serverSocket.getLocalPort();
            String ipAndPort = this.ipAddress + ":" + this.portNumber;
            this.id = ipAndPort.hashCode();
            this.fingerTable = new FingerTable(this);
        } catch (IOException e) {
            System.err.println("ERROR Failed to create ServerSocket...\n" + e);
        }
    }

    private void connectToRegistry() {
        try {
            this.socketToRegistry = new Socket(registryIpAddress, registryPortNumber);
            TCPReceiverThread tcpReceiverThread = new TCPReceiverThread(this, this.socketToRegistry);
            Thread thread = new Thread(tcpReceiverThread);
            thread.start();
        } catch (IOException e) {
            System.err.println("ERROR Failed to connect to Registry " + e);
        }
    }

    private void registerSelf() {
        try {
            TCPSender tcpSender = new TCPSender(this.socketToRegistry);
            RegisterRequest registerRequest = new RegisterRequest(new PeerInfo(this));
            byte[] bytes = registerRequest.getBytes();
            tcpSender.sendData(bytes);
        } catch (IOException e) {
            System.err.println("ERROR Trying to register self " + e);
        }
    }

    public void manageCLI() {
        PeerCLIManager cliManager = new PeerCLIManager(this);
        Thread thread = new Thread(cliManager);
        thread.start();
    }

    public PeerInfo getPredecessor() {
        return predecessor;
    }

    public int getPortNumber() {
        return this.portNumber;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public ServerSocket getServerSocket() {
        return this.serverSocket;
    }

    public int getId() {
        return this.id;
    }

    public void onEvent(Event event, Socket socket) {
        if (event != null) {
            int type = event.getType();
            switch (type) {
                case Protocol.REGISTER_RESPONSE:
                    handleRegisterResponse((RegisterResponse) event);
                    break;
                case Protocol.DEREGISTER_RESPONSE:
                    handleDeregisterResponse((DeregisterResponse) event);
                    break;
                case Protocol.NEW_NODE_REQUEST:
                    handleNewNodeRequest((NewNodeRequest) event);
                    break;
                case Protocol.DETAILS_FOR_NEW_NODE:
                    handleDetailsForNewNode((DetailsForNewNode) event);
                    break;
                case Protocol.UPDATE_FINGER_TABLE_JOIN:
                    handleUpdateFingerTableOnJoin((UpdateFingerTableOnJoin) event);
                    break;
                case Protocol.UPDATE_FINGER_TABLE_LEAVE:
                    handleUpdateFingerTableOnLeave((UpdateFingerTableOnLeave) event);
                    break;
                case Protocol.UPLOAD_FILE:
                    handleUploadFile((UploadFile) event);
                    break;
                case Protocol.DOWNLOAD_FILE:
                    handleDownloadFile((DownloadFile) event);
                    break;
                case Protocol.DOWNLOAD_DELIVERY:
                    handleDownloadDelivery((DownloadDelivery) event);
                    break;
                case Protocol.POKE:
                    handlePoke((Poke) event);
                    break;
                default:
                    System.out.println("onEvent couldn't handle event type " + type);
            }
        }
    }

    private void handleRegisterResponse(RegisterResponse registerResponse) {
        String info = registerResponse.getInfo();
        System.out.println(info);
        int statusCode = registerResponse.getStatusCode();
        if (statusCode == Protocol.FAILURE) {
            this.id = registerResponse.getHashedId();
        }
        PeerInfo randomPeerInfo = registerResponse.getPeerNodeInfo();
        if (randomPeerInfo.getId() == this.id) {
            this.predecessor = new PeerInfo(this);
            fingerTable.update();
        }
        else {
            sendFindSuccessorMessage(randomPeerInfo);
        }
    }

    private void handleDetailsForNewNode(DetailsForNewNode detailsForNewNode) {
        this.predecessor = detailsForNewNode.getPredecessorInfo();
        fingerTable.update(detailsForNewNode);
        sendUpdateFingerTableAfterJoining();
    }

    private void sendUpdateFingerTableAfterJoining() {
        PeerInfo myPeerInfo = new PeerInfo(this);
        UpdateFingerTableOnJoin updateFingerTableOnJoin = new UpdateFingerTableOnJoin(myPeerInfo);
        fingerTable.sendToSuccessor(updateFingerTableOnJoin);
    }

    private void handleUpdateFingerTableOnJoin(UpdateFingerTableOnJoin updateFingerTableOnJoin) {
        if (updateFingerTableOnJoin.getPeerInfo().getId() != id) {
            fingerTable.update(updateFingerTableOnJoin);
            fingerTable.sendToSuccessor(updateFingerTableOnJoin);
            checkForFileRedistribution(updateFingerTableOnJoin);
        }
    }

    private void checkForFileRedistribution(UpdateFingerTableOnJoin updateFingerTableOnJoin) {
        if (updateFingerTableOnJoin.getPeerInfo().getId() == predecessor.getId()) {
            File[] listOfFiles = getMyFiles();
            for (File file : listOfFiles) {
                int hash = file.getName().hashCode();
                if (!fingerTable.isBetween(predecessor.getId(), id, hash)) {
                    upload(filepath + file.getName());
                    File fileObj = new File(filepath + file.getName());
                    if (!fileObj.delete()) {
                        System.out.println("Failed to delete '" + fileObj.getName() + "'");
                    }
                }
            }
        }
    }

    private File[] getMyFiles() {
        File folder = new File(filepath);
        return folder.listFiles();
    }

    private boolean fileIsStored(String name) {
        for (File file : getMyFiles()) {
            if (file.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private void handleUpdateFingerTableOnLeave(UpdateFingerTableOnLeave updateFingerTableOnleave) {
        if (updateFingerTableOnleave.getPeerInfo().getId() != id) {
            if (updateFingerTableOnleave.getPeerInfo().getId() == predecessor.getId()) {
                this.predecessor = updateFingerTableOnleave.getPredecessorInfo();
            }
            fingerTable.update(updateFingerTableOnleave);
            fingerTable.sendToSuccessor(updateFingerTableOnleave);
        }
    }

    private void handleNewNodeRequest(NewNodeRequest newNodeRequest) {
        PeerInfo peerInfo = newNodeRequest.getPeerNodeInfo();
        if (fingerTable.isBetween(predecessor.getId(), id, peerInfo.getId())) {
            // I'm the successor of the new node
            PeerInfo successorInfo = new PeerInfo(this);
            DetailsForNewNode detailsForNewNode = new DetailsForNewNode(predecessor, successorInfo, fingerTable);
            this.predecessor = peerInfo; // Set my new predecessor!
            try {
                byte[] bytes = detailsForNewNode.getBytes();
                Socket socket = new Socket(peerInfo.getIpAddress(), peerInfo.getPortNumber());
                TCPSender sender = new TCPSender(socket);
                sender.sendData(bytes);
            } catch (IOException e) {
                System.err.println("Failed to send DetailsForNewNode");
            }
        }
        else {
            // Get the message close to the successor
            PeerRef next = fingerTable.findSuccessorOfTarget(peerInfo.getId());
            next.writeToSocket(newNodeRequest);
        }
    }

    private void sendFindSuccessorMessage(PeerInfo peerInfo) {
        PeerInfo myNodeInfo = new PeerInfo(this);
        NewNodeRequest newNodeRequest = new NewNodeRequest(myNodeInfo);
        try {
            Socket socket = new Socket(peerInfo.getIpAddress(), peerInfo.getPortNumber());
            TCPSender sender = new TCPSender(socket);
            byte[] bytes = newNodeRequest.getBytes();
            sender.sendData(bytes);
        } catch (IOException e) {
            System.err.println("Failed to contact random peer for FindSuccess " + e);
        }
    }

    private void handlePoke(Poke poke) {
        if (poke.getPeerInfo().getId() == id) {
            System.out.println("Got my poke message back!");
        }
        else {
            System.out.println("Received poke from " + poke.getPeerInfo());
            fingerTable.sendToSuccessor(poke);
        }
    }

    private void handleDeregisterResponse(DeregisterResponse deregisterResponse) {
        int statusCode = deregisterResponse.getStatusCode();
        if (statusCode == Protocol.SUCCESS) {
            PeerInfo myPeerInfo = new PeerInfo(this);
            PeerInfo mySuccessor = new PeerInfo(fingerTable.getSuccessorPeer());
            UpdateFingerTableOnLeave updateFingerTable = new UpdateFingerTableOnLeave(myPeerInfo, mySuccessor, predecessor);
            fingerTable.sendToSuccessor(updateFingerTable);
            // Send my files to my successor
            System.out.println("Waiting for finger table updates...");
            try {
                // Wait for my last message to send
                Thread.sleep(1000);
            } catch (InterruptedException ignored) { }
            System.out.println("Sending my files to my successor...");
            for (File file : getMyFiles()) {
                try {
                    byte[] currentFile = Files.readAllBytes(Paths.get(filepath + file.getName()));
                    int hash = file.getName().hashCode();
                    UploadFile uploadFile = new UploadFile(hash, file.getName(), currentFile);
                    fingerTable.sendToSuccessor(uploadFile);
                } catch (IOException e) {
                    System.err.println("Failed to send file to successor");
                }
            }
            System.out.println("Exiting");
            System.exit(1);
        }
        else {
            System.out.println("Deregister Failed.");
        }
    }

    private void handleUploadFile(UploadFile uploadFile) {
        int hashed = uploadFile.getHash();
        String filename = uploadFile.getFilename();
        byte[] file = uploadFile.getFile();
        PeerRef target = fingerTable.findSuccessorOfTarget(hashed);
        if (target.getId() == id) {
            writeToFile(filename, file);
        }
        else {
            target.writeToSocket(uploadFile);
        }
    }

    private void handleDownloadFile(DownloadFile downloadFile) {
        downloadFile.updateHopPath(id);
        int hashed = downloadFile.getHash();
        PeerRef target = fingerTable.findSuccessorOfTarget(hashed);
        if (target.getId() == id) {
            byte[] file = new byte[1];
            boolean success = fileIsStored(downloadFile.getFilename());
            try {
                if (success) {
                    file = Files.readAllBytes(Paths.get(filepath + downloadFile.getFilename()));
                }
            } catch (IOException e) {
                System.err.println("ERROR Failed to read file bytes " + e);
            } finally {
                PeerRef client = new PeerRef(downloadFile.getClient());
                DownloadDelivery downloadDelivery = new DownloadDelivery(file, downloadFile.getHopPath(), downloadFile.getFilename(), success);
                client.writeToSocket(downloadDelivery);
            }
        }
        else {
            target.writeToSocket(downloadFile);
        }
    }

    private void handleDownloadDelivery(DownloadDelivery downloadDelivery) {
        if (!downloadDelivery.getSuccess()) {
            System.err.println("File '" + downloadDelivery.getFilename() + "' does not exist.");
        }
        else {
            System.out.println(downloadDelivery.getHopPath());
            writeToFile(downloadDelivery.getFilename(), downloadDelivery.getFile(), System.getProperty("user.dir"));
        }
    }

    public void upload(String filepath) {
        String[] parts = filepath.split("/");
        String filename = parts[parts.length - 1];
        int hashed = filename.hashCode();
        try {
            byte[] file = Files.readAllBytes(Paths.get(filepath));
            UploadFile uploadFile = new UploadFile(hashed, filename, file);
            handleUploadFile(uploadFile);
        } catch (IOException e) {
            System.err.println("'" + filepath + "' does not exist.");
        }
    }

    public void download(String filepath) {
        String[] parts = filepath.split("/");
        String filename = parts[parts.length - 1];
        int hashed = filename.hashCode();
        PeerRef target = fingerTable.findSuccessorOfTarget(hashed);
        if (target.getId() == id) {
            try {
                byte[] file = Files.readAllBytes(Paths.get(filepath));
                writeToFile(filename, file, System.getProperty("user.dir"));
            } catch (IOException e) {
                System.err.println("'" + filepath + "' does not exist.");
            }
        }
        else {
            DownloadFile downloadFile = new DownloadFile(new PeerInfo(this), filename, id, hashed);
            target.writeToSocket(downloadFile);
        }
    }

    private void writeToFile(String filename, byte[] file) {
        write(file, filepath + filename);
    }

    private void writeToFile(String filename, byte[] file, String filepath) {
        write(file, filepath + "/" + filename);
    }

    private void write(byte[] file, String writePath) {
        File outputFile = new File(writePath);
        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            outputStream.write(file);
        } catch (IOException e) {
            System.err.println("Failed to write file to disc " + e);
        }
    }

    public void printFingerTable() {
        System.out.println(this.fingerTable.toFormatString());
    }

    public void exit() {
        PeerInfo myPeerInfo = new PeerInfo(this);
        try {
            TCPSender tcpSender = new TCPSender(this.socketToRegistry);
            DeregisterRequest deregisterRequest = new DeregisterRequest(myPeerInfo);
            byte[] bytes = deregisterRequest.getBytes();
            tcpSender.sendData(bytes);
        } catch (IOException e) {
            System.err.println("ERROR Trying to register self " + e);
        }
    }

    public void files() {
        File[] files = getMyFiles();
        for (File file : files) {
            System.out.println(file.getName() + " " + file.getName().hashCode());
        }
    }

    private String getFileString() {
        String filesString = "Files: [";
        File[] files = getMyFiles();
        for (File file : files) {
            filesString += file.getName() + ", ";
        }
        if (files.length > 0) filesString = filesString.substring(0, filesString.length() - 2);
        filesString += "]";
        return filesString;
    }

    public void poke() {
        Poke poke = new Poke(new PeerInfo(this));
        fingerTable.sendToSuccessor(poke);
    }

    public void neighbors() {
        System.out.println("predecessor: " + predecessor);
        System.out.println("successor: " + fingerTable.getSuccessorString());
    }

    @Override
    public String toString() {
        String filesString = getFileString();
        return "My ID: " + id + "\nPredecessor: " + predecessor + "\nSuccessor: " + fingerTable.getSuccessorString() + "\n" + fingerTable + filesString;
    }

    public static void main(String[] args) {
        if (args.length == 2) {
            String registryIpAddress = args[0];
            int registryPortNumber = Integer.parseInt(args[1]);
            Peer node = new Peer(registryIpAddress, registryPortNumber);
            node.doWork();
        }
        else {
            System.err.println("Invalid Usage. Provide IP/Port of Registry");
        }
    }

}