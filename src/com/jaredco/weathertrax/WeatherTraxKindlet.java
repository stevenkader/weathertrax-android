package com.jaredco.weathertrax;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.amazon.kindle.kindlet.AbstractKindlet;
import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.ui.KImage;
import com.amazon.kindle.kindlet.ui.KMenu;
import com.amazon.kindle.kindlet.ui.KMenuItem;
import com.amazon.kindle.kindlet.ui.KOptionPane;

public class WeatherTraxKindlet extends AbstractKindlet {
	/*
	 * private static final int SQUARE_SIZE = 40; private static final int
	 * STONE_SIZE = SQUARE_SIZE; private static final int GLOBAL_X_OFFSET = 30;
	 * private static final int GLOBAL_Y_OFFSET = 20;
	 * 
	 * private static final String SGF_DIR = "/sgf/";
	 */
	public static final String addCityUrl = "http://www.worldweatheronline.com/feed/search.ashx?key=ad61101387174311100203&feedkey=3f82ae28f8084719112504&query={0}&num_of_results=20&format=xml";
	public static final String fiveDaysWeatherUrl = "http://www.worldweatheronline.com/feed/premium-weather-v2.ashx?key=ad61101387174311100203&tp=24&feedkey=3f82ae28f8084719112504&lat={0}&lon={1}&format=xml&num_of_days=5";
	public static final String currentDayWeather =  "http://www.worldweatheronline.com/feed/premium-weather-v2.ashx?key=ad61101387174311100203&feedkey=3f82ae28f8084719112504&format=xml&lat={0}&lon={1}&fx=no";
	
	private Container root;
	final JPanel mainPanel = new JPanel();
	//final SelCityDialog selCityDialog = new SelCityDialog();;
	WSCallAndParsing wsCall;
	public void start() {
		System.out.println("start");
	}

	public void stop() {
		System.out.println("stop");
	}

	public void destroy() {
		System.out.println("destroy");
	}

	private JLabel getHeader()
	{
		JLabel header = new JLabel("WeatherTrax");
		header.setFont(new Font("Helvetica", Font.BOLD, 28));
		header.setAlignmentX(Component.CENTER_ALIGNMENT);
		return header;
	}
	public void create(final KindletContext context) {
		wsCall = new WSCallAndParsing();
		root = context.getRootContainer();
		root.removeAll();
		mainPanel.removeAll();

		
		
		mainPanel.add(getHeader());
		mainPanel.add(new JLabel(" "));
		mainPanel.add(new JLabel(" "));
		mainPanel.add(new JLabel(" "));
		mainPanel.add(new JLabel(" "));
		mainPanel.add(new JLabel(" "));
		mainPanel.add(new JLabel(" "));
		mainPanel.add(new JLabel(" "));
		mainPanel.add(new JLabel(" "));
		//mainPanel.setLayout(new BorderLayout());
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		final JLabel label = new JLabel("Please use the menu to add a city");
		//mainPanel.a
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		mainPanel.add(label,BorderLayout.CENTER);

		/*
		 * final KMenuItem addCity = new KMenuItem("Add City");
		 * addCity.addActionListener(new ActionListener() {
		 * 
		 * public void actionPerformed(ActionEvent arg0) { addCity(); }
		 * 
		 * private void addCity() { String city =
		 * KOptionPane.showInputDialog(null, "Enter City Name:", null);
		 * 
		 * if(city == null) { mainPanel.setVisible(true); }else{ final JPanel
		 * cityPanel = showCityDialog(city); EventQueue.invokeLater(new
		 * Runnable() { public void run() { root.removeAll();
		 * root.add(cityPanel); // addMenus(cityPanel,true); root.validate();
		 * root.repaint(); //gamePanel.buttonHint.requestFocus();
		 * 
		 * } });}
		 * 
		 * } });
		 */

		context.setMenu(getMenus());


		root.add(mainPanel);

	}

	// Gets the menus items and events
	private KMenu getMenus() {

		final KMenuItem addCity = new KMenuItem("Add City");
		addCity.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				addCity();
			}

