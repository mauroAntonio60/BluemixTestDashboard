/**
 * 
 */
package com.ibm.dashboard.tag;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.ibm.dashboard.UrlResponse;
import com.ibm.dashboard.UrlResponseDoc;
import com.ibm.dashboard.singleton.DashboardData;
import com.ibm.dashboard.singleton.RefreshData;
import com.ibm.dashboard.store.UrlStatusPersisted;
import com.ibm.dashboard.store.UrlStatusPersistedStore;
import com.ibm.dashboard.store.UrlStatusPersistedStoreFactory;

/**
 * @author mareksadowski
 *
 */
public class PresentDashboardTag extends SimpleTagSupport {

	// private String refreshTime;
	//// TODO: CR 1.1 custom tag to replace the code
	private String NUMBER_OF_DISPLAYED_RESULTS;// = 12;
	private String TRESHOLD_TO_YELLOW;// = 1000;

	/**
	 * @param nUMBER_OF_DISPLAYED_RESULTS
	 *            the nUMBER_OF_DISPLAYED_RESULTS to set
	 */
	public void setNUMBER_OF_DISPLAYED_RESULTS(String nUMBER_OF_DISPLAYED_RESULTS) {
		this.NUMBER_OF_DISPLAYED_RESULTS = nUMBER_OF_DISPLAYED_RESULTS;
		System.out.println(
				"PresentDashboard TAG: setting NUMBER_OF_DISPLAYED_RESULTS: " + this.NUMBER_OF_DISPLAYED_RESULTS);
	}

	/**
	 * @param tRESHOLD_TO_YELLOW
	 *            the tRESHOLD_TO_YELLOW to set
	 */
	public void setTRESHOLD_TO_YELLOW(String tRESHOLD_TO_YELLOW) {
		this.TRESHOLD_TO_YELLOW = tRESHOLD_TO_YELLOW;
		System.out.println("PresentDashboard TAG: setting TRESHOLD_TO_YELLOW: " + this.TRESHOLD_TO_YELLOW);
	}

	public PresentDashboardTag() {
		System.out.println("PresentDashboard TAG: the constructor");
	}

