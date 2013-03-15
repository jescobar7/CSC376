import java.io.*;
import java.net.*;

public class ToyFTServer
{
	public static void main(String argv[]) throws Exception
    {
        ServerSocket welcomeSocket = new ServerSocket(8000);
        System.out.println("Server is running...");

        while(true) 
        {
			Socket connectionSocket = null;
			try 
			{
				connectionSocket = welcomeSocket.accept();
				System.out.println("\nConnection accepted!");
				
		        Runnable r = new ToyFTConnection(connectionSocket);
		        Thread t = new Thread(r);
		        t.start();
			}
			catch(IOException e) 
			{
		            System.err.println(e);
			if (connectionSocket != null)
				connectionSocket.close();
			}
        }
    }
}
