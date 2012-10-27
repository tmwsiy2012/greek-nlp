package com.eddiedunn.util;

import java.util.concurrent.TimeUnit;

public class StopWatch {

	private static final String FORMAT = "%02d:%02d:%02d";	
	long mark;
	String creationMessage;
	
	public StopWatch(String creationMessage){
		this.creationMessage = creationMessage;
		mark =  System.currentTimeMillis();
	}
	
    public long elapsedTime() {
        long now = System.currentTimeMillis();
        return (now - mark) ;
    } 	
    public void printElapsedTime(){
    	System.out.println("Elapsed time since "+creationMessage+": "+parseTime(elapsedTime()));
    }
	private String parseTime(long milliseconds) {
	      return String.format(FORMAT,
	              TimeUnit.MILLISECONDS.toHours(milliseconds),
	              TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(
	              TimeUnit.MILLISECONDS.toHours(milliseconds)),
	              TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(
	              TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
	   }	
}
