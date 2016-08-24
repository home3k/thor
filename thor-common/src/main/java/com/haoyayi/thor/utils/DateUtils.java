/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.FastDateFormat;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public class DateUtils {

    public static final FastDateFormat sdf = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
    public static final FastDateFormat sdfDate = FastDateFormat.getInstance("yyyy-MM-dd");
    public static final FastDateFormat sdfTime = FastDateFormat.getInstance("HH:mm:ss");
    public static final List<String> weekDay=new ArrayList<String>();

    public static String getDate(String date, long day) {
	    	try {
	    		Date parsedDate = sdfDate.parse(date);
	    		return sdfDate.format(parsedDate.getTime() + day * 24 * 60 * 60 * 1000);
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    		return "";
	    	}
    }
    
    public static String getDateOffsetSecond(String date, long second) {
    	  try {
              Date parsedDate = sdfDate.parse(date);
              return sdf.format(parsedDate.getTime() + second * 1000);
          } catch (Exception e) {
              e.printStackTrace();
              return "";
          }
    }

    	public static void main(String[] args) {
    		System.out.println(addDays(new Date(), 1));
			System.out.println(getDateOffsetSecond("2015-02-03", 1));
			
		}
    
    public static long getUnixtime(String datetime) {
        try {
            Date parsedDate = sdf.parse(datetime);
            return parsedDate.getTime()/1000;
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public static long getOffsetUnixDate(String datetime, long offset) {
        return getUnixtime(getOffsetDate(datetime, offset));
    }

    public static String getOffsetDate(String datetime, long offset) {

        try {
            Date parsedDate = sdf.parse(datetime);
            return sdf.format(parsedDate.getTime() + offset * 60 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getOffsetTime(String datetime, long offset) {

        try {
            Date parsedDate = sdf.parse(datetime);
            return sdfTime.format(parsedDate.getTime() + offset * 60 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static Date parseDate(String datetime) {
        try {
            Date parsedDate = sdf.parse(datetime);
            return parsedDate;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    
    public  static String formateDate(Date date){
	    	if (date==null)
	    		return null;
	    	return sdf.format(date);
    }

    public  static String formateDate2(Date date){
        if (date==null)
            return null;
        return sdfDate.format(date);
    }

    public static Date parseDateBySdfDate(String datetime){
        try {
            Date parsedDate = sdfDate.parse(datetime);
            return parsedDate;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static Date parseDateBySdfTime(String datetime){
        try {
            Date parsedDate = sdfTime.parse(datetime);
            return parsedDate;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getWeekEndDate(String startDay) {

        try {
            Date parsedDate = sdfDate.parse(startDay);
            return sdfDate.format(parsedDate.getTime() +  6*24*60*60 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getWeekDay(String date){
        try {
            Date parsedDate = sdfDate.parse(date);
            int  day=parsedDate.getDay();
            return weekDay.get(day);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static boolean isWeekEnd(String date) {
    	Calendar calendar = Calendar.getInstance();
    	try {
			calendar.setTime(sdfDate.parse(date));
			int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
			return dayOfWeek == 1 || dayOfWeek == 7;
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
    }
    
    public static String addDays(Date date, int days) {
    		Date d = org.apache.commons.lang3.time.DateUtils.addDays(date, days);
    		return formateDate2(d);
    }

    static {
        weekDay.add("日");
        weekDay.add("一");
        weekDay.add("二");
        weekDay.add("三");
        weekDay.add("四");
        weekDay.add("五");
        weekDay.add("六");
    }

}
