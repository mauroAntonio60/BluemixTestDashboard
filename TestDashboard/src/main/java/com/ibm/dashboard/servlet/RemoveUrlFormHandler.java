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
import com.ibm.dashboard.store.UrlStatusPersistedStore;
import com.ibm.dashboard.store.UrlStatusPersistedStoreFactory;

/**
 * Servlet implementation class RemoveUrlFormHandler
 */
@WebServlet("/RemoveUrlFormHandler")
public class RemoveUrlFormHandler extends HttpServlet {

	private static String FORWARD_TO_PAGE = "/manageUrls.jsp";
	private static final long serialVersionUID = 1L;

	UrlStatusPersistedStore store = UrlStatusPersistedStoreFactory.getInstance();

	/**
	 * Default constructor.
	 */
	public RemoveUrlFormHandler() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Removing urls to monitoring the status provided as the url ids (from db)
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Deleting URLs from the DB - Served at: ").append(request.getContextPath());
		String paramName = "urls";
		String[] urls = request.getParameterValues(paramName);
		for (String url_id : urls) {
			if (url_id != null) {
				System.out.println("url to delete: " + url_id);
				if (store == null) {
					System.out.println("store is null!!!");
				} else {
					System.out.println("the store is not null.");
					try {
						store.delete(url_id);
						System.out.println("deleted url...");
						// TODO: reset the cache
						DashboardData dashboardData = DashboardData.getInstance();
						dashboardData.resetUrls();
						// TODO: rev1.0 insert a message: Schedule changes will take effect at XX:XX:XXX Pacific time
						// get the XX:XX:XXX Pacific time from the RefreshData
						String deleteMessage = "The URL deleted (delete servlet) successfully. Changes will take effect at XX:XX:XXX Pacific time";
						// adding session
						// Retrieve the current session. Create one if not exists
						HttpSession session = request.getSession(true);
						//adding the message
						session.setAttribute("message", deleteMessage);
						
						
					} catch (NoDocumentException e) {
						System.out.println(e.getError());
					}
				}
			}
		}
		//getting back to the page who potentially called this servlet
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(FORWARD_TO_PAGE);
		dispatcher.forward(request, response);

	}

}
