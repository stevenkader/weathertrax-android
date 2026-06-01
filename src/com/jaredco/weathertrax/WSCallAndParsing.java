package com.jaredco.weathertrax;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class WSCallAndParsing {

	public static final String addCityUrl = "http://www.worldweatheronline.com/feed/search.ashx?key=ad61101387174311100203&feedkey=3f82ae28f8084719112504&query={0}&num_of_results=20&format=xml";
	public static final String fiveDaysWeatherUrl = "http://www.worldweatheronline.com/feed/premium-weather-v2.ashx?key=ad61101387174311100203&tp=24&feedkey=3f82ae28f8084719112504&lat={0}&lon={1}&format=xml&num_of_days=5";
	public static final String currentDayWeather =  "http://www.worldweatheronline.com/feed/premium-weather-v2.ashx?key=ad61101387174311100203&feedkey=3f82ae28f8084719112504&format=xml&lat={0}&lon={1}&fx=no";
	
	/**
	 * @param args
	 */
	private String getResponseFromServer(String wsUrl)
	{
		//StringBuilder wsResponse = new StringBuilder();
		String output="";
		try {

			URL url = new URL(wsUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/xml");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
			
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			
		//	//System.out.println("Output from Server .... \n");
			String out="";
			while ((out = br.readLine()) != null) {
				// //System.out.println(output);
				//wsResponse.append(output);
				output = output+out;
			}

			conn.disconnect();

			//parseXMLString(wsResponse.toString());

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
		
		return output;
	}
	private String getResponseFromClasspath(String wsUrl)
	{
		String wsResponse = "";
		//try {

			
			InputStream inputStream = ClassLoader.class.getResourceAsStream("/search.ashx.xml");
			
		//	String content = inputStream.toString();

		//	BufferedReader br = new BufferedReader(new InputStreamReader(
		//			(is)));

			/*BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));*/

			//String output;
		//	//System.out.println("Output from Server .... \n");
			
		/*	while ((output = br.readLine()) != null) {
				// //System.out.println(output);
				wsResponse=wsResponse+output;
			}*/

		

			//parseXMLString(wsResponse.toString());

		/*} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}*/
			// String str = new Scanner(inputStream).next();
		//	System.out.println(str);
		return null;	}
	
	private List parseXMLStringAddCity(String xmlString) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		List  cities = new ArrayList();
		//xmlString = "<search_api><result><areaName><![CDATA[Pune]]></areaName><country><![CDATA[India]]></country><region><![CDATA[Maharashtra]]></region><latitude>18.533</latitude><longitude>73.867</longitude><population>2935968</population><weatherUrl><![CDATA[http://www.worldweatheronline.com/Pune-weather/Maharashtra/IN.aspx]]></weatherUrl></result><result><areaName><![CDATA[Pune]]></areaName><country><![CDATA[Brazil]]></country><region><![CDATA[Para]]></region><latitude>1.967</latitude><longitude>-54.917</longitude><population>0</population><weatherUrl><![CDATA[http://www.worldweatheronline.com/Pune-weather/Para/BR.aspx]]></weatherUrl></result></search_api>";

		try {
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(new StringReader(
					xmlString)));
			// Document document = builder.parse(wsResponse.toString());
			////System.out.println("wsResponse::" + xmlString);
			////System.out.println("getTextContent::" + document.getXmlVersion());

			Element docEle = document.getDocumentElement();

			// Print root element of the document
			////System.out.println("Root element of the document: "
			//		+ docEle.getNodeName());

			NodeList results = docEle.getElementsByTagName("result");

			for (int i = 0; i < results.getLength(); i++) {
				Node node = results.item(i);
				//System.out.println("NodeName()	 : 	NodeValue");
				HashMap map = new HashMap();
				for (int j = 0; j < node.getChildNodes().getLength(); j++) {
					Node childNode = node.getChildNodes().item(j);
					//System.out.println(childNode.getNodeName() + ":"
					//		+ childNode.getTextContent());
					map.put(childNode.getNodeName(), childNode.getTextContent());
				}
				cities.add(map);
				//System.out.println("----------------------");
			}

			// Print total student elements in document
			////System.out.println("Total results: " + results.getLength());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return cities;
	}
	private Map parseXMLStringFiveDaysWeather(String xmlString) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Map map = new HashMap();
		try {
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(new StringReader(
					xmlString)));
			// Document document = builder.parse(wsResponse.toString());
			////System.out.println("wsResponse::" + xmlString);
			////System.out.println("getTextContent::" + document.getXmlVersion());

			Element docEle = document.getDocumentElement();

			// Print root element of the document
			////System.out.println("Root element of the document: "
			//		+ docEle.getNodeName());

			//This is working fine for current tag 
			Map currentCondMap = new HashMap();
			NodeList currCondResults = docEle.getElementsByTagName("current_condition");

			for (int i = 0; i < currCondResults.getLength(); i++) {
				Node node = currCondResults.item(i);
			//	//System.out.println("NodeName()	 : 	NodeValue");
				for (int j = 0; j < node.getChildNodes().getLength(); j++) {
					Node childNode = node.getChildNodes().item(j);
				//	//System.out.println(childNode.getNodeName() + ":"
				//			+ childNode.getTextContent());
					currentCondMap.put(childNode.getNodeName(), childNode.getTextContent());
				}
				////System.out.println("----------------------");
			}

			
			NodeList results = docEle.getElementsByTagName("weather");
			
			List weathers = new ArrayList();
			for (int i = 0; i < results.getLength(); i++) {
				Node node = results.item(i);
				Map weatherMap = new HashMap();
				for (int j = 0; j < node.getChildNodes().getLength(); j++) {
					Node childNode = node.getChildNodes().item(j);
				
					if(childNode.getChildNodes().getLength()>1)
					{
						for(int k =0;k<childNode.getChildNodes().getLength();k++)
						{
							Node subChildNode = childNode.getChildNodes().item(k);
							weatherMap.put(subChildNode.getNodeName(), subChildNode.getTextContent());
						}
					}else
						weatherMap.put(childNode.getNodeName(), childNode.getTextContent());
				}
				weathers.add(weatherMap);
			}
			//System.out.println("Total results: " + results.getLength());
			map.put("current_condition", currentCondMap);
			map.put("weathers", weathers);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	private Map parseXMLStringCurrDay(String xmlString) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Map map = new HashMap();
		try {
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(new StringReader(
					xmlString)));
			// Document document = builder.parse(wsResponse.toString());
		//	//System.out.println("wsResponse::" + xmlString);
		//	//System.out.println("getTextContent::" + document.getXmlVersion());

			Element docEle = document.getDocumentElement();

			// Print root element of the document
		//	//System.out.println("Root element of the document: "
		//			+ docEle.getNodeName());

			//This is working fine for current tag 
			NodeList currCondResults = docEle.getElementsByTagName("current_condition");

			for (int i = 0; i < currCondResults.getLength(); i++) {
				Node node = currCondResults.item(i);
			//	//System.out.println("NodeName()	 : 	NodeValue");
				for (int j = 0; j < node.getChildNodes().getLength(); j++) {
					Node childNode = node.getChildNodes().item(j);
				//	//System.out.println(childNode.getNodeName() + ":"
				//			+ childNode.getTextContent());
					map.put(childNode.getNodeName(), childNode.getTextContent());
				}
				////System.out.println("----------------------");
			}			
			// Print total  elements in document
			//System.out.println("Total results: " + currCondResults.getLength());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public List addCity(String city) {
		// "http://www.worldweatheronline.com/feed/search.ashx?key=ad61101387174311100203&feedkey=3f82ae28f8084719112504&query=pune&num_of_results=20&format=xml"

		String url = "http://www.worldweatheronline.com/feed/search.ashx?key=ad61101387174311100203&feedkey=3f82ae28f8084719112504&query="+city+"&num_of_results=20&format=xml";
		
		
		
		////System.out.println(url.toString());
		//url.insert(url.lastIndexOf("{0}"), "gopal");
		////System.out.println(parseXMLStringAddCity(getResponseFromServer(addCityUrl)));
			//parseXMLStringAddCity(getResponseFromServer(addCityUrl));
		return parseXMLStringAddCity(getResponseFromServer(url));
	}

	
	public Map getWeatherOfFiveDays(String lat, String lon) {
		// "http://www.worldweatheronline.com/feed/premium-weather-v2.ashx?key=ad61101387174311100203&tp=24&feedkey=3f82ae28f8084719112504&lat=48.450&lon=34.980&format=xml&num_of_days=5"
		String url = "http://www.worldweatheronline.com/feed/premium-weather-v2.ashx?key=ad61101387174311100203&tp=24&feedkey=3f82ae28f8084719112504&lat="+lat+"&lon="+lon+"&format=xml&num_of_days=5";
		
		//StringBuilder url = new StringBuilder(fiveDaysWeatherUrl);
		//System.out.println(url.toString());
		/*url.lastIndexOf("{0}");
		url.replace(url.lastIndexOf("{0}"), url.lastIndexOf("{0}")+3 , lat);
		
		url.lastIndexOf("{0}");
		url.replace(url.lastIndexOf("{1}"), url.lastIndexOf("{1}")+3 , lon);
		*/
		//System.out.println(url.toString());
	return parseXMLStringFiveDaysWeather(getResponseFromServer(url));
	}

	public Map getWeatherOfCurrDay(String lat, String lon) {
		// "http://www.worldweatheronline.com/feed/premium-weather-v2.ashx?key=ad61101387174311100203&feedkey=3f82ae28f8084719112504&format=xml&lat=48.450&lon=34.980&fx=no"
		/*StringBuilder url = new StringBuilder(currentDayWeather);
		//System.out.println(url.toString());
		url.lastIndexOf("{0}");
		url.replace(url.lastIndexOf("{0}"), url.lastIndexOf("{0}")+3 , lat);
		
		url.lastIndexOf("{0}");
		url.replace(url.lastIndexOf("{1}"), url.lastIndexOf("{1}")+3 , lon);
		*/
		//System.out.println(url.toString());
		
		String url = "http://www.worldweatheronline.com/feed/premium-weather-v2.ashx?key=ad61101387174311100203&feedkey=3f82ae28f8084719112504&format=xml&lat="+lat+"&lon="+lon+"&fx=no";
		
		return parseXMLStringCurrDay(getResponseFromServer(url));
	}

	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		WSCallAndParsing ws = new WSCallAndParsing();
		////System.out.println(ws.addCity(""));
		List cities = ws.addCity("pune");
		System.out.println("---"+cities);
		ws.getWeatherOfCurrDay("lat","long");
		ws.getWeatherOfFiveDays("lat","long");
	}

}
