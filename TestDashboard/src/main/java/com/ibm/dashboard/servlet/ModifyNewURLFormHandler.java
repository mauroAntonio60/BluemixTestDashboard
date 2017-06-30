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
import com.ibm.dashboard.store.UrlStatusPersisted;
import com.ibm.dashboard.store.UrlStatusPersistedStore;
import com.ibm.dashboard.store.UrlStatusPersistedStoreFactory;

/**
 * Servlet implementation class ModifyNewUrlFormHandler
 */
@WebServlet("/ModifyUrlFormHandler")
public class ModifyNewURLFormHandler extends HttpServlet {
	private static String FORWARD_TO_PAGE = "/manageUrls.jsp";
	UrlStatusPersistedStore store = UrlStatusPersistedStoreFactory.getInstance();

	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ModifyNewURLFormHandler() {
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
		
		response.getWriter().append("Modifying URL in the DB- Served at: ").append(request.getContextPath());
		// TODO: rev0.4 table : do we modify or delete
		String paramName = "modify";
		
		//get the parameters
		String id = request.getParameter(paramName);
		if (id!= null && id != "") {
			
			System.out.println(id);
			//update the db
			paramName = "url." + id;
			String url = request.getParameter(paramName);
			paramName = "get." + id;
			Boolean isGet = "true".equalsIgnoreCase(request.getParameter(paramName));
			paramName = "url_name." + id;
			String urlName = request.getParameter(paramName);
			paramName = "json.txt." + id;
			String jsonText = request.getParameter(paramName);
			
			System.out.println("url to modify: " + url);
			System.out.println("url_name to modify: " + urlName);
			System.out.println("isGet to modify: " + isGet);
			System.out.println("json.text to modify: " + jsonText);
			if (store == null) {
				System.out.println("store is null!!!");
			} else {
				System.out.println("the store is not null.");
				try {
					UrlStatusPersisted urlStatusPersisted = store.get(id);

					// TODO parse the sslOn status
					urlStatusPersisted.sslOn = false;
					//keeping old values
					//urlStatusPersisted.setUrlLogTails(emptyArray);
					//urlStatusPersisted.setUrlResponses(emptyArray);
					//urlStatusPersisted.setUrlTimes(emptyArray);
					urlStatusPersisted.url = url;
					urlStatusPersisted.name = urlName;
					urlStatusPersisted.isGet = isGet;
					urlStatusPersisted.jsonText = jsonText;

					store.update(id, urlStatusPersisted);
					System.out.println("ModifyNewURLFormHandler: updated url..." + urlName + " , id: " + id);
					
					// TODO: reset the cache
					DashboardData dashboardData = DashboardData.getInstance();
					dashboardData.resetUrls();
					// TODO: rev1.0 add a message: Schedule changes will take effect at XX:XX:XXX Pacific time
					// get the XX:XX:XXX Pacific time from the RefreshData
					String modifyMessage = "The URL modified successfully. Changes will take effect at "
					+ dashboardData.nextRun + " Pacific time";
					// adding session
					// Retrieve the current session. Create one if not exists
					HttpSession session = request.getSession(true);
					//adding the message
					session.setAttribute("message", modifyMessage);
					
				} catch (NoDocumentException e) {
					System.out.println(e.getError());
				}
			}
		} else {
			// TODO: rev0.4 table : do we modify or delete
			paramName = "delete";
			//get the parameters
			id = request.getParameter(paramName);
			
			if (id!= null && id != "") {
			
				System.out.println("ModifyNewURLFormHandler: url to delete: " + id);
				if (store == null) {
					System.out.println("ModifyNewURLFormHandler: store is null!!!");
				} else {
					System.out.println("ModifyNewURLFormHandler: the store is not null.");
					try {
						store.delete(id);
						System.out.println("ModifyNewURLFormHandler: deleted url...");
						// TODO: reset the cache
						DashboardData dashboardData = DashboardData.getInstance();
						dashboardData.resetUrls();
						// TODO: rev1.0 add a message: Schedule changes will take effect at XX:XX:XXX Pacific time
						// get the XX:XX:XXX Pacific time from the RefreshData
						String deleteMessage = "The URL deleted successfully. Changes will take effect at "
						+ dashboardData.nextRun + " Pacific time";
						// adding session
						// Retrieve the current session. Create one if not exists
						HttpSession session = request.getSession(true);
						//adding the message
						session.setAttribute("message", deleteMessage);
						
						
					} catch (NoDocumentException e) {
						System.out.println(e.getError());
					}
				}
			} else {
				System.out.println("ModifyNewURLFormHandler: something went wrong id found in delete or modify ???");
			}
		}
		
		//return back to the dashboard
		
		//getting back to the page who potentially called this servlet
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
