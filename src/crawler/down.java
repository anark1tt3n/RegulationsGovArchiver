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
								//attachment links | attachment titles | extension of attachments | the folder they go in
	public static void loadAll(ArrayList<String> links, ArrayList<String> titles, ArrayList<String> ext, ArrayList<String> folds) { 
		int i = 0;
        new File("Attachments").mkdir(); //make the attachments folder
        for(String url : links) {    
            if(!(url == null)) {
            		main.print("Referencing index " + i + " out of " + links.size());
                    load(url, titles.get(i), ext.get(i), folds.get(i));
            }
            i++;
        }
    }

	public static void delete(File file) {
		file.delete();
	}

	public static void load(String link, String title, String ext, String fold) { //goes through list of pdfs, downloads
		main.print("Downloading file titled " + title);
		try {
			String T = title.replaceAll("[\\\\/:*?\"<>|]", "_"); //we're sanitizing the title
			new File("Attachments/" + fold).mkdirs();
			File attachment = File.createTempFile("Attachments/" + fold + "/" + T, ext); 
			URL website = new URL(link); //makes a URL from the first string passed
			ReadableByteChannel rbc = Channels.newChannel(website.openStream()); //opens the given url as a stream of bytes
			FileOutputStream fos = new FileOutputStream(attachment); //create new fileoutput stream, file name as the stored PDF title"
			main.print(new File("Attachments/" + fold + "/" + T).getAbsolutePath());
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE); //writes the above bytes to the file output stream
			fos.close(); //closes file output stream
		} catch (IOException e) {
			main.print("failed to DL attachment, continuing...");
            e.printStackTrace();
		}
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