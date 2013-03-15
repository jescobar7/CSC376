//Eddie Groberski & Juan Carlos Escobar
//CSC 376
//GameServer.java

import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class GameServer
{
    static Guess rootNode;
    static int nodeIdNumber;
    static GuessList guessList;
    static HashMap<Integer, Guess> nodeMap = new HashMap<Integer, Guess>();
    
    public static void main(String[] args) throws Exception
    {
        ServerSocket welcomeSocket = new ServerSocket(6001);
        System.out.println("Guessing Game Server Started");
        System.out.println();
        
       
        String saveFile = "saveFile";
        File file = new File(saveFile);
        RandomAccessFile raf;
        if (file.exists()){
            System.out.println(saveFile + " Found.");
            System.out.println(); 
            raf = new RandomAccessFile(file, "rws");
               
            int fileLength = (int)file.length();
            int filePointer;
            int nodeID;
            int leftChildID;
            int rightChildID;
            String nodeData;
            
            System.out.println("Loading Tree into Memory...");
            System.out.println(); 
            int countOfNodes = 0;
            while (raf.getFilePointer() < fileLength){
                //read bytes of node's file pointer
                filePointer = raf.readInt();
                System.out.println(); 
                System.out.println("filePointer: " + filePointer);
                
                //read bytes of node's id
                nodeID = raf.readInt();
                System.out.println("nodeID: " + nodeID);

                //read bytes of left child id
                leftChildID = raf.readInt();
                System.out.println("nodeID: " + leftChildID);

                //read bytes of right child id
                rightChildID = raf.readInt();
                System.out.println("nodeID: " + rightChildID);

                //read 88 bytes of node's data
                nodeData = raf.readUTF();

                
                //trim data of padding
                nodeData = nodeData.replace("*", "");
                System.out.println("nodeData: " + nodeData);

                System.out.println(); 
                
                //create node
                Guess node = new Guess(nodeID, leftChildID, null, rightChildID, null, nodeData, null);
                node.setFilePointer(filePointer);
                Integer intID = new Integer(nodeID);
                //put node in map
                nodeMap.put(intID,node);
                countOfNodes++;
                
                
            }
            
            
            System.out.println("Linking " + countOfNodes + " Node(s)...");
            System.out.println(); 
            //connect nodes together
            ArrayList<Guess> tmpNodeList = new ArrayList<Guess>(nodeMap.values());
            int counter = 1;
            for (Guess guessNode : tmpNodeList){          
                //hook up left child
                Integer leftNodeID = new Integer(guessNode.getLeftChildID());
                int lID = leftNodeID.intValue();
                if (lID !=0){
                    guessNode.setLeftChild(nodeMap.get(leftNodeID));
                }
                
                //hook up right child
                Integer rightNodeID = new Integer(guessNode.getRightChildID());
                int rID = rightNodeID.intValue();
                if (rID !=0){
                    guessNode.setRightChild(nodeMap.get(rightNodeID));    
                }
                counter++;
                if (counter == countOfNodes){
                    nodeIdNumber = guessNode.getNodeID() + 1;
                }
            }
            
            int root = 1;
            Integer nodeIDInt = new Integer(root);
            rootNode = nodeMap.get(nodeIDInt);
             
            guessList = new GuessList(raf); 
            System.out.println("Initialization Finished.");
            System.out.println();
        }else{
            System.out.println(saveFile + " Not Found!");
            System.out.println(); 
            raf = new RandomAccessFile(file, "rws");
            //creates beginning root node for first time
            rootNode = new Guess(1, 0, null, 0 ,null, "Obama", null);
            guessList = new GuessList(raf); 
            guessList.addFirstNode(rootNode);
            nodeIdNumber = 1;
            System.out.println("Wrote Beginning Node to Memory and " + saveFile + ".");
            System.out.println();
        }
        
        System.out.println("Awaiting Connections...");
        System.out.println();
        
        int clientID = 1;
        while(true) {
	    Socket connectionSocket = null;
	    try {
		connectionSocket = welcomeSocket.accept();
                System.out.println("New Connection accepted: ClientID: " + clientID);
                System.out.println();
                
                Runnable r = new GameServerConnection(connectionSocket, rootNode, clientID, nodeIdNumber, guessList);
                Thread t = new Thread(r);
                t.start();
                clientID++;
	    }
	    catch(IOException e) {
                e.printStackTrace();//System.err.println(e);
		if (connectionSocket != null)
		    connectionSocket.close();
	    }
        }
    }
    
    
   
}

    


            
