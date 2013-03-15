import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;

public class ToyFTClient
{
	public static void main(String[] args) throws Exception
	{

		Socket sock = new Socket("localhost", 8000);
		
		DataInputStream socketIn = new DataInputStream(new BufferedInputStream(
				sock.getInputStream()));
		DataOutputStream socketOut = new DataOutputStream(new BufferedOutputStream(
				sock.getOutputStream()));

		BufferedReader consoleIn = new BufferedReader(new InputStreamReader(
				System.in));
		
		byte success = 1;
		byte fail = 0;
		
		int bytes = 4096;
		byte[] data = new byte[bytes];

		File file;
		String requestFile;
		boolean serverFlag = true;
		
		while(serverFlag)//!(requestFile = consoleIn.readLine()).equals("!"))
		{
			System.out.print("\nWhat file do you want? ");
			
			requestFile = consoleIn.readLine();
			if (requestFile.equals("!"))
			{
				serverFlag = false;
				socketOut.writeUTF("!");
				socketOut.flush();
				break;
			}
			if (requestFile.equals("*"))
			{
				socketOut.writeUTF("*");
				socketOut.flush();
				System.out.println("\nFile(s) in Directory:");
				System.out.println(socketIn.readUTF());
				//break;
			}
			else 
			{
				socketOut.writeUTF(requestFile);
				socketOut.flush();
			
			
				if(socketIn.readByte() == success) 
				{
					file = new File(requestFile);
					FileOutputStream fileOutput = new FileOutputStream(file);
					long bytesRemaining = socketIn.readLong();
				
					while(bytesRemaining > 0)
					{
						int bytesRead = (int)Math.min(bytesRemaining, bytes);
						socketIn.read(data, 0, bytesRead);
						fileOutput.write(data, 0, bytesRead);
						bytesRemaining -= bytesRead;
					}
				
					System.out.println("File Transfer Complete.");
					fileOutput.close();
				} 
				else {
					System.out.print("ERROR: " + socketIn.readUTF() + "\n");
				}
		  	}
		}
		System.out.println("Connection closed.");
		
		socketOut.close();
		socketIn.close();
		sock.close();
	}
}
