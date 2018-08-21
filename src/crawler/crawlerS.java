package crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;;

public class crawlerS {

	static ArrayList<String> attLinks = new ArrayList<String>(); //we're gonna store attachment links
	static ArrayList<String> attTitles = new ArrayList<String>(); // and the titles here
	static ArrayList<String> hrefs = new ArrayList<String>(); //where we place all the <a> elements
	static ArrayList<String> docExt = new ArrayList<String>(); //where we'll be placing the file extension of the documents
	static ArrayList<String> docs = new ArrayList<String>(); //the <a> links that are actually documents
	static ArrayList<String> docTitles = new ArrayList<String>(); //used for folder names
	static String posExt[] = {"pdf" , "msw12" , "excel12book" , "crtext"};//possible attachment extensions
	static String realExt[] = {"pdf" , "docx" , "xlsx" , "txt"};
	static boolean nPage = true; //bool to verify if there is a next page
	static ChromeOptions options = new ChromeOptions();
	static String[] urlSplit; //where we place the document id
	static int pNumb = 50; //page number, since getting selenium to click the damn button was too hard
	static String uUrl; //user given url, so it cna be passed from the original program.
	static int progress = 0;


	public static void initC(String url) throws InterruptedException{
		System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
		if(main.head==false) {
			options.addArguments("--headless");
		}
		WebDriver crawler = new ChromeDriver(options);
		try {
			crawl(url,crawler);
		} catch(TimeoutException | IOException e) {
			crawler.quit();
			main.print("No documents or bad URL on page " + url + ". Closing chromedriver...");
			JOptionPane.showMessageDialog(null, "Bad URL or no documents on this page", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
	}

	public static void crawl(String url, WebDriver crawler) throws InterruptedException, IOException {
		crawler.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS); //make it wait for the page to hopefully load
		main.print("Starting search for document pages on this page");
		if(url.toLowerCase().contains("docket")) {
		if(!url.toLowerCase().contains("browser")) { //makes sure we're going to the docket *browser*
			String urlSplit[] = url.split("="); //and showing all documents outside comments
			url = "https://www.regulations.gov/docketBrowser?rpp=50&so=ASC&sb=commentDueDate&po=50&dct=O%2BSR%2BFR%2BPR%2BN&D=" + urlSplit[urlSplit.length-1];
			main.print("URL was not a link to the docket browser, document ID is: " + urlSplit[urlSplit.length-1]);
			uUrl = url;
			crawler.get(url);
		}
		else {
			crawler.get(url); 
		}
		} else {
			crawler.quit();
			main.print("Was not linked to a docket. URL given was: " + url);
			JOptionPane.showMessageDialog(null, "Make sure you are linking to the docket", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		while(nPage) {
			getLinks(crawler);
		}
		main.print(hrefs);
		main.print("We got a list of urls on the page, now to parse...");
		int linksOnPage = hrefs.size();
		for(int i=0;i<linksOnPage;i++) { //originally this didn't work as a for each loop statement, will rewrite soon
			String a = hrefs.get(i);
			if(a!=null) {
				if(a.contains("document")) {
					if(!docs.contains(a)) {
						docs.add(a);
					}
				}
			}
		}

		main.print("We should have a list of document URLs now");
		main.print(docs);
		main.print("Now we start looking for the attachments");
		compileList(crawler, docs);
		down.loadAll(attLinks,attTitles,docExt,docTitles);
		main.print("Should've downloaded all attachments listed.");
		crawler.quit();
		JOptionPane.showMessageDialog(draw.urlF, "We should've downloaded all attachments in the Docket Browser for this document!", "Done", JOptionPane.INFORMATION_MESSAGE);
	}

	public static void nextPage(WebDriver crawler) {
		// Displaying a - b of c | is the below
		// we don't know how many characters there's gonna be so I'm dealing with this like this instead of substrings
		String a = crawler.findElement(By.xpath("//*[@id=\"searchContent\"]/div[2]/div[2]/div/div[2]/div[3]/div/div/div[12]")).getText();
		String fPage[] = a.split(" of ");
		//"Displaying a - b" | "c"
		String cPage[] = fPage[0].split(" - ");
		//"Displaying a" | "b"
		if(!fPage[1].equals(cPage[1])) { //if the final page isn't equal to the current...
			main.print("There's another page, let's go to that!");
			uUrl = uUrl.replace("&po="+pNumb, "&po="+(pNumb+50)); //going to next page via this cool argument
			pNumb = pNumb + 50;
			crawler.get(uUrl);
			getLinks(crawler);
		}
		else {
			nPage = false;
		}
	}

	public static void getLinks (WebDriver crawler) {
		//we want the column to be visible before we search for anything
		WebDriverWait wait = new WebDriverWait(crawler, 3);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("GIY1LSJBCE")));
		//there's probably a better way to do this but we're all about hacked together solutions here /shrug
		List<WebElement> links = crawler.findElements(By.tagName("a")); //so that we can make a list of the href elements on the page
		for(WebElement link : links) { //for each link in links
			hrefs.add(link.getAttribute("href")); //get the link
		}
		main.print("Got the links, checking if another page");
		nextPage(crawler);
	}

	public static int percentArch(int a,int b) {
		return (a/b)*80;
	}

	public static void compileList(WebDriver crawler, ArrayList<String> docs) {
		WebDriverWait wait = new WebDriverWait(crawler, 1);
		for(String doc : docs) { //for each document in the array docs
			main.print("Loading a new document...");
			crawler.get(doc); //load it
			main.print("Connected to document ");
			main.print("Finding attachment link...");
			try { //try to do...
				int i = 0; //we're using this for attachment stuff further down
				for(String ext : posExt) { //looking for files of every extension type
					List<WebElement> loadAtt = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy((By.cssSelector("a[href*='=" + ext + "']")))); //waiting for column to show
					main.print("We've found files with the extension of: " + realExt[i]);
					for(WebElement att : loadAtt) {
						try {
							main.print("Found the attachment, adding to link collection");
							attLinks.add(att.getAttribute("href")); //adding download link
							WebElement rTitle = wait.until(ExpectedConditions.presenceOfElementLocated((By.xpath("/html/body/div[3]/div[2]/div[2]/div[2]/div/div/div[1]/div[1]/h1"))));
							docTitles.add(rTitle.getText()); //
							try { //titles are nested weirdly so we're ripping em
								WebElement parentElement = att.findElement(By.xpath("./..")); //getting to grandparentto try to
								WebElement grandElement = parentElement.findElement((By.xpath("./.."))); //find the parent to find the title
								WebElement title = grandElement.findElement(By.xpath("//h3")); //if its an attachment listed an alt way
								rTitle = title;
							} catch(NoSuchElementException e) {
								main.print("Defaulting to document title for file name...");
							}
							attTitles.add(rTitle.getText()); //adding the text of the above element to our list of document titles
							docExt.add(realExt[i]); //adding the extension, filtered because this website is weird with extensions
//							progress = percentArch(i,docs.size()); 
//							main.print("We are " + progress + "% percent done with checking these links :)");
						} catch(InvalidSelectorException e) {
							main.print("Couldn't find a document with an extension of \"" + ext + "\" on this page. \n Continuing...");
						}
					}
					i++;
				}
			} catch (TimeoutException e) {
				main.print("Couldnt find document with current extension, continuing.");
				attLinks.add(null);
				docExt.add(null);
				attTitles.add(null);
				docTitles.add(null);
				
			}
		}
	}

}
