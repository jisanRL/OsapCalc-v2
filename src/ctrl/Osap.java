package ctrl;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Osap
 */
@WebServlet({ "/Osap", "/Osap/*"  })						// * means -> all url with osap  
public class Osap extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Osap() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
		
		// task 7 (p-2), to respond to the client with a message  
		response.setContentType("text/plain");
		Writer resOut = response.getWriter();
		resOut.write("\n");														// optional for new line 
		resOut.write("Hello, World!\n");
		
		String clientIP = request.getRemoteAddr();								// client IP
		resOut.write("Client IP: " + clientIP + "\n");
		
		int cPort = request.getLocalPort();										// client port   or  request.getServerPort();[check this one as well]
		resOut.write("Client Port: " + cPort + "\n");
		resOut.write("This IP has been flagged!\n");
		
		String cProtocol = request.getProtocol();		    					// protocol 
		resOut.write("Client Protocol: " + cProtocol + "\n");
		
		String cMethod = request.getMethod();									// method 
		resOut.write("Client Method: " + cMethod + "\n");
		
		
		String clientQueryString = request.getQueryString();					// querystring   
		String foo = request.getParameter("foo");								// [check this part]
		resOut.write("Query String: " + clientQueryString + "\n");  			// [check this part
		resOut.write("Query Param foo= " + foo + "\n");
		
//		check this part if its needed 
		String uri = request.getRequestURI().toString(); 						// URI 
		resOut.write("Request URI: " + uri + "\n");
		
		
		String servletPath = request.getServletPath();							// servlet path [this reads the web.xml file]
		resOut.write("Request Servlet Path: " + servletPath + "\n");
		
		ServletContext context = this.getServletContext();
		resOut.write("---Application info---\n");
		
		String appName = this.getServletContext().getInitParameter("applicationName");					// application name
		resOut.write("Application Name= " + appName + "\n");
		
		String applicant = this.getServletContext().getInitParameter("applicantName");					// applicant Name
		resOut.write("Applicant Name= " + applicant + "\n");
		
		// input from web.xml [this are the built-in/hard-coded values]
		double default_principal = Double.parseDouble(this.getServletContext().getInitParameter("principal"));  // this reads the web.xml file and returns the parameter value of the param-name
		double default_interest = Double.parseDouble(this.getServletContext().getInitParameter("interest"));
		double default_period =  Double.parseDouble(this.getServletContext().getInitParameter("period"));
		double default_gracePeriod =  Double.parseDouble(this.getServletContext().getInitParameter("grace period"));
		
		// task B: servlet retrieving data from the form, [user input the values]
		double principal = Double.parseDouble(request.getParameter("principal"));
		double userInterest = Double.parseDouble(request.getParameter("interest"));				
		double period =  Double.parseDouble(request.getParameter("period")); 			// grace period   
		
		
		double sPrincipal, sPeriod, dInterest, grace;
		double fixedInterest = Double.parseDouble(this.getServletContext().getInitParameter("fixed interest"));  // fixed interest 
		double gracePeriod = Double.parseDouble(this.getServletContext().getInitParameter("grace period"));
		
		double interest = fixedInterest + userInterest;
		double graceInterest = principal * ((interest / 100)/12) * gracePeriod;
		
//		double graceInterest = (principal * ((interest + fixedInterest) / 12) * gracePeriod);				// check this out 
		
		// input from query String [these are the user input values]
		if (request.getParameterMap().isEmpty()) {
			 sPrincipal = default_principal;    		// the default values 
			 sPeriod = default_period; 							
			 dInterest = default_interest;		
		} else {
			 sPrincipal = principal;							// the user values
			 sPeriod = period;
			 dInterest = interest;     			// check this part again 
		}

//		check this part if its needed 
		String contextPath = context.getContextPath();								// context path 
		resOut.write("Context Path= " + contextPath + "\n");
		
		String realPath = context.getRealPath("Osap");				  				// real path
		resOut.write("Real Path= " + realPath + "\n");
		

		// task D osap monthly payments, make parameters period, interest in web.xml 		
		resOut.write("Based on Principal = " + sPrincipal);
		resOut.write(" period=" + sPeriod);
		resOut.write(" Interest=" + dInterest +"\n");
		
		
		// the formula for osap calculation 
		double calc = (((dInterest/100) / 12) * sPrincipal) / (1 - Math.pow(1 + ((dInterest/100) / 12), -sPeriod));
		double payment = calc + (graceInterest/gracePeriod);
		resOut.write("Monthly payments: " + payment);

		
		// task E : save session, how do you get the data from servlet into the results page
		HttpSession session = request.getSession();
		request.getServletContext().setAttribute("GI", graceInterest);
		request.getServletContext().setAttribute("PAY", payment);
		
		request.getSession().setAttribute("GI", graceInterest);			
		request.getSession().setAttribute("PAY", payment);

	
		String target = "/UI.jspx"; 				// this sets the path to UI.jspx instead of Osap.java
		String resultPage = "/Results.jspx";

		// task B and E to distinguish between a fresh visit and a submission visit
		if (request.getParameter("calculate") == null) {
			request.getRequestDispatcher("/UI.jspx").forward(request, response);
		} else {
			request.getRequestDispatcher("/Results.jspx").forward(request, response); 
		}
		
		
		
			
//		debugs 
		System.out.println("default_principal = " + default_principal);
		System.out.println("default_period =  " + default_period);
		System.out.println("default_interest =  " + default_interest);
		
		System.out.println("principal " + principal);
		System.out.println("period " + period);
		System.out.println("interest " + interest);
		System.out.println("------------------------------------------");
		System.out.println("Grace Period = " + gracePeriod);
		System.out.println("Grace Interest =  " + graceInterest);
		System.out.println("Grace Interest =  " + default_gracePeriod);
		System.out.println("------------------------------------------");
		System.out.println("target and request = " + request);
		System.out.println("Fixed interst = " + fixedInterest);
//		System.out.println("Overall interest = " + overallInterest);
		System.out.println("------------------------------------------");
		System.out.println("Final calc: " + payment);
		System.out.println("------------------------------------------");
		System.out.println("Content length = " + request.getContentLength() + "\n" + "Content type = " + request.getContentType());
		System.out.println("Hello, Got a " + cMethod + " request from Osap!");    // task7
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}



