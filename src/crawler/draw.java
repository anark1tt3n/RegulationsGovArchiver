package crawler;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;



public class draw extends JPanel implements ActionListener {

	String userU;
	JButton go = new JButton("Get PDFs"); 
	static JTextField urlF = new JTextField(20);
	static JProgressBar progressBar = new JProgressBar(0, 100);

	protected void paintComponent(Graphics g) { //lesbian flag gradient background for aesthetic/pride
		super.paintComponents(g); //we're overwriting the paintComponent basically
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		int w = getWidth();
		int h = getHeight();
		Color color1 = new Color(164,0,97);
		Color color2 = new Color(183,85,146);
		Color color3 = new Color(236,236,234);
		Color color4 = new Color(196,78,85);
		Color color5 = new Color(138,30,4);
		Color[] colors = {color1,color2,color3,color4,color5}; //colors for our gradient
		float[] dist = {0.2f,0.4f,0.5f,0.6f,1.0f}; //positions for the gradient
		LinearGradientPaint gp = //making the gradient
				new LinearGradientPaint(0,0,w,h,dist,colors);
		g2d.setPaint(gp);
		g2d.fillRect(0, 0, w, h);
	}

	public void updateBar() {

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		userU = urlF.getText();
		main.print(userU);
		try {
			new Thread(new Runnable(){
				public void run(){
					int x = 0;
					while(x<=100) {
						x = crawlerS.progress;
						progressBar.setValue(x);        // Setting incremental values
						if (x == 100 ){
							progressBar.setString("Done with the download!");   // End message
							try{
								Thread.sleep(200);
							}catch(Exception ex){
							}
						}
					}
				}
			}).start();
			crawlerS.initC(userU); //passes the url to the crawlerS class so it can start searching
		} catch (InterruptedException malf) {
			main.print("Malformed URL");
		}
	}

	public void makeGUI() { //visual stuff

		go.addActionListener(this);

		urlF.setEditable(true);
		urlF.addFocusListener(new FocusListener() {
			String a;
			public void focusGained(FocusEvent e) { //when focused, clears the text field
				a = urlF.getText();
				if(a.equals("Place document URL here")){
					urlF.setText("");
				}
			}

			public void focusLost(FocusEvent e) {
				a = urlF.getText();
				if(a.equals("") || a.equals(null)){
					urlF.setText("Place document URL here");
				}
			}
		});
		urlF.setText("Place document URL here");

		progressBar.setValue(0);
		progressBar.setStringPainted(true);

		add(urlF, BorderLayout.CENTER);
		add(go, BorderLayout.SOUTH);
		add(progressBar,BorderLayout.NORTH);

		JFrame test = new JFrame();

		test.setLocationByPlatform(true);
		test.add(this);
		test.setSize(370, 100);
		test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		test.setTitle("Docket Archiver - V1.0");
		test.setResizable(false);
		test.setVisible(true);
		go.requestFocusInWindow();
	}
}