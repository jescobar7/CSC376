//Eddie Groberski & Juan Carlos Escobar
//CSC 376
//GameServerConnection.java

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class GameServerConnection implements Runnable {

    Socket connectionSocket;
    BufferedReader inFromClient;
    PrintWriter outToClient;
    PrintWriter notifyClient;
    String name;
    int timeout = 20000; // Socket timeout in millis
    Guess rootNode;
    int clientID;
    int nodeIdNumber;
    GuessList guessList;
    
   
    public GameServerConnection(Socket s, Guess inRootNode, int inClientID, int inNodeIdNumber, GuessList inGuessList) throws IOException {
        connectionSocket = s;
        inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));    
        outToClient =  new PrintWriter(new OutputStreamWriter(connectionSocket.getOutputStream()));
        rootNode = inRootNode;
        nodeIdNumber = inNodeIdNumber;
        clientID = inClientID;
        guessList = inGuessList; 
    }

	/** 
	 *	Telnet Backspace Usage Correction 
	 */
	public String checkString (String oldString) {
            StringBuffer strBuf = new StringBuffer("");
					
            for (int i = 0; i < oldString.length(); i++) {
                int charPos = (int)oldString.charAt(i);
                // Delete last character in StringBuffer when backspace detected
                if (i > 0 && charPos == 8) {
                    int bufLen = strBuf.length();
                    if (bufLen > 0){
                        strBuf.deleteCharAt(strBuf.length()-1);
                    }
                }
                // Append to StringBuffer, but ignore extra backspaces
                else {
                    if (oldString.charAt(i) != 8){
                        strBuf.append(oldString.charAt(i));
                    }
                }
             }
             String newString = strBuf.toString();

                    // DEBUG
                    /*
                    System.out.print("\nOld String: "+oldString+"\n");
                    System.out.print("StrBuf Len: "+strBuf.length()+"\n");
                    System.out.print("New String: "+newString+"\n");									
                    */

            return newString;	
	}
        
        
        public void notifyClient(Guess node) throws IOException{
            notifyClient =  new PrintWriter(new OutputStreamWriter(node.getClientSocket().getOutputStream()));
            notifyClient.println(name + " guessed " + node.getData());
            notifyClient.flush(); 
        }
        
        public boolean testIfQuestion(Guess node){ 
            if(node.getLeftChild() != null){
                return true;
            }else{   
                return false;
            }
        }
        
    public void run()
    {
        try {
            while(true) {
                //name of client
                outToClient.println("What is your name?");
                outToClient.flush();
                name = checkString(inFromClient.readLine());
                
                String saveFile = "saveFile";
       
                //while user doesnt quit
                String response = "";
                boolean gameActive = true;
                while(gameActive) {
                    connectionSocket.setSoTimeout(0); // infinite
                
                    //start the game
                    outToClient.println("Would you like to play a celebrity guessing game?");
                    outToClient.flush();
                    response = checkString(inFromClient.readLine());
                    
                    
                    boolean repeat = true;
                    while (repeat) {
                    	//user wishes to quit
		                if ( (response.equalsIgnoreCase("no")) || (response.equalsIgnoreCase("n")) ) {
                                    repeat = false;
                                    inFromClient.close();
		                    outToClient.close();
		                    connectionSocket.close();
		                    gameActive = false;
		                }
		                // User wishes to continue playing
		                else if ( (response.equalsIgnoreCase("yes")) || (response.equalsIgnoreCase("y")) ) {
                                    repeat = false;
		                }
		                // Invalid user response
		                else {
                                    outToClient.println("Please type Y or N");
                                    outToClient.flush();
                                    response = checkString(inFromClient.readLine());
		                }
		    		}

                    //traverse tree while node is a question
                    Guess currentNode = rootNode;
                    boolean traverseNodes = true;
                    while(traverseNodes) {
		                while (testIfQuestion(currentNode) == true){
		                    //ask question from node
		                    outToClient.println(currentNode.getData());
		                    outToClient.flush();
		                    response = checkString(inFromClient.readLine());

							// While user response is valid
							repeat = true;
							while (repeat) {
				                if ( (response.equalsIgnoreCase("yes")) || (response.equalsIgnoreCase("y")) ){
				                	repeat = false;
				                    currentNode = currentNode.getLeftChild();
				                }
				                else if ( (response.equalsIgnoreCase("no")) || (response.equalsIgnoreCase("n")) ){
				                	repeat = false;
				                    currentNode = currentNode.getRightChild();
				                }
				                // User response is invalid
				                else {
									outToClient.println("Please type Y or N");
									outToClient.flush();
									response = checkString(inFromClient.readLine());
				                }
				            } 
		                }
                    
						synchronized (currentNode) {
							connectionSocket.setSoTimeout(5000); // prevent one client thread from deadlocking program

							try {
						        if(testIfQuestion(currentNode) == false){    
									//ask question if it is this celebrity
									outToClient.println("Is your celebrity " + currentNode.getData() + "?");
									outToClient.flush();
									response = checkString(inFromClient.readLine());

								
									repeat = true;
									while (repeat) {
										if ( (response.equalsIgnoreCase("yes")) || (response.equalsIgnoreCase("y")) ){
											repeat = false;
										    outToClient.println("Yes! I'm pretty smart!");
										    outToClient.flush();
										    // Sends guess notification to client that created new celebrity if still online
										    try {
										    	// Check if client that created entry is still connected && prevent receiving self notification
									        	if ( (currentNode.getClientSocket() != null) && (connectionSocket.getPort() != currentNode.getClientSocket().getPort()) ) {
										        	notifyClient(currentNode);
									        	}
						                                    traverseNodes = false;
									        } catch (SocketException e) {
									        	//System.out.println();
									        	//System.err.println(e);
									        	System.out.println("Cannot send notification: Client has disconnected...");
									        }        
										}
										//Guess is not in tree and needs to be created
										else if ( (response.equalsIgnoreCase("no")) || (response.equalsIgnoreCase("n")) ){
										
											repeat = false;
											connectionSocket.setSoTimeout(timeout);
											
											try {
												//read new celebrity name
												outToClient.println("Who are you thinking of?");
												outToClient.flush();

												String newCelebName = checkString(inFromClient.readLine());

												//read new question
												long end = System.currentTimeMillis();
												outToClient.println("Give me a yes or no question to distinguish between " + newCelebName + " and " + currentNode.getData());
												outToClient.flush();
												String newQuestion = checkString(inFromClient.readLine());
												     
												outToClient.println("If someone answers yes, would that be " + newCelebName + "?");
												outToClient.flush();
												response = checkString(inFromClient.readLine());
												
												Guess newLeftNode = null;
												Guess newRightNode = null;

								                                /* START New Node Creation */
												repeat = true;
												while (repeat) {
								                                    String combineID;
								                                    Integer idInteger;
								                                    int id;
													if ( (response.equalsIgnoreCase("yes")) || (response.equalsIgnoreCase("y")) ){
														repeat = false;
								                                                
								                                                
								                                                //combine ids
								                                                nodeIdNumber++;
								                                                combineID = "" + clientID + nodeIdNumber;
								                                                idInteger = new Integer(combineID);
								                                                id = idInteger.intValue();
								                                                //create new left node(yes) with new celebrity
								                                                newLeftNode = new Guess(id, 0, null, 0, null, newCelebName, connectionSocket);
								                                           
								                                                
								                                             
								                                                //combine ids
								                                                nodeIdNumber++; 
								                                                combineID = "" + clientID + nodeIdNumber;
								                                                idInteger = new Integer(combineID);
								                                                id = idInteger.intValue();                                                            
														//create new right node(no) with information of current node
														newRightNode = new Guess(id, 0, null, 0, null, currentNode.getData(), connectionSocket);
								                                                
								                                                
								                                         
								                                          
													}
													else if ( (response.equalsIgnoreCase("no")) || (response.equalsIgnoreCase("n")) ){
														repeat = false;
								                                                
								                                                //combine ids
								                                                nodeIdNumber++; 
								                                                combineID = "" + clientID + nodeIdNumber;
								                                                idInteger = new Integer(combineID);
								                                                id = idInteger.intValue();
														//create new left node(yes) with information of current node
														newLeftNode = new Guess(id, 0, null, 0, null, currentNode.getData(), connectionSocket);
								                                               
								                                                
								                                                
								                                                
								                                                //combine ids
								                                                nodeIdNumber++;
								                                                combineID = "" + clientID + nodeIdNumber;
								                                                idInteger = new Integer(combineID);
								                                                id = idInteger.intValue();  
														//create new right node(no) with new celebrity
														newRightNode = new Guess(id, 0, null, 0, null, newCelebName, connectionSocket);
								                                       
								                                                
								                                                
													}
													else {
								                                                outToClient.println("Please type Y or N");
								                                                outToClient.flush();
								                                                response = checkString(inFromClient.readLine());
													}
												}
								                                //set current node new left node id
												currentNode.setLeftChildID(newLeftNode.getNodeID());                                               
								                                //set current node new left node
												currentNode.setLeftChild(newLeftNode);
								                                //set current node new right node id
												currentNode.setRightChildID(newRightNode.getNodeID());
												//set current node new right node
												currentNode.setRightChild(newRightNode);
												//set current node new data to question
												currentNode.setData(newQuestion);
												outToClient.println("Thank you for adding " + newCelebName + " to the game.");
												outToClient.flush();
												/* END New Node Creation */
								                                                                   
								                                guessList.addNodes(currentNode, newLeftNode, newRightNode);
						                                                traverseNodes = false;
											}
											catch (InterruptedIOException e) {
												outToClient.println("Timeout Occurred! Starting over.");
												System.err.println(e);
												System.out.println("Dropped client from locked creation node...");
												traverseNodes = false;
											}
										}
										else {
											outToClient.println("Please type Y or N");
											outToClient.flush();
											response = checkString(inFromClient.readLine());
										}
									}
		                        }
		                    }
		                    // synchronized thread timeout
		                    catch (InterruptedIOException e) {
		                    	outToClient.println("Timeout Occurred! Starting over.");
								System.err.println(e);
								System.out.println("Dropped client from locked question node...");
								traverseNodes = false;	
		                    }
		            	} // synchronized end block bracket
            		}  
                }
        }
        }
        catch(IOException e) {
            System.err.println(e);
        }
        finally {
            try {
                connectionSocket.close();
            }
            catch (IOException e) {
                System.err.println(e);
            }
        }
    }
}

