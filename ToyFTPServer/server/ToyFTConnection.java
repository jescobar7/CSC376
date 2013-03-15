import java.io.*;
import java.net.*;

class ToyFTConnection implements Runnable 
{
    Socket sock;
    DataInputStream socketIn;
    DataOutputStream socketOut;

    public ToyFTConnection(Socket s) throws IOException // void
    {
        sock = s;
        socketIn =
            new DataInputStream(sock.getInputStream());
        socketOut =
            new DataOutputStream(sock.getOutputStream());
    }

    public void run()
    {
		System.setProperty("line.separator", "\r\n");

		byte success = 1;
		byte fail = 0;
		 
		int bytes = 4096;
		byte[] data = new byte[bytes]; //[4096];

		String filesInDirectory = "";

        try {
		    while (true)
			{
				String requestFile;
				boolean serverFlag = true;
				while(serverFlag)
				{
					requestFile = socketIn.readUTF();
					if (requestFile.equals("!"))
					{
						serverFlag = false;
						break;
					}
					if (requestFile.equals("*"))
					{
						serverFlag = true;
						filesInDirectory = directoryFiles();
						socketOut.writeUTF(filesInDirectory);
						socketOut.flush();
						//break;
					}
					else 
					{
						File sendFile = new File(requestFile);	
						if(sendFile.exists())	{
							System.out.println("Sending: " + requestFile);
					
							socketOut.writeByte(success);
							socketOut.writeLong(sendFile.length());
					
							FileInputStream fileStream = new FileInputStream(sendFile);
							long bytesRemaining = sendFile.length();
							while(bytesRemaining  > 0)	{
								int bytesRead = (int)Math.min(bytesRemaining, bytes);
								fileStream.read(data, 0, bytesRead);
								socketOut.write(data, 0, bytesRead);
								bytesRemaining -= bytesRead;
							}
					
							System.out.println("File Transfer Complete.");
							fileStream.close();
							socketOut.flush();
						}
						else
						{
							System.out.println("ERROR: Client requested invalid file.");
				
							socketOut.write(fail);
							socketOut.writeUTF("File Not Found");
							socketOut.flush();	
						}
					}
				}
				socketOut.close();
				socketIn.close();
				sock.close();
				System.out.println("Connection closed.\n");
			}
        }
        catch(IOException e) {
            System.err.println(e);
        }
        finally {
            try {
                sock.close();
            }
            catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    public static String directoryFiles()
	{
		String path = "."; 
	 
		String files = "";
		File folder = new File(path);
		File[] fileList = folder.listFiles(); 

 		String tmp = "No files to display";
		for (int i = 0; i < fileList.length; i++) 
		{
			if (fileList[i].isFile())
			{	
				tmp = fileList[i].getName();
				files = files + "\n" + tmp;
			}
		}
		return files;
    }    
}
