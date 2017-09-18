/**
 * 
 */
package com.ibm.dashboard.tag;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.SkipPageException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.ibm.dashboard.singleton.DashboardData;
import com.ibm.dashboard.singleton.RefreshData;
import com.ibm.dashboard.store.SchedulerSettings;
import com.ibm.dashboard.store.SchedulerSettingsStore;
import com.ibm.dashboard.store.SchedulerSettingsStoreFactory;

/**
 * @author mareksadowski
 *
 */
public class RefreshTimeTag extends SimpleTagSupport {
	
	private String refreshTime;
	
	/**
	 * @param refreshTime the refreshTime to set
	 */
	public void setRefreshTime(String refreshTime) {
		this.refreshTime = refreshTime;
		System.out.println("Refresh Time TAG: setting refresh time: " + this.refreshTime);
	}

	public RefreshTimeTag() {
		//System.out.println("Refresh Time TAG: the constructor");
	}
	
	@Override
	public void doTag() throws JspException, IOException {
		int refreshRate = -10;
		DashboardData dashboardData = DashboardData.getInstance();
		RefreshData refreshData = RefreshData.getInstance();

		
		PageContext pageContext = (PageContext) getJspContext();  
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		String foundRefreshTime = request.getParameter("time");
		if ((foundRefreshTime != null) && (!foundRefreshTime.isEmpty()) && (foundRefreshTime != "")) {
			System.out.println("Refresh Time TAG: found time: " + foundRefreshTime);
			//setting refresh in the background
			refreshRate = Integer.parseInt(foundRefreshTime);
			refreshTime = foundRefreshTime;
			System.out.println("Refresh Time TAG: setting refreshing for " + refreshRate + "min");

			//TODO: CR 0.4 get refresh schedule from DB

			SchedulerSettingsStore schedulerStore = SchedulerSettingsStoreFactory.getInstance();
			if (schedulerStore == null) {
				System.out.println("Refresh Time TAG: no store defined!");
				try {
					getJspContext().getOut().write("Refresh Time TAG: no store defined!");
				} catch (Exception e) {
					e.printStackTrace();
					// stop page from loading further by throwing SkipPageException
					//throw new SkipPageException("Exception in RefreshTime Tag with " + refreshTime);
					System.out.println("Refresh Time TAG: Exception with " + refreshTime);
				}
			} else {
			//TODO: CR 0.4 get refresh schedule from DB

				SchedulerSettings schedulerSettings;
				Collection<SchedulerSettings> col = schedulerStore.getAll();
				if (col != null && col.size() > 0) {
					//System.out.println("Refresh Time TAG: Found the scheduler in the db!");
					//get the first one
					schedulerSettings = (SchedulerSettings) (col.toArray())[0];
					schedulerSettings.setRefreshTime(refreshRate);
					schedulerStore.update(schedulerSettings.get_id(), schedulerSettings);
					System.out.println("Refresh Time TAG: updated the scheduler " + schedulerSettings.get_id() + " in the db with refresh rate: " + refreshRate);

				} else {
					System.out.println("Refresh Time TAG: There are no settings in the DB - creating a scheduler record");
					schedulerSettings = new SchedulerSettings();

					schedulerSettings.setRefreshTime(refreshRate);
					schedulerStore.persist(schedulerSettings);
					System.out.println("Refresh Time TAG: persisted the scheduler in the db with refresh rate: " + refreshRate);

				}
			}
			
			refreshData.isRefreshing = true;
			dashboardData.setRefreshRate(Integer.parseInt(foundRefreshTime) * 60);
			refreshData.refreshingData();

			// TODO: clear the time parameter 
			try {
				getJspContext().getOut().write(refreshTime);
				pageContext = (PageContext) getJspContext();  
				HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
				response.sendRedirect("index.jsp");			
			} catch (Exception e) {
				e.printStackTrace();
				// stop page from loading further by throwing SkipPageException
				//throw new SkipPageException("Exception in RefreshTime Tag with " + refreshTime);
				System.out.println("Refresh Time TAG: Exception in RefreshTime Tag with " + refreshTime);
			}
			
		} 
		
	}

}
