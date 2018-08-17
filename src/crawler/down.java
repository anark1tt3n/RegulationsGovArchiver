package crawler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class down{

	public static void loadAll(ArrayList<String> links, ArrayList<String> titles, ArrayList<String> ext, ArrayList<String> groups, ArrayList<String> folds) {
		String folder = groups.toString().replaceAll("[\\[\\]\\s]", ""); //what folder are we on?
		int[] groupNumb = Arrays.stream(folder.split(",")).mapToInt(Integer::parseInt).toArray(); //grouping of attachments as ints
		int document = 0; //what document in that directory are we on?
		int groupCount = 0; //what docs-per-folder are we comparing this number to?
		int count = 0; //what number are we in terms of docs-per-folder?
		for(String url : links) {
			if(!url.equals("No Att")) {
				if(count < groupNumb[document]) { //if there's still more files to fit in this folder
					try {
						load(url, titles.get(count), ext.get(count), folds.get(groupNumb[groupCount])); //try to download the file
						count++;
					} catch (IOException e) {
						main.print("failed to DL attachment, continuing...");
						count++;
						e.printStackTrace();
					}
				}
			}
			else {
				document++;
			}
			if(count==groupNumb.length-1) {
				groupCount++;	
			}
		}
	}
	
	public static void delete(File file) {
		file.delete();
	}

	public static void load(String link, String title, String ext, String fold) throws IOException { //goes through list of pdfs, downloads
		main.print("Downloading file titled " + title);
		String T = title.replaceAll("[\\\\/:*?\"<>|]", "_") + "." + ext; //we're sanitizing the title
		new File("Attachments" + fold).mkdirs(); //making a folder for it
		URL website = new URL(link); //makes a URL from the first string passed
		ReadableByteChannel rbc = Channels.newChannel(website.openStream()); //opens the given url as a stream of bytes
		FileOutputStream fos = new FileOutputStream(new File("Attachments/" + fold + "/" + T)); //create new fileoutput stream, file name as the stored PDF title"
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE); //writes the above bytes to the file output stream
		fos.close(); //closes file output stream
	}

	public static void unzip(String a) throws IOException { //utility for unzipping
		try {
			ZipFile zipFile = new ZipFile(a+".zip"); //appending zip to the end of this
			Enumeration<?> enu = zipFile.entries();
			while (enu.hasMoreElements()) { //honestly this is mostly magic and I can't explain what exactly is gong on
				ZipEntry zipEntry = (ZipEntry) enu.nextElement();

				String name = zipEntry.getName();

				File file = new File(name);
				if (name.endsWith("/")) {
					file.mkdirs();
					continue;
				}

				File parent = file.getParentFile();
				if (parent != null) {
					parent.mkdirs();
				}

				InputStream is = zipFile.getInputStream(zipEntry);
				FileOutputStream fos = new FileOutputStream(file);
				byte[] bytes = new byte[1024];
				int length;
				while ((length = is.read(bytes)) >= 0) {
					fos.write(bytes, 0, length);
				}
				is.close();
				fos.close();

			}
			zipFile.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public static void load(String link, String title, String ext) throws IOException { //goes through list of pdfs, downloads
		main.print("Downloading file titled " + title);
		String T = title.replaceAll("[\\\\/:*?\"<>|]", "_") + "." + ext; //we're sanitizing the title
		URL website = new URL(link); //makes a URL from the first string passed
		ReadableByteChannel rbc = Channels.newChannel(website.openStream()); //opens the given url as a stream of bytes
		FileOutputStream fos = new FileOutputStream(new File(T)); //create new fileoutput stream, file name as the stored PDF title"
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE); //writes the above bytes to the file output stream
		fos.close(); //closes file output stream
	}
}