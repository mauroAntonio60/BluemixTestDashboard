/**
 * 
 */
package com.ibm.dashboard.singleton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author mareksadowski
 *
 */
public class RefreshData {

	private static final RefreshData instance = new RefreshData();
	public boolean isRefreshing = false;
	public boolean shouldBeRunning = false;

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	// public boolean isRunning = false;
	public int changeTime = 0;
	// public int previousTime = 0;
	// rev 0.5 stabilizing scheduler
	// public int noOfThreads = 0;

	/**
	 * 
	 */
	protected RefreshData() {
		// TODO Auto-generated constructor stub
		int refreshRate = 0;
		// TODO rev0.4 dashboard data refresh from db
		DashboardData dashboard = DashboardData.getInstance();
		refreshRate = dashboard.getRefreshRate();
		if (refreshRate > 0) {
			// rev 0.5 stabilizing scheduler
			// setting up scheduler
			// isRefreshing = true;
			shouldBeRunning = true;
			System.out.println("launching refresh rate from DashboardData on launch with the refresh: " + refreshRate);
			refreshingData();
		} else {
			// rev 0.5 stabilizing scheduler
			// single call on start
			refreshingData();
			shouldBeRunning = false;
		}

	}

	public void refreshingData() {
		// rev 0.5 stabilizing scheduler
		System.out.println("RefreshData entering refresh");
		refreshCycle();
		System.out.println("RefreshData left refresh");

	}

	// Runtime initialization
	// By default ThreadSafe
	public static RefreshData getInstance() {
		return instance;
	}

	public void refreshCycle() {
		int refreshRate = 0; //1 minute
		final DashboardData myDashboardData = DashboardData.getInstance();
		refreshRate = myDashboardData.getRefreshRate();
		System.out.println("launching refresh rate every: " + refreshRate);
		if (changeTime == 0) {
			//rev 0.5 stabilizing scheduler
			System.out.println("first refresh? setting changeTime to: " + refreshRate);
			changeTime = refreshRate;
			//rev 0.5 stabilizing scheduler 
			//single call on start
			shouldBeRunning = false;
			//isRunning = false;
		}
		//rev 0.5 stabilizing scheduler
		if ((changeTime == refreshRate)&&(refreshRate>0)) {
			//rev 0.5 stabilizing scheduler 
			//setting up scheduler
			//isRefreshing = true;
			shouldBeRunning = true;
			//isRunning = true;
		}
		if (refreshRate == 0) {
			System.out.println("Refresh Data run only once");
			myDashboardData.refreshData();
			//rev 0.5 stabilizing scheduler 
			//setting up scheduler
			shouldBeRunning = false;
			
		} else if ((shouldBeRunning)&&(!isRefreshing)) { //rev 0.5 stabilizing scheduler 
			System.out.println("creating new thread!");
			//previousTime = changeTime;
			//isRunning = false;
			final Runnable updateData = new Runnable() {
				public void run() {
					System.out.println("inside runnable data");

					// get the refreshRate
					if (myDashboardData.getRefreshRate() > 0) {
						System.out.println("refreshing data inside runnable");
						//rev 0.5 stabilizing scheduler
						// refreshData
						myDashboardData.refreshData();
						//rev 1.0 setting the refresh time
						DateFormat df = new SimpleDateFormat("HH:mm");
						df.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
						myDashboardData.nextRun = df.format(new Date().getTime() + myDashboardData.getRefreshRate()*1000);
						//Date nextRun = new Date(new Date().getTime() + myDashboardData.getRefreshRate()*1000);
						//myDashboardData.nextRun = nextRun.getHours() + ":" + nextRun.getMinutes();

					} else {
						//rev 0.5 stabilizing scheduler
						// turnoff Refresh
						isRefreshing = false;
						shouldBeRunning = false;
						//isRunning = false;
						System.out.println("Refresh Data run only once - value 0!!!!!!!!");
						myDashboardData.refreshData();
					}
				}
			};
			
			//starting scheduler
			if ((shouldBeRunning)&&(!isRefreshing)) { //rev 0.5 stabilizing scheduler
				System.out.println("Refresh Data: turning on refreshing for "+ refreshRate/60 +" minutes");
				
				//rev 0.5 stabilizing scheduler
				isRefreshing = true;
				final ScheduledFuture<?> updateDataHandle = scheduler
						.scheduleAtFixedRate(updateData, 0, refreshRate,
						TimeUnit.SECONDS);
				//noOfThreads = noOfThreads + 1;
				//System.out.println("Added the thread: Number of threads = " + noOfThreads);
				scheduler.schedule(new Runnable() {
					public void run() {
						System.out.println("verify if about to cancel refreshing");
						updateDataHandle.cancel(true);
						//rev 0.5 stabilizing scheduler
						isRefreshing = false;
						System.out.println("cancelled scheduler ");
						//rev 0.5 stabilizing scheduler
						if(shouldBeRunning) {
							System.out.println(" and  rerunning a new scheduler");
							System.out.println("Keeping the thread alive");
							//: Number of threads = " + noOfThreads);
							refreshCycle();
						} else {
							System.out.println("NOT rerunning the scheduler");							
							//isRunning = true;
							//noOfThreads = noOfThreads - 1;
							//System.out.println("After stopping one thread - Number of threads = " + noOfThreads);
						}
					}				
				}, 
				//rev 0.5 stabilizing scheduler
				//running for cycle 30 sec
				//60 * 60,
				//just enough to run it second time
						(refreshRate*2 - 5),
				TimeUnit.SECONDS); // refreshed rev 0.5
			} else {
				System.out.println("Refresh Data end - run once");
				//rev 0.5 stabilizing scheduler
				//isRefreshing = false;
				//isRefreshing = true;
			}
		}

	}

}
