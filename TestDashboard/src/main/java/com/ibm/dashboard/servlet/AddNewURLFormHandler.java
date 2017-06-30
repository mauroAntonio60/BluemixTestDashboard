package com.ibm.dashboard.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cloudant.client.org.lightcouch.NoDocumentException;
import com.ibm.dashboard.singleton.DashboardData;
import com.ibm.dashboard.singleton.RefreshData;
import com.ibm.dashboard.store.UrlStatusPersisted;
import com.ibm.dashboard.store.UrlStatusPersistedStore;
import com.ibm.dashboard.store.UrlStatusPersistedStoreFactory;

/**
 * Servlet implementation class AddNewURLFormHandler
 */
@WebServlet("/AddNewURLFormHandler")
public class AddNewURLFormHandler extends HttpServlet {
	private static String FORWARD_TO_PAGE = "/manageUrls.jsp";
	UrlStatusPersistedStore store = UrlStatusPersistedStoreFactory.getInstance();

	private static final long serialVersionUID = 1L;


	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AddNewURLFormHandler() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Adding a new Url to monitor the status - Served at: ")
				.append(request.getContextPath());

		String paramName = "url";
		String url = request.getParameter(paramName);
		paramName = "get";
		Boolean isGet = "true".equalsIgnoreCase(request.getParameter(paramName));
		String json = "";
		if (!isGet) {
			paramName = "json.txt";
			json = request.getParameter(paramName);			
		}
		System.out.println("json.txt to add: " + json);
		paramName = "url_name";
		String urlName = request.getParameter(paramName);
		System.out.println("url to add: " + url);
		if (store == null) {
			System.out.println("store is null!!!");
		} else {
			System.out.println("the store is not null.");
			try {
				UrlStatusPersisted urlStatusPersisted = new UrlStatusPersisted();

				String[] emptyArray = new String[1];

				// TODO parse the sslOn status
				urlStatusPersisted.sslOn = false;
				urlStatusPersisted.setUrlLogTails(emptyArray);
				urlStatusPersisted.setUrlResponses(emptyArray);
				urlStatusPersisted.setUrlTimes(emptyArray);
				urlStatusPersisted.url = url;
				urlStatusPersisted.name = urlName;
				urlStatusPersisted.isGet = isGet;
				urlStatusPersisted.setJsonText(json);

				store.persist(urlStatusPersisted);
				System.out.println("added url...");
				
				// TODO: reset the cache
				DashboardData dashboardData = DashboardData.getInstance();
				dashboardData.resetUrls();
				// TODO: rev1.0 add a message: Schedule changes will take effect at XX:XX:XXX Pacific time
				// get the XX:XX:XXX Pacific time from the RefreshData
				String addMessage = "The URL added successfully. Changes will take effect at " + dashboardData.nextRun + " Pacific time";
				// adding session
				// Retrieve the current session. Create one if not exists
				HttpSession session = request.getSession(true);
				//adding the message
				session.setAttribute("message", addMessage);
				
			} catch (NoDocumentException e) {
				System.out.println(e.getError());
			}
		}

		// getting back to the page who potentially called this servlet
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(FORWARD_TO_PAGE);
		dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
