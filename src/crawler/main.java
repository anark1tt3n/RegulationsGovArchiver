//Tea Jerrica "Teachable" De Ferrari - me@teachable.im
package crawler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JOptionPane;

//version 1.1

public class main {

	static String docUrl;
	static File log = new File("log.txt");
	static File backups = new File("backup.txt");
	static boolean current = false;
	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	static boolean head=false;

	public static void print(Object a) { //utility class to make logging easier
		a.toString();
		System.out.println(a);
		if(!current) { //add the date and add a new line if the program has just ran for the first time
			a = LocalDateTime.now() + "\r\n\r\n" + a;
			current=true;
		}
		writeF(a,log);
	}

	public static void writeF(Object a, File title) {
		try { //should be writing to the file
			FileWriter fileWriter = new  FileWriter(title,true);
			fileWriter.write(a + "\r\n"); //write and add new line
			fileWriter.close(); //closing our filewriter
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws InterruptedException, IOException {
		if(args.length<=1) {
			for (String a: args) { //for each string in args
				if(a.equals("-head")) {
					head=true;
				}
			}
		} else {
			main.print("The only valid argument is -head, please don't use any others");
		}
		draw panel = new draw();
		panel.makeGUI();
		File chrom = new File("chromedriver.exe");
		try{
			if(!chrom.exists()) {
				down.load("https://chromedriver.storage.googleapis.com/2.41/chromedriver_win32.zip", "chromedriver", "zip");
				down.unzip("chromedriver");
				down.delete(new File("chromedriver.zip"));
			}
		} catch(IOException e) {
			JOptionPane.showMessageDialog(null, "Failed to download chromedriver", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}