package MyPackage;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

/**
 * Servlet implementation class MyServlet
 */
@WebServlet("/MyServlet")
public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MyServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String city= request.getParameter("city");
		
		//Setup API
		String api_key="c0c855015a08dbc49195d1c519beb1b9";
		
		//Creating URL for OpenWeather API request
		String apiURL="https://api.openweathermap.org/data/2.5/weather?q=" +   city + "&appid=" + api_key;
		
		try {
			//API Integration
			@SuppressWarnings("deprecation")
			URL url = new URL(apiURL);
			HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod("GET");
			
			//Reading data from OpenWeather
			InputStream inputStream= httpURLConnection.getInputStream();
			InputStreamReader reader=new InputStreamReader(inputStream);
			
			//Storing data into String
			StringBuilder responseContent = new StringBuilder();
			
			//Scanning data
			Scanner scanner=new Scanner(reader);
			
			while(scanner.hasNext()) {
				responseContent.append(scanner.nextLine());
			}
			scanner.close();
			
			//Typecasting = parsing data into JSON to extract date,time, temperature
			Gson gson=new Gson();
			JsonObject jsonObject=gson.fromJson(responseContent.toString(),JsonObject.class);
			
			long dateTimestamp= jsonObject.get("dt").getAsLong() * 1000; 
			String date =new Date (dateTimestamp).toString(); 
   //Temperature 
			double temperatureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
			int temperatureCelsius =(int) (temperatureKelvin + 273.15);
			//Humidity
			int humidity =jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
			//Wind Speed
			double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
			//Weather Condition
      String weather=jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString()	;	 
			
      // Set the data as request attributes (for sending to the jsp page)
      request.setAttribute("date", date);
      request.setAttribute("city", city);
      request.setAttribute("temperature", temperatureCelsius);
      request.setAttribute("weatherCondition", weather); 
      request.setAttribute("humidity", humidity);    
      request.setAttribute("windSpeed", windSpeed);
      request.setAttribute("weatherData", responseContent.toString());
   
      httpURLConnection.disconnect();
		
	}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // Forward the request to the weather.jsp page for rendering
        request.getRequestDispatcher("index.jsp").forward(request, response);
       
	}

}
