
import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.*;

public class FTPServer {
	public static void main(String argv[]) throws Exception {
		String receivedUsername; 
		String receivedPassword;
		String capitalizedSentence; 

		
		String serverUsername = "tmac";
		serverUsername = serverUsername.replaceAll("\\s+","");

		String serverPassword = "password";
		serverPassword = serverPassword.replaceAll("\\s+","");
		
		ServerSocket welcomeSocket = new ServerSocket(6790);
		
		while(true) {
			Socket connectionSocket = welcomeSocket.accept();
			
			
			
			BufferedReader inFromClient = new BufferedReader (new InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			
			receivedUsername = inFromClient.readLine();
			receivedPassword = inFromClient.readLine();
			
				
			
			while (!(receivedUsername.equals(serverUsername) && receivedPassword.equals(serverPassword))) {
				outToClient.writeBytes("Unauthenticated!\n");
				receivedUsername = inFromClient.readLine();
				receivedPassword = inFromClient.readLine();
				
			}
				if(receivedUsername.equals(serverUsername) && receivedPassword.equals(serverPassword)) {
				outToClient.writeBytes("Authenticated!\n");
				File currentDirectory = new File(".");
				while (true) {
					String command = inFromClient.readLine();
					if (command.equals("1")) {
						ListDirectory(currentDirectory, outToClient);
					}
					else if (command.equals("2")) {
						String newDirectoryPath = UpdateDirectory(currentDirectory, inFromClient, outToClient);
						// Update the currentDirectory to the new Directory
						currentDirectory = new File(newDirectoryPath);

					}
					
					else if (command.equals("3")) {
						String fileName = inFromClient.readLine();

						saveFile(fileName, connectionSocket, outToClient );
					}
					else if (command.equals("4")) {
						String fileName = inFromClient.readLine();
						
						sendFile(connectionSocket, fileName);
					}
					else if (command.equals("5")) {
						connectionSocket.close();
						break;
					}
				}
				
				
			}
			
			
			
//			capitalizedSentence = receivedUsername.toUpperCase() + '\n';
//			
//			outToClient.writeBytes(capitalizedSentence);
		}
	}
	
	public static void ListDirectory(File currentDirectory, DataOutputStream outToClient) throws IOException {
		File[] filesInDirectory = currentDirectory.listFiles();
		System.out.print(filesInDirectory);
		for(File file: filesInDirectory) {
			if (file.isDirectory()) {
				outToClient.writeBytes("directory:");
			} else {
				outToClient.writeBytes("     file:");
			}
			outToClient.writeBytes(file.getCanonicalPath() + ",");
//			if(file.isFile()) 
//				outToClient.write((file.getName() + ":").getBytes());
		}
		outToClient.writeBytes("\n");
	}
	
	public static String UpdateDirectory(File currentDirectory, BufferedReader inFromClient, DataOutputStream outToClient) throws IOException {
		String cDirectoryPath = currentDirectory.getAbsolutePath();
		outToClient.writeBytes(cDirectoryPath + "\n");
		String newDirectoryPath = inFromClient.readLine();
		System.setProperty("user.dir", newDirectoryPath);
		
		outToClient.writeBytes("You changed your path to: " + newDirectoryPath + "\n");
		
		
		
		//return new path
		return newDirectoryPath;
		
		
	}
	
	public static void saveFile(String file, Socket s, DataOutputStream outToClient) throws IOException {
		final int FILE_SIZE = 6022386;
		int bytesRead; 
		int current = 0; 
		FileOutputStream fos = null; 
		BufferedOutputStream bos = null; 
		String FILE_TO_RECEIVED = "C:\\Users\\Thomas McFann\\eclipse-workspace\\CSCI361MiniProject2\\Server\\" + file;
		Socket sock = null; 
		try {
			outToClient.write((file + "\n").getBytes());
			byte [] mybytearray  = new byte [FILE_SIZE];
		    InputStream is = s.getInputStream();
		    fos = new FileOutputStream(FILE_TO_RECEIVED);
		    bos = new BufferedOutputStream(fos);
		    bytesRead = is.read(mybytearray,0,mybytearray.length);
		    current = bytesRead;

		    do {
		         bytesRead =
		            is.read(mybytearray, current, (mybytearray.length-current));
		         if(bytesRead >= 0) current += bytesRead;
		      } while(bytesRead > -1);
		      bos.write(mybytearray, 0 , current);
		      bos.flush();
		      
		    }
		
		finally {
			if (fos != null) fos.close();
		    if (bos != null) bos.close();
		    if (sock != null) sock.close();
		}
		

	}
	
	
	private static void sendFile(Socket connectionSocket, String fileName) throws IOException
	{
		FileInputStream fis = null; 
		BufferedInputStream bis = null; 
		OutputStream os = null; 
		Socket sock = null; 
		try 
		{
			File myFile = new File(fileName).getAbsoluteFile();
			byte [] mybytearray = new byte [(int)myFile.length()];
			fis = new FileInputStream(myFile);
			bis = new BufferedInputStream(fis);
			bis.read(mybytearray, 0, mybytearray.length);
			os = connectionSocket.getOutputStream();
			os.write(mybytearray, 0, mybytearray.length);
			os.flush();
			System.out.println("Done.");
		}
		
		finally {
			if (bis != null) bis.close();
            if (os != null) os.close();
            if (sock!=null) sock.close();
		}
	}
	

}
