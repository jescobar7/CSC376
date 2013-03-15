//Eddie Groberski & Juan Carlos Escobar
//CSC 376
//Guess.java

import java.util.Random;
import java.io.Serializable;
import java.net.*;
import java.io.*;
import java.net.*;

public class Guess{
    //attributes that will be serialized
    int filePointer;
    int nodeID;
    int leftChildID;
    int rightChildID;
    private String data;
    
    //attributes that are only saved to memory
    private Guess leftChild;
    private Guess rightChild;
    Socket clientSocket;
    
    
    public Guess(int nodeID, int leftChildID, Guess leftChild, int rightChildID, Guess rightChild, String data, Socket clientSocket) {
        this.nodeID = nodeID;
        this.leftChildID = leftChildID;
        this.rightChildID = rightChildID;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.data = data;
        this.clientSocket = clientSocket;
    }
    //node ID
    public int getNodeID(){
        return nodeID;
    }
    
    //child IDs
    public void setLeftChildID(int id){
        leftChildID = id;
    }
    
    public void setRightChildID(int id){
        rightChildID = id;
    }
    
    public int getLeftChildID(){
        return leftChildID;
    }
    
    public int getRightChildID(){
        return rightChildID;
    }
    
    
    
    //child objects
    public void setLeftChild(Guess child){
        leftChild = child;
    }
    
    public void setRightChild(Guess child){
        rightChild = child;
    }
    
    public Guess getLeftChild() {
        return leftChild;
    }
    
    public Guess getRightChild() {
        return rightChild;
    }
    
    
    //file pointer
    public void setFilePointer(int pos){
        filePointer = pos;
    }
    
    public int getFilePointer() {
        return filePointer;
    }
    
    
    //node data
    public void setData(String newData){
        data = newData;
    }
    
    public String getData(){
        return data;
    }
       
    
    
    //node creator socket
    public Socket getClientSocket(){
        return clientSocket;
    }

    
    @Override
    public String toString() {
        String nodeInfo = "File Pointer Pos: "+ getFilePointer() + "\n" 
                         +"ID: "+ getNodeID() +"\n"
                         +"Left Child ID: "+ getLeftChildID() + "\n"                          
                         +"Right Child ID: "+ getRightChildID() + "\n"          
                         +"Data: "+ getData() +"\n";    
        return nodeInfo;
    }   
}