			private void addCity() {
				String city = KOptionPane.showInputDialog(null,
						"Enter City Name", null);

				if (city == null) {
					mainPanel.setVisible(true);
				} else {
					final JPanel cityPanel = showCityDialog(city);
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							root.removeAll();
							root.add(cityPanel);
							root.validate();
							root.repaint();

						}
					});
				}

			}
		});

		final KMenuItem addGPSLoc = new KMenuItem("Add This GPS Location");
		addGPSLoc.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				// showMessageDialog("Only 2 hints allowed per game.");
				// int response = JOptionPane.showConfirmDialog(mainPanel,
				// "Add This GPS Location clicked", "WeatherTrax",
				// JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			}

			/*
			 * private void showMessageDialog(final String msg){ new Thread(){
			 * public void run(){ try { sleep(500); } catch
			 * (InterruptedException e) { e.printStackTrace(); } //
			 * JOptionPane.showMessageDialog(mainPanel,
			 * msg,"Hangman",JOptionPane.INFORMATION_MESSAGE); } }.start(); }
			 */
		});

		final KMenuItem deleteCity = new KMenuItem("Delete City");
		deleteCity.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				// int response = JOptionPane.showConfirmDialog(mainPanel,
				// "Delete City clicked", "Hangman",
				// JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			}
		});
		final KMenuItem makeCityDefault = new KMenuItem("Make City Default");
		makeCityDefault.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				// int response = JOptionPane.showConfirmDialog(mainPanel,
				// "Make City Default clicked", "Hangman",
				// JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			}
		});

		
		
		final KMenu menu = new KMenu();
		menu.add(addCity);
		//menu.add(addGPSLoc);
		menu.add(deleteCity);
		menu.add(makeCityDefault);
		menu.add(new KMenuItem("Refresh/Update This City"));
		menu.add(new KMenuItem("Fahrenheit"));
		//menu.addSeparator();
		//menu.add(new KMenuItem("BBM My Wether"));
		//menu.add(new KMenuItem("Tell a Friend"));
		//menu.addSeparator();
		menu.add(new KMenuItem("Online Help"));
		//menu.add(new KMenuItem("Contact Support"));
		menu.add(new KMenuItem("About"));
		menu.add(new KMenuItem("Exit WeatherTrax"));
		

		return menu;
	}

	public JPanel showCityDialog(String city) {

	
		mainPanel.removeAll();
		mainPanel.setLayout(new BorderLayout());
		JLabel addCityLab = new JLabel("Select City");
		JCheckBox selCityChkBox = new JCheckBox("Save in the list");
		
		
		JPanel citiesPane = new JPanel();
		
		//citiesPane.setLayout(new GridBagLayout());
		citiesPane.setLayout(new BoxLayout(citiesPane, BoxLayout.Y_AXIS));
		//citiesPane.setAutoscrolls(true);
		//Dimension dim = new Dimension(150, 100);
		//citiesPane.setSize(dim);
		//citiesPane.setPreferredSize(dim);
		//citiesPane.setMaximumSize(dim);
		JPanel top = new JPanel(new GridLayout(0, 1));
		top.add(addCityLab);
		top.add(selCityChkBox);
		
		List cities = wsCall.addCity(city);
		//List cities = new ArrayList();
		//JList list  = new JList();
		
		JRadioButton cityRadio;
		final	ButtonGroup btnGrp = new ButtonGroup();
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.ipady = 5;
		for(int i =0; i<cities.size();i++)
		{
			c.gridy = i;
		Map cityDetails = (Map)cities.get(i);
		String cityLabel;
		cityLabel = cityDetails.get("areaName")+" ";
		cityLabel =cityLabel + cityDetails.get("region");
		cityLabel =cityLabel + ", ";
		cityLabel =cityLabel + (String)cityDetails.get("country");
		cityRadio = new JRadioButton(cityLabel);
		cityRadio.setHorizontalTextPosition(SwingConstants.RIGHT);
		cityRadio.setName(cityDetails.get("latitude")+","+cityDetails.get("longitude"));
		btnGrp.add(cityRadio);
		//cityRadios.add(cityRadio);
		
		citiesPane.add(cityRadio,c);	
		cityRadio.setAlignmentX(Component.LEFT_ALIGNMENT);
		}
		//citiesPane.setBackground(Color.green);
		//mainPanel.add(citiesPane);
	
		JButton okButton = new JButton("Ok");
		okButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				
				//if(event.getSource()==utton)  
			       String lat="";
			       String lon="";
			        Enumeration allRadioButton=btnGrp.getElements();  
			        while(allRadioButton.hasMoreElements())  
			        {  
			           JRadioButton temp=(JRadioButton)allRadioButton.nextElement();  
			           if(temp.isSelected())  
			           {  
			        	   temp.getName().indexOf(',');
			        	 lat =  temp.getName();
			        	String[] lanLon = split(temp.getName(), ",");
			        	lat = lanLon[0];
			        	lon = lanLon[1];
			            //JOptionPane.showMessageDialog(null,"You select : "+temp.getText()+temp.getName()); 
			        	   break;
			           }  
			        }            
			     
				showWeather(lat,lon);
				
				}
				
			
		});
		
		JPanel buttom = new JPanel(new GridLayout(0, 1));
		buttom.add(okButton);
		buttom.add(new JButton("Cancel"));
		
		
		mainPanel.add(top,BorderLayout.NORTH);
		//mainPanel.add(citiesPane,BorderLayout.CENTER);
		mainPanel.add(buttom,BorderLayout.SOUTH);

		  final JScrollPane scrollPane = new JScrollPane(citiesPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		 // citiesPane.setAlignmentY(1);
	        // reverse the default scrolling direction, as this is more intuitive.
	      //  final JScrollBar horizontalScrollBar = scrollPane.getVerticalScrollBar();
	      //  horizontalScrollBar.setUnitIncrement(-horizontalScrollBar.getUnitIncrement());
	        mainPanel.add(scrollPane, BorderLayout.CENTER);
		
		return mainPanel;
	}

	public void showWeather(String lat, String lon) {
		final JPanel cityPanel = new JPanel(new BorderLayout());
		
		JPanel headerPane= new JPanel();
		headerPane.add(getHeader());
		
		cityPanel.add(headerPane,BorderLayout.NORTH);
		cityPanel.add(showCityWeather(lat,lon),BorderLayout.CENTER);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				root.removeAll();
				root.add(cityPanel);
				root.validate();
				root.repaint();
			}
		});
	}

	public JPanel showCityWeather(String lat, String lon) {
		JPanel jPanel = new JPanel(new BorderLayout());
		
		
		JComboBox cityCombo = new JComboBox();
	//	cityCombo.addItem("Pune");
		//cityCombo.setPreferredSize(new Dimension(300, 50));
		jPanel.setBorder(new EmptyBorder(0, 10, 0, 10));
		//cityCombo.setBorder(BorderFactory.create);
		jPanel.add(cityCombo,BorderLayout.NORTH);
		
		jPanel.add(getWeatherPanel(lat,lon),BorderLayout.CENTER);
JPanel empty =new JPanel(new GridLayout(0, 1));
JLabel emp1  =new JLabel(" ");
JLabel emp2  =new JLabel(" ");
JLabel emp3  =new JLabel(" ");
JLabel emp4  =new JLabel(" ");
empty.add(emp1);
empty.add(emp2);
empty.add(emp3);
empty.add(emp4);
jPanel.add(empty,BorderLayout.SOUTH);
		return jPanel;
	}

	private JPanel getWeatherPanel(String lat, String lon) {
		JPanel panel = new JPanel(new BorderLayout());
		
		Map weatherMap = wsCall.getWeatherOfFiveDays(lat,lon);
		
		
		panel.add(addWeatherPanelSumm((Map)weatherMap.get("current_condition")), BorderLayout.CENTER);
		panel.add(addWeatherPanel((List)weatherMap.get("weathers")), BorderLayout.SOUTH);
		return panel;
	}

	private Component addWeatherPanelSumm(Map currMap) {
		JPanel panel = new JPanel(new GridBagLayout());
		
		String observation_time = currMap.get("observation_time").toString();
		String humidity = currMap.get("humidity").toString();
		String temp_C = currMap.get("temp_C").toString();
		String weatherIconUrl = currMap.get("weatherIconUrl").toString();
		//String maxtempC = weather.get("maxtempC").toString();
		weatherIconUrl = weatherIconUrl.replace(':', '_');
		ResourceBundle myResources =
		      ResourceBundle.getBundle("WeatherIconMapping");
	String imgName = myResources.getString(weatherIconUrl);
		
	
	panel.add(addWeatherSummTemp(observation_time,humidity));
		panel.add(addWeatherSummIcon(temp_C, imgName));
		/*Dimension minimumSize = new Dimension(250, 200);
		panel.setMinimumSize(minimumSize);
		panel.setMaximumSize(minimumSize);
		panel.setPreferredSize(minimumSize);*/
		return panel;
	}

	private Component addWeatherSummTemp(String obsrvAt, String humidity) {
		JPanel panel = new JPanel(new GridLayout(2, 2, 2, 2));
		panel.add(new JLabel("Observed at :"));
		panel.add(new JLabel(obsrvAt));
		panel.add(new JLabel("Humiity :"));
		panel.add(new JLabel(humidity+"%"));
		return panel;
	}

	private Component addWeatherSummIcon(String tempr, String imgName) {
		JPanel panel = new JPanel(new GridLayout(1, 2, 2, 2));
		JLabel temprat = new JLabel(tempr+"°C ");
	//	temprat.setForeground(Color.YELLOW);
		// temprat.setSize(50, 50);
		temprat.setFont(new Font("Helvetica", Font.BOLD, 28));
		panel.add(temprat);
		panel.add(new KImage(Toolkit.getDefaultToolkit().createImage(
				getClass().getResource(imgName))));

		return panel;
	}

	private Component addWeatherPanel(List weathers) {
		JPanel panel = new JPanel(new GridLayout(0,6));

		Dimension minimumSize = new Dimension(250, 200);

		JPanel iconPanel = getIconHeaderPanel("High", "Low", "Rain");
		panel.add(iconPanel);
		JPanel iconPanel1;
		for(int i=0;i<weathers.size();i++)
		{
			Map weather = (Map)weathers.get(i);
			String maxtempC = weather.get("maxtempC").toString();
			String mintempC = weather.get("mintempC").toString();
			String date = weather.get("date").toString();
			String precipMM = weather.get("precipMM").toString();
			String weatherIconUrl = weather.get("weatherIconUrl").toString();
			//String maxtempC = weather.get("maxtempC").toString();
			weatherIconUrl = weatherIconUrl.replace(':', '_');
			ResourceBundle myResources =
			      ResourceBundle.getBundle("WeatherIconMapping");
		String imgName = myResources.getString(weatherIconUrl);
			
			Date dat = null;
			try {
				 dat = new  SimpleDateFormat("yyyy-MM-dd").parse(date);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dat);
			SimpleDateFormat formatter = new SimpleDateFormat("EEE");
			String text = formatter.format(calendar.getTime());
			
			 iconPanel1 = getIconPanel(
					 text,
						maxtempC,
						mintempC,
						precipMM,
						Toolkit.getDefaultToolkit().createImage(
								getClass().getResource(imgName)));
				panel.add(iconPanel1);
		}
		
		return panel;
	}

	private JPanel getIconHeaderPanel(String string, String string2,
			String string3) {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		// c.insets = new Insets( 2, 2, 2, 2 );
		c.gridx = 0; // Column 0
		c.gridy = 0; // Row 0
		c.ipadx = 1; // Increases component width by 2 pixels
		c.ipady = 64; // Increases component height by 2 pixels
		panel.add(new JLabel(" "), c);
		c.gridy = 1; // Row 0
		c.ipady = 1;
		panel.add(new JLabel(string), c);
		c.gridy = 2; // Row 0
		c.ipady = 1;
		panel.add(new JLabel(string2), c);
		c.gridy = 3; // Row 0
		c.ipady = 1;
		panel.add(new JLabel(string3), c);
		return panel;
	}

	private JPanel getIconPanel(String day, String high, String low,
			String rain, Image createImage) {
		// TODO Auto-generated method stub
		JPanel panel = new JPanel(new GridBagLayout());
		Dimension minimumSize = new Dimension(150, 100);
		// panel.setMinimumSize(minimumSize);
		// panel.setMaximumSize(minimumSize);
		// panel.setPreferredSize(minimumSize);

		GridBagConstraints c = new GridBagConstraints();
		// c.insets = new Insets( 2, 2, 2, 2 );
		c.gridx = 0; // Column 0
		c.gridy = 0; // Row 0
		c.ipadx = 10; // Increases component width by 10 pixels
		c.ipady = 2; // Increases component height by 10 pixels

		panel.add(new JLabel(day), c);
		c.gridy = 1; // Row 1
		panel.add(new KImage(createImage), c);
		c.gridy = 2; // Row 2
		panel.add(new JLabel(high), c);
		c.gridy = 3; // Row 3

		panel.add(new JLabel(low), c);
		c.gridy = 4; // Row 4
		panel.add(new JLabel(rain), c);
		return panel;
	}
	/*
	 * void exit() { //Display confirm dialog int confirmed =
	 * JOptionPane.showConfirmDialog(mainFrame,
	 * "Exit will close all open widows.\nAre you sure you want to exit the application?"
	 * , "Confirm Quit", JOptionPane.YES_NO_OPTION); //Close if user confirmed
	 * if (confirmed == JOptionPane.YES_OPTION) { System.exit(0); } }
	 */
	public static String[] split(String inString, String delimeter) {
        String[] retAr;
        try {
                Vector vec = new Vector();
                int indexA = 0;
                int indexB = inString.indexOf(delimeter);

                while (indexB != -1) {
                        vec.addElement(new String(inString.substring(indexA, indexB)));
                        indexA = indexB + delimeter.length();
                        indexB = inString.indexOf(delimeter, indexA);
                }
                vec.addElement(new String(inString.substring(indexA, inString
                                .length())));
                retAr = new String[vec.size()];
                for (int i = 0; i < vec.size(); i++) {
                        retAr[i] = vec.elementAt(i).toString();
                }
        } catch (Exception e) {
                String[] ar = { e.toString() };
                return ar;
        }
        return retAr;
}
public static String replaceAll(String originalString, String replaceTo, String replaceWith){
  int index = originalString.indexOf(replaceTo);
 String finalString="";
 while(index!=-1){
            finalString+= originalString.substring(0, index)+replaceWith;
            originalString=originalString.substring(index+replaceTo.length());
            index = originalString.indexOf(replaceTo);
      }
      finalString+=originalString;
     
      return finalString;
}
}

	
