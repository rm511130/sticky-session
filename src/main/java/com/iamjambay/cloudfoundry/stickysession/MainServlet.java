package com.iamjambay.cloudfoundry.sessionapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.io.FileWriter;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import javax.servlet.http.HttpSession;

public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public MainServlet() {
	}

	protected void printCookies(HttpServletRequest request, HttpServletResponse response, PrintWriter writer)
			throws ServletException, IOException 
	{
		Cookie[] cookies = request.getCookies();
		boolean sticky = false;
                int counter;
                  
		if ("true".equals(request.getParameter("sticky")) ) { HttpSession session = request.getSession(true); sticky = true; }

		if (cookies != null) {  HttpSession session = request.getSession();
                                        try { counter = (Integer) session.getAttribute("counter"); } catch( Exception e ) { counter=0; }
                                        counter = counter + 1;
                                        session.setAttribute("counter", counter);
                                        writer.println("Counter = " + counter +"<br/><br/>");
                                        writer.println("Cookies: <br/><br/>");
			                for (int i = 0; i < cookies.length; i++) { String name = cookies[i].getName();
				                                                   String value = cookies[i].getValue();
				                                                   if ("__VCAP_ID__".equals(name)) sticky = true;
				                                                   writer.println(name + " = "+ value +"<br/>");
			                                                         }
			                writer.println("<br/>");
		                     }
		
		if (sticky) { writer.println("Sticky session is enabled. Refreshing the browser will keep routing to the same app instance.<br/><br/>");
                              writer.println("<a href='?clearcookies=true'>Clear Cookies JSESSIONID & __VCAP_ID__ </a>" + "<br/><br/>");
                            }
		else { 	writer.println("Sticky session is NOT enabled. Refreshing the browser will route to random app instances.");
		        writer.println("<br/><br/><a href='?sticky=true'>Start a Sticky Session</a>" + "<br/><br/>");
		     }
	}

	protected void printAppEnv(HttpServletRequest request, HttpServletResponse response, PrintWriter writer)
			throws ServletException, IOException 
	{
                String vcap = System.getenv("VCAP_APPLICATION");
                JSONParser parser = new JSONParser();
                Object obj_vcap = null;
                try { obj_vcap = parser.parse(vcap); }   catch( Exception e ) { System.out.println( "Error occured :" + e ); }
                JSONObject json_vcap = (JSONObject) obj_vcap;
                String appname = (String) json_vcap.get("name");
                Long instanceIndex = (Long) json_vcap.get("instance_index");
                
               writer.println("Application Name: " + appname + "<br/>"); 
               writer.println("            Port: " + System.getenv("PORT") + "<br/>");
               writer.println("  Instance Index: " + instanceIndex + "<br/><br/>"); 
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException 
	{
                response.setContentType("text/html");
		response.setStatus(200);
		PrintWriter writer = response.getWriter();
		if ("true".equals(request.getParameter("shutdown"))) { System.exit(1);	}
		if ("true".equals(request.getParameter("clearcookies"))) { Cookie[] cookies = request.getCookies(); 
                                                                           if (cookies != null) {  for (int i = 0; i < cookies.length; i++)
                                                                                                   {
                                                                                                     Cookie cookie = cookies[i];
                                                                                                     cookies[i].setValue(null);
                                                                                                     cookies[i].setMaxAge(0);
                                                                                                     response.addCookie(cookie);
              /*       response.sendRedirect("/"); i*/    
                                                                                                  }
                                                                                                }
                                                                         }
		writer.println("<a href='?shutdown=true'>Kill this App Instance</a>" + "<br/><br/>");
		printCookies(request, response, writer);
		printAppEnv(request, response, writer);
		writer.close();
	}
}
