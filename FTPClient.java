import java.io.*;
import java.net.*;
import java.util.*;
public class FTPClient {
	
	public static void main(String argv[]) throws Exception {
		
		String fileName;
		String username;
		String password;
		String isAuthenticated; 
		FileInputStream fis;
		
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		Socket clientSocket = new Socket("localhost", 6790);
		
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		System.out.println("Enter your username: ");
		username = inFromUser.readLine();
		System.out.println("Enter your password: ");
		password = inFromUser.readLine();
		
		outToServer.writeBytes(username + '\n');
		outToServer.writeBytes(password + '\n');
		
		isAuthenticated = inFromServer.readLine();
		while (!isAuthenticated.equals("Authenticated!")) {
			System.out.println("Your information is incorrect, try again");
			System.out.println("Enter your username: ");
			username = inFromUser.readLine();
			System.out.println("Enter your password: ");
			password = inFromUser.readLine();
			
			outToServer.writeBytes(username + '\n');
			outToServer.writeBytes(password + '\n');
			
			isAuthenticated = inFromServer.readLine();
		}
		
		System.out.println("You are " + isAuthenticated);
		
		if (isAuthenticated.equals("Authenticated!")) {
			while (true) {
				System.out.println("===========FTP Menu=========");
				System.out.println("Your Command Options are: ");
				System.out.println("1: LIST");
				System.out.println("2: CHANGEDIR");
				System.out.println("3: UPLOAD Files (Both ASCI and binary)");
				System.out.println("4: EXIT");
				
			   String command = inFromUser.readLine();
				switch(command) {
				  case "1":
				    // code block
					outToServer.writeBytes("1\n");
					// Recieve a String composed of all the files separated by a ","
					String currentDirectory = inFromServer.readLine();
					//Split String into an array of files 
					String[] splitCurrentFolders = currentDirectory.split(",");
					// Loop through the files printing out each one 
					for (int i = 0; i<splitCurrentFolders.length; i++) {
						System.out.println(splitCurrentFolders[i]);
					}
				    break;
				  case "2":
				    // code block
				    outToServer.writeBytes("2\n");
				    System.out.println("So you want to change the directory do you?");
				    String cDirectory = inFromServer.readLine();
				    System.out.println("Current Directory is: " + cDirectory);

				    System.out.println("Enter in the new path: ");
				    String newDirectory = inFromUser.readLine();
				    outToServer.writeBytes(newDirectory + "\n");
				    String serverResponse = inFromServer.readLine();
				    System.out.println(serverResponse);
				    
				    break;
				    
				  case "3": 
				    System.out.print("Enter file name: ");
				    fileName = inFromUser.readLine();
					File f = new File(fileName);
					if(!f.exists()) {
						System.out.println(fileName + " does not exist!");
					} else {
						System.out.println("file exists");
						//First send the command as 1 
						outToServer.write("3\n".getBytes());
						//Now send the file name
						outToServer.write((fileName + "\n").getBytes());
						//Finally send the file content
						sendFile(fileName, clientSocket);
						System.out.println(fileName + " uploaded successfully.");
					}
				
				
					break;
				  case "4":
					System.out.print("Enter file name: ");
					fileName = inFromUser.readLine();
					outToServer.write("4\n".getBytes());
					outToServer.write((fileName + "\n").getBytes());
					saveFile(fileName, clientSocket, outToServer);
					break;
				  case "5":
				    //Send the command as 4 to logout
					outToServer.write("5\n".getBytes());
					System.out.println("Logged out successfully.");
					System.exit(0);
					break;
					
				 default: 
					 System.out.println("You did not input a correct number. Try again and enter either 1,2,3, or 4");
				    
				}
				
			}
				
			
		}
		
		clientSocket.close();
		
	}
	
	public static void sendFile(String file, Socket s) throws IOException {
		DataOutputStream dos = new DataOutputStream(s.getOutputStream());
		FileInputStream fis = new FileInputStream(file);
		byte[] buffer = new byte[4096];
		
		while (fis.read(buffer) > 0) {
			dos.write(buffer);
		}
		
		fis.close();
	}
	
	public static void saveFile(String file, Socket s, DataOutputStream outToServer) throws IOException {
		final int FILE_SIZE = 6022386;
		int bytesRead; 
		int current = 0; 
		FileOutputStream fos = null; 
		BufferedOutputStream bos = null; 
		String FILE_TO_RECEIVED = "C:\\Users\\Thomas McFann\\eclipse-workspace\\CSCI361MiniProject2\\Client\\" + file;
		Socket sock = null; 
		try {
			outToServer.write((file + "\n").getBytes());
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
		      System.out.println("File " + FILE_TO_RECEIVED
		          + " downloaded (" + current + " bytes read)");
		    }
		
		finally {
			if (fos != null) fos.close();
		    if (bos != null) bos.close();
		    if (sock != null) sock.close();
		}
	}
	
	


}