	@Override
	public void doTag() throws JspException, IOException {
		// tag comes here

		System.out.println("PresentDashboard TAG: do tag = start with NUMBER_OF_DISPLAYED_RESULTS = " + NUMBER_OF_DISPLAYED_RESULTS 
				+ "Yellow " + TRESHOLD_TO_YELLOW );

		int NUMBER_OF_DISPLAYED_RESULTS = Integer.parseInt(this.NUMBER_OF_DISPLAYED_RESULTS);
		int TRESHOLD_TO_YELLOW = Integer.parseInt(this.TRESHOLD_TO_YELLOW);

		JspWriter out = getJspContext().getOut();

		DashboardData dashboardData = DashboardData.getInstance();
		RefreshData refreshData = RefreshData.getInstance();
		UrlStatusPersistedStore store = UrlStatusPersistedStoreFactory.getInstance();

		if (store == null) {
			System.out.println("PresentDashboard TAG: no store defined! Showing instructions to create Cloudant");
			out.println("<br> NO STORE DEFINED - DEFINE LOCAL STORE IN WEB-INF/CLASSES/cloudant-properties \n"
					+ "<br> OR BIND THE CLOUDANT DB TO THE SERVICE IF IT IS DEPLOYED ON BLUEMIX:\n"
					+ "<br> cf create-service \"cloudantNoSQLDB Dedicated\" \"Shared Dedicated\" NAME_OF_THE_CLOUDANT_SERVICE\n"
					+ "<br> cf bind-service NAME_OF_THIS_APPLICATION NAME_OF_THE_CLOUDANT_SERVICE\n" + " <br><br>");
		} else {

			String message2Print = "The next scheduled refresh will take place at " + dashboardData.nextRun
					+ " Pacific time";
			if (dashboardData.nextRun != null && dashboardData.nextRun != "") {
				out.println("<br><b>" + message2Print + "</b><BR>");
			}
			// get the _id of the record from the url parameter :
			// working with the dashboarddata singleton cache instead of pulling
			// the data from DB direct
			out.println("<table>");
			// get the singleton
			// get the latest json
			// parse json

			// TODO do only display of data
			System.out.print("Refreshed the jsp page with urls: ");
			for (UrlStatusPersisted doc : dashboardData.getUrls()) {
				if (doc.getUrl() != null) {

					out.println("<tr><td>\n" + doc.getName() + "</td>");
					try {
						// iterate through the statuses
						// request to show only NUMBER_OF_DISPLAYED_RESULTS
						int max = (doc.urlResponses).length;

						// CR rev1.0 max 12 runs
						if (max > NUMBER_OF_DISPLAYED_RESULTS) {
							max = NUMBER_OF_DISPLAYED_RESULTS;
							System.out.println("CR - showing only first " + NUMBER_OF_DISPLAYED_RESULTS + " calls");
						}
						// CR rev0.4 adding time counting in minutes
						long time = 0;
						String userInput = doc.urlTimes[0];
						String expectedPattern = "yyyy-MM-dd HH:mm:SS z"; // from
																			// UrlStatus
						SimpleDateFormat formatter = new SimpleDateFormat(expectedPattern);
						// System.out.println("now time: ");
						Date datetimeZero = new Date();

						Date date;
						String dateString = "";
						long milisecondsZero = datetimeZero.getTime();
						long milisecondsTest;
						for (int i = 0; i < max; i++) {

							// cr rev0.4 adding time...

							try {
								// (2) give the formatter a String that matches
								// the SimpleDateFormat pattern
								userInput = doc.urlTimes[i];
								// System.out.println("old call time: ");
								date = formatter.parse(userInput);
								// rev 1.0 setting the refresh time
								DateFormat df = new SimpleDateFormat("HH:mm");
								df.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
								dateString = df.format(date);

								// (3) prints out "Tue Sep 22 00:00:00 EDT 2009"
								// System.out.println(dateString);
							} catch (Exception e) {
								System.out.println(e);
								date = datetimeZero;
							}
							milisecondsTest = date.getTime();
							time = (milisecondsZero - milisecondsTest) / (1000 * 60);

							// skip nulls
							// TODO: remove initial nulls
							if (doc.urlResponses[i] == null) {
								// System.out.println("skipping null values");
							} else if ("HTTP/1.1 200 OK".contentEquals(doc.urlResponses[i])) {

								// CR rev1.1 skip pretty when there is an
								// exception
								// CR rev0.4 pretty forming information about
								// the call
								String prettyInfo = "";
								String jsonString;

								jsonString = doc.urlLogTails[i].replaceAll("\"", "'");
								int totalResponseTime = 0;
								try {
									// 1 time - in minutes from the time 0
									prettyInfo = "request date: " + doc.urlTimes[i] + "\n";
									// 2 info on the seperate calls
									prettyInfo = prettyInfo + "operation responses for: " + "\n";

									// System.out.println("ok - for :"+
									// jsonString);

									UrlResponseDoc urlResponseDoc = new UrlResponseDoc(jsonString);
									// UrlResponse[] urlResponses =
									// urlResponseDoc.getOperationArray();
									UrlResponse[] urlResponses = urlResponseDoc.getUrlResponses();
									for (int y = 0; y < urlResponses.length; y++) {
										prettyInfo = prettyInfo + "\n";
										prettyInfo = prettyInfo + urlResponses[y].getType() + "\n";
										prettyInfo = prettyInfo + " - time: " + urlResponses[y].getResponse_time()
												+ "\n";
										prettyInfo = prettyInfo + " - code: " + urlResponses[y].getResponse_code()
												+ "\n";

										prettyInfo = prettyInfo + " - details:"
												+ urlResponses[y].getDescString().replaceAll("\"", "'") + "\n";

										totalResponseTime = totalResponseTime + urlResponses[y].getResponse_time();
									}
									// before info: doc.urlTimes[i] + "\n" +
									// doc.urlLogTails[i]

									prettyInfo = "total response time: " + totalResponseTime + "\n" + prettyInfo;

									// getting out a good response
									// green under the treshold
									// yellow outside of the treshold
									out.println("<td align=center><img src="
											+ ((totalResponseTime < TRESHOLD_TO_YELLOW) ? "'./img/ok.png' alt='ok' "
													: "'./img/yellow.png' alt='too long' ")
											+ "title=\"" + prettyInfo + "\" /><br>" + time + "<BR>" + dateString
											+ " </td>");
								} catch (Exception e) {
									// TODO: CR 1.1 when there is a problem with
									// the parsed json (gson exception etc do
									// not fail the page

									// there was an exception while parsing of
									// the returned json
									// just mark it yellow and get the json
									// without parsing
									System.out.println("pretty info exception - not parsing json for log no " + i);
									e.printStackTrace();
									jsonString = doc.urlLogTails[i].replaceAll("\'", "\\\'");
									jsonString = jsonString.replaceAll("\"", "'");
									prettyInfo = jsonString;
									// there is an exception - so marking it
									// yellow
									out.println("<td align=center><img src="
											+ "'./img/yellow.png' alt='exception while parsing' " + "title=\""
											+ prettyInfo + "\"  /><br>" + time + "<BR>" + dateString + " </td>");
								}
							} else {
								System.out.println("fail - for " + i);
								out.println("<td align=center><img src='./img/fail.png' alt='fail' title=\""
										+ doc.urlTimes[i] + "\n" + doc.urlResponses[i] + "\" /><br> " + time + "<BR>"
										+ dateString + "</td>");

								// System.out.println("fail - for "+i);

							}
						}

					} finally {
						// System.out.println("end");
						System.out.print(".");
					}

					out.println("</tr>");

				}
			}
		}
		out.println("</table>");
		// System.out.println("custom tag --- end");
		// TODO: CR 1.1 custom table builder tag --end
	}

}
