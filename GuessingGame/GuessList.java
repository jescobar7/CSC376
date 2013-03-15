//Eddie Groberski & Juan Carlos Escobar
//CSC 376
//GuessList.java

import java.util.LinkedList;
import java.io.*;

public class GuessList implements Serializable {
    private LinkedList<Guess> list;
    private final RandomAccessFile save;
    
    
    public GuessList (RandomAccessFile raf) throws Exception {
        list = new LinkedList<Guess>();
        save = raf;
    }
    
    
    public static String rightPad(String s, int width) {
        return String.format("%-" + width + "s", s).replace(' ', '*');
    }
    
    public static int byteCount(String data) throws UnsupportedEncodingException{
        return data.getBytes("utf8").length;
    }
    
    public void addFirstNode(Guess node) throws IOException{

        int nodeDataLength = byteCount(node.getData());
        String nodeData = null;
        int pad = 0;
        String padString = null;
        //pad * to data for writing
        if (nodeDataLength < 82){
            pad = 82 - nodeDataLength;
            padString = rightPad("",pad);
            nodeData = node.getData() + padString;   
        }else{
            nodeData = node.getData();
        }
        int filePointer = 0;
        //save file pointer to memory
        node.setFilePointer(filePointer); 
        
        //write 4 bytes of file pointer
        save.writeInt(filePointer);
        //write 4 bytes of ID
        save.writeInt(node.getNodeID());
        //write 4 bytes of left child ID
        save.writeInt(node.getLeftChildID());
        //write 4 bytes of right child ID
        save.writeInt(node.getRightChildID());
        //write 88 bytes of data
        save.writeUTF(nodeData); 
        System.out.println("node: \n" + node);
    }
    
   
    public void addNodes(Guess currentNode, Guess leftNode, Guess rightNode) throws IOException  {
        //RE-WRITE CURRENT NODE
        int currentNodeDataLength = byteCount(currentNode.getData());
        String currentNodeData = null;
        int currentNodePad = 0;
        String padString = null;
        //pad * to data for writing
        if (currentNodeDataLength < 82){
            currentNodePad = 82 - currentNodeDataLength;
            padString = rightPad("",currentNodePad);
            currentNodeData = currentNode.getData() + padString;   
        }else{
            currentNodeData = currentNode.getData();
        }
        save.seek(currentNode.getFilePointer());
        //write 4 bytes of file pointer
        save.writeInt(currentNode.getFilePointer());
        //write 4 bytes of ID
        save.writeInt(currentNode.getNodeID());
        //write 4 bytes of left child ID
        save.writeInt(currentNode.getLeftChildID());
        //write 4 bytes of right child ID
        save.writeInt(currentNode.getRightChildID());
        //write 88 bytes of data
        save.writeUTF(currentNodeData); 
        System.out.println("Wrote currentNode: \n" + currentNode);
        
        //WRITE LEFT NODE
        save.seek((int)save.length());
        int leftNodeDataLength = byteCount(leftNode.getData());
        String leftData = null;
        int leftNodePad = 0;
        String leftNodePadString = null;
        //pad * to data for writing
        if (leftNodeDataLength < 82){
            leftNodePad = 82 - leftNodeDataLength;
            leftNodePadString = rightPad("",leftNodePad);
            leftData = leftNode.getData() + leftNodePadString;   
        }else{
            leftData = leftNode.getData();
        }
        int leftFilePointer = (int)save.getFilePointer();
        //save file pointer to memory
        leftNode.setFilePointer(leftFilePointer); 
        //write 4 bytes of file pointer
        save.writeInt(leftFilePointer);
        //write 4 bytes of ID
        save.writeInt(leftNode.getNodeID());
        //write 4 bytes of left child ID
        save.writeInt(leftNode.getLeftChildID());
        //write 4 bytes of right child ID
        save.writeInt(leftNode.getRightChildID());
        //write 88 bytes of data
        save.writeUTF(leftData); 
        System.out.println("Wrote leftNode: \n" + leftNode);
        
        
        //WRITE RIGHT NODE
        //save.seek((int)save.length());
        int rightNodeDataLength = byteCount(rightNode.getData());
        String rightData = null;
        int rightNodePad = 0;
        String rightNodePadString = null;
        //pad * to data for writing
        if (rightNodeDataLength < 82){
            rightNodePad = 82 - rightNodeDataLength;
            rightNodePadString = rightPad("",rightNodePad);
            rightData = rightNode.getData() + rightNodePadString;   
        }else{
            rightData = rightNode.getData();
        }
        int rightFilePointer = (int)save.getFilePointer();
        //save file pointer to memory
        rightNode.setFilePointer(rightFilePointer); 
        //write 4 bytes of file pointer
        save.writeInt(rightFilePointer);
        //write 4 bytes of ID
        save.writeInt(rightNode.getNodeID());
        //write 4 bytes of left child ID
        save.writeInt(rightNode.getLeftChildID());
        //write 4 bytes of right child ID
        save.writeInt(rightNode.getRightChildID());
        //write 88 bytes of data
        save.writeUTF(rightData); 
        System.out.println("Wrote rightNode: \n" + rightNode); 
    }
   
    
 
    
   
    
}
