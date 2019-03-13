package meteo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateUtil
{

    private static Calendar cal;
    
    private static String dfString = "dd-MM-yyyy HH:mm";
//    private static String SQLFormatString = "SYYYY/MM/DD HH24:MI";    
//    private static String SQLdfString = "yyyy/MM/dd HH:mm"; 
    private static String shortdfString = "dd-MM-yyyy";    
    private static String shortHourString = "dd-MM HH:mm";
    
    private static SimpleDateFormat df = new SimpleDateFormat(dfString);
    private static SimpleDateFormat UTCdf = new SimpleDateFormat(dfString);    
//    private static SimpleDateFormat sqldf = new SimpleDateFormat(SQLdfString);    
    
    private static SimpleDateFormat shortdf = new SimpleDateFormat(shortdfString); 
    private static SimpleDateFormat shortHourdf = new SimpleDateFormat(shortHourString);
//    private static SimpleDateFormat hdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
    
    static 
    {
        UTCdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /** @return Today date at 00:00 time. */
    public static Date today()
    {
        cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE,      0);
        cal.set(Calendar.SECOND,      0);
        cal.set(Calendar.MILLISECOND, 1);
        
        return cal.getTime();    
    }

    public static Date thisHour()
    {
        cal = Calendar.getInstance();
        
        return roundHour(cal.getTime());    
    }
    
    public static Date roundMunite(Date date)
    {
        return new Date(roundMunite(date.getTime()));
    }
    
    public static long roundMunite(long date)
    {
        cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
//        cal.set(Calendar.MINUTE,      cal.get(Calendar.MINUTE) - cal.get(Calendar.MINUTE)%10);
        cal.set(Calendar.SECOND,      0);
        cal.set(Calendar.MILLISECOND, 0);
        
        return cal.getTimeInMillis();    
    }  
    
    public static Date round10Munites(Date date)
    {
        return new Date(round10Munites(date.getTime()));
    }
    
    public static long round10Munites(long date)
    {
        cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        cal.set(Calendar.MINUTE,      cal.get(Calendar.MINUTE) - cal.get(Calendar.MINUTE)%10);
        cal.set(Calendar.SECOND,      0);
        cal.set(Calendar.MILLISECOND, 0);
        
        return cal.getTimeInMillis();    
    }  
    
    public static Date roundHour(Date date)
    {
        return new Date(roundHour(date.getTime()));
    }
    
    public static long roundHour(long date)
    {
        cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        cal.set(Calendar.MINUTE,      0);
        cal.set(Calendar.SECOND,      0);
        cal.set(Calendar.MILLISECOND, 0);
        
        return cal.getTimeInMillis();    
    }  
      
    public static Date roundDay(Date date)
    {
        return new Date(roundDay(date.getTime()));
    }       

    public static long roundDay(long date)
    {
        cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE,      0);
        cal.set(Calendar.SECOND,      0);
        cal.set(Calendar.MILLISECOND, 0);
        
        return cal.getTimeInMillis();    
    }
    
    /** 
     * @param time
     * @return Array of times for 24 hour summary list. 
     */
    public static long getRound6Hours(long time)
    {
        
        cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) - cal.get(Calendar.HOUR_OF_DAY)%6);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        return cal.getTimeInMillis(); 
        
    }
    
    /** 
     * @param time
     * @return Array of times for 24 hour summary list. 
     */
    public static long getRound12Hours(long time)
    {
        cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) - cal.get(Calendar.HOUR_OF_DAY)%12);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        return cal.getTimeInMillis(); 
    }

    public static long getRound3Hours(long time)
    {
        
        cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) - cal.get(Calendar.HOUR_OF_DAY)%3);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        return cal.getTimeInMillis(); 
        
    }
   
    public static Date now()
    {
        return new Date(System.currentTimeMillis());
    }    
    
    /** @param date 
     * @return Minimal time of day + date at given date. */ 
    public static long getMinDailyTime(long date)
    {

        cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE,      0);
        cal.set(Calendar.SECOND,      0);
        cal.set(Calendar.MILLISECOND, 0);
        
        return cal.getTimeInMillis();
    }
    
    /** @param date 
     * @return Maximal time of day + date at given date. */ 
    public static long getMaxDailyTime(long date)
    {
        cal = Calendar.getInstance();
        
        cal.setTimeInMillis(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE,      59);
        cal.set(Calendar.SECOND,      59);
        cal.set(Calendar.MILLISECOND, cal.getActualMaximum(Calendar.MILLISECOND));
        
        return cal.getTimeInMillis();
    }

    /** @param date 
     * @return Minimal date in month. */ 
    public static long getMinMonthlyTime(long date)
    {

        cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE,      0);
        cal.set(Calendar.SECOND,      0);
        cal.set(Calendar.MILLISECOND, 0);
        
        return cal.getTimeInMillis();
    }
    
    /** @param date 
     * @return Maximal date in month. */ 
    public static long getMaxMonthlyTime(long date)
    {
        cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE,      59);
        cal.set(Calendar.SECOND,      59);
        cal.set(Calendar.MILLISECOND, cal.getActualMaximum(Calendar.MILLISECOND));
        
        return cal.getTimeInMillis();
    }
    
    /** @param date 
     * @return Minimal date in year. */ 
    public static long getMinYearlyTime(long date)
    {
        cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        cal.set(Calendar.MONTH, cal.getActualMinimum(Calendar.MONTH));
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getActualMinimum(Calendar.MILLISECOND));        
        return cal.getTimeInMillis();
    }
    
    /** @param date 
     * @return Maximal date in year. */ 
    public static long getMaxYearlyTime(long date)
    {
        cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        cal.set(Calendar.MONTH, cal.getActualMaximum(Calendar.MONTH));
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, cal.getActualMaximum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getActualMaximum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getActualMaximum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getActualMaximum(Calendar.MILLISECOND));
        
        return cal.getTimeInMillis();
    }
    
    public static Date getMaxDailyTime  (Date date) { return new Date(getMaxDailyTime  (date.getTime())); }
    public static Date getMinDailyTime  (Date date) { return new Date(getMinDailyTime  (date.getTime())); }
    public static Date getMaxMonthlyTime(Date date) { return new Date(getMaxMonthlyTime(date.getTime())); }
    public static Date getMinMonthlyTime(Date date) { return new Date(getMinMonthlyTime(date.getTime())); }
    public static Date getMaxYearlyTime (Date date) { return new Date(getMaxYearlyTime (date.getTime())); }
    public static Date getMinYearlyTime (Date date) { return new Date(getMinYearlyTime (date.getTime())); }
    
    public static long getDayOffset(long date, int offset)
    {
        cal = Calendar.getInstance();
        cal.setTimeInMillis(date);  
        cal.add(Calendar.DAY_OF_MONTH, offset);
        
        return cal.getTimeInMillis();
    } 
    
    public static long getHourOffset(long date, int offset)
    {
        cal = Calendar.getInstance();
        cal.setTimeInMillis(date);  
        cal.add(Calendar.HOUR_OF_DAY, offset);
        
        return cal.getTimeInMillis();
    }    

    public static long getMinuteOffset(long date, int offset)
    {
        cal = Calendar.getInstance();
        cal.setTimeInMillis(date);  
        cal.add(Calendar.MINUTE, offset);
        
        return cal.getTimeInMillis();
    }    
  
    public static long getSecondOffset(long date, int offset)
    {
        cal = Calendar.getInstance();
        cal.setTimeInMillis(date);  
        cal.add(Calendar.SECOND, offset);
        
        return cal.getTimeInMillis();
    }  
    
    public static Date getDayOffset(Date date, int offset)
    {
        return new Date(getDayOffset(date.getTime(), offset));
    }
    
    public static Date getHourOffset(Date date, int offset)
    {
        return new Date(getHourOffset(date.getTime(), offset));
    } 
       
    public static Date getMinuteOffset(Date date, int offset)
    {
        return new Date(getMinuteOffset(date.getTime(), offset));
    }   
    
    //recieves time in mili seconds and returns it in seconds. 
    public static long convertToSeconds(long miliTime){
    	return miliTime/1000;
    }
 
    public static Date getSecondOffset(Date date, int offset)
    {
        return new Date(getSecondOffset(date.getTime(), offset));
    }                
/* Formatting functions ***********************************************************************/    

    public static String toString(Date date)
    {
        return df.format(date);
    } 

    public static String toUTCString(Date date)
    {
        return UTCdf.format(date);
    } 
    public static String toUTCString(long date) { return toUTCString(new Date(date)); } 

    public static String toShortString(Date date)
    {
        return shortdf.format(date);
    }     
    public static Date parse(String source)
    {
        Date date = null;
        try { date = df.parse(source); }
        catch(ParseException e) { date = null; }
        
        return date;
    }     
    
    public static String toString(long date)
    {
        return toString(new Date(date));
    } 
    
    public static String[] toString(long[] dates){
    	String[] strings = new String[dates.length];
    	for (int d = 0; d < dates.length; d++) 
			strings[d] = toString(dates[d]);
    	
    	return strings;
    }
    
    public static String toShortString(long date)
    {
        return toShortString(new Date(date));
    }     
    
    public static String toShortHourString(long date)
    {
    	return toShortHourString(new Date(date));
    }
    
    public static String toShortHourString(Date date)
    {
    	return shortHourdf.format(date);
    }
    
    public static String getFormatString()
    {
        return dfString;
    }
}