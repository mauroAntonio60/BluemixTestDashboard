package com.ibm.dashboard.servlet;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.dashboard.singleton.RefreshData;

/**
 * Servlet implementation class PingHandler
 */
@WebServlet("/PingHandler")
public class PingHandler extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PingHandler() {
        super();
        // TODO Auto-generated constructor stub
    }

	 public void init() throws ServletException
	    {
		 // liberty <webContainer deferServletLoad="false"/>
	          System.out.println("----------");
	          System.out.println("---------- init successfully ----------");
	          System.out.println("----------");
	          // TODO: rev0.4 self schedule from DB settings
	          RefreshData initRefreshScheduler = RefreshData.getInstance();
	          System.out.println("----------");
	          System.out.println("---------- Refresh Scheduler initialized successfully ----------");
	          System.out.println("----------");
	    }
    

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		response.getWriter().append("{service: 'dashboard', operations:[], response_code: 200, desc:' Served at: ").append(request.getContextPath()).append("Error: no connection to Cloudant'}");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
