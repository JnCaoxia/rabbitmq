package com.caox.sharding.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 日期操作工具类（新）
 */
public class DateExtendUtil {

	public final static long SECOND = 1000;
	public final static long MINUTE = 60 * SECOND;
	public final static long HOUR = 60 * MINUTE;
	public final static long DAY = 24 * HOUR;

	public final static String YEAR_PART = "yyyy";
	public final static String MONTH_PART = "MM";
	public final static String DATE_PART = "dd";
	public final static String HOUR_PART = "HH";
	public final static String MINUTE_PART = "mm";
	public final static String SECOND_PART = "ss";
	public final static String MILlISECOND_PART = "SSS";

	public final static String SMALL_DATE_FORMAT = "yyyy-MM-dd";
	public final static String FULL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public final static String MILLIS_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	public final static String UTC_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	/**
	 * 
	 * @param str
	 *           字符串转日期（年月日）
	 * @return
	 */
	public static Date parseString2Date(String dateStr) throws Exception {
		Date dt = null;
		try {
			dt = new SimpleDateFormat(MILLIS_DATE_FORMAT).parse(dateStr);
		} catch (ParseException e0) {
			try {
				dt = new SimpleDateFormat(FULL_DATE_FORMAT).parse(dateStr);
			} catch (ParseException e1) {
				try {
					dt = new SimpleDateFormat(SMALL_DATE_FORMAT).parse(dateStr);
				} catch (ParseException e2) {
					throw new Exception(e2);
				}
			}
		}
		return dt;
	}
	
	/**
	 * 将当前日期转换到秒的格式 格式如2012-12-12 12:12:12
	 * 
	 * @param formatString
	 * @return
	 */
	public static String getNowFullDate() {
		return formatDate2FullDateString(new Date());
	}

	/**
	 * 将当前日期转换到日的格式 如2012-12-12
	 * 
	 * @param formatString
	 * @return
	 */
	public static String getNowSmallDate() {
		return formatDate2SmallDateString(new Date());
	}

	/**
	 * 将日期转换成指定到秒格式的字符串
	 * 
	 * @param date
	 * @param formatString
	 * @return
	 */
	public static String formatDate2FullDateString(Date date) {
		return formatDate2String(date, FULL_DATE_FORMAT);
	}
	
	public static String formatDate2FullDateString(String dateStr) throws Exception {
		Date date = parseString2Date(dateStr);
		return formatDate2String(date, FULL_DATE_FORMAT);
	}

	/**
	 * 将日期Str转换成UTC格式的时间字符串
     */
	public static String formatDate2UTCDateString(String dateStr) throws Exception {
		Date date = parseString2Date(dateStr);
		return formatDate2String(date, UTC_DATE_FORMAT);
	}

	/**
	 * 将日期转换成指定到日格式的字符串
	 * 
	 * @param date
	 * @param formatString
	 * @return
	 */
	public static String formatDate2SmallDateString(Date date) {
		return formatDate2String(date, SMALL_DATE_FORMAT);
	}
	
	public static String formatDate2SmallDateString(String dateStr) throws Exception {
		Date date = parseString2Date(dateStr);
		return formatDate2String(date, SMALL_DATE_FORMAT);
	}

	/**
	 * 将日期转换成指定格式的字符串
	 * 
	 * @param date
	 * @param formatString
	 * @return
	 */
	public static String formatDate2String(Date date, String formatString) {
		return new SimpleDateFormat(formatString).format(date);
	}
	
	public static String formatDate2String(String dateStr, String formatString) throws Exception {
		Date date = parseString2Date(dateStr);
		return new SimpleDateFormat(formatString).format(date);
	}

	/**
	 * 
	 * @param datePart
	 *            日期操作部分 "day"为日，"month"为月，"year"为年
	 * @param count
	 *            日期数
	 * @param date
	 *            日期
	 * @return 日期加法结果
	 */
	public static Date dateAdd(String datePart, int count, Date date) // 日期加法操作
	{
		Date resultDate = null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		GregorianCalendar gc = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),
				cal.get(Calendar.SECOND));
		datePart = datePart.toLowerCase();
		if ("day".equals(datePart)) // 日
		{
			gc.add(Calendar.DATE, count);
			resultDate = (Date) gc.getTime();
		} else if ("month".equals(datePart)) // 月
		{
			gc.add(Calendar.MONTH, count);
			resultDate = (Date) gc.getTime();
		} else if ("year".equals(datePart)) // 年
		{
			gc.add(Calendar.YEAR, count);
			resultDate = (Date) gc.getTime();
		} else if ("hour".equals(datePart)) // 小时
		{
			gc.add(Calendar.HOUR, count);
			resultDate = (Date) gc.getTime();
		} else if ("minute".equals(datePart)) // 分钟
		{
			gc.add(Calendar.MINUTE, count);
			resultDate = (Date) gc.getTime();
		} else if ("second".equals(datePart)) // 秒
		{
			gc.add(Calendar.SECOND, count);
			resultDate = (Date) gc.getTime();
		}
		return resultDate;
	}
	
	public static String dateAdd(String datePart, int count, String date, String dateFormat) throws Exception // 日期加法操作
	{
		Date resultDate = null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(parseString2Date(date));
		GregorianCalendar gc = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),
				cal.get(Calendar.SECOND));
		datePart = datePart.toLowerCase();
		if ("day".equals(datePart)) // 日
		{
			gc.add(Calendar.DATE, count);
			resultDate = (Date) gc.getTime();
		} else if ("month".equals(datePart)) // 月
		{
			gc.add(Calendar.MONTH, count);
			resultDate = (Date) gc.getTime();
		} else if ("year".equals(datePart)) // 年
		{
			gc.add(Calendar.YEAR, count);
			resultDate = (Date) gc.getTime();
		} else if ("hour".equals(datePart)) // 小时
		{
			gc.add(Calendar.HOUR, count);
			resultDate = (Date) gc.getTime();
		} else if ("minute".equals(datePart)) // 分钟
		{
			gc.add(Calendar.MINUTE, count);
			resultDate = (Date) gc.getTime();
		} else if ("second".equals(datePart)) // 秒
		{
			gc.add(Calendar.SECOND, count);
			resultDate = (Date) gc.getTime();
		}
		return formatDate2String(resultDate, dateFormat);
	}

	/**
	 * 获取本月1号的日期字符串，比如2012-12-01
	 * 
	 * @return
	 */
	public static String getFirstDayOfMonth() {
		String ret = "";
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, 1); // 设为本月1号
		ret = formatDate2SmallDateString(cal.getTime());
		return ret;
	}
	
	/**
	 * 获取本周第一天
	 * @return
	 */
	public static String getFirstDayOfWeek() {
		String ret = "";
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, 2);	//设为本周第一天
		ret = formatDate2SmallDateString(cal.getTime());
		return ret;
	}

	/**
	 * 获取下一天（明天）
	 * 
	 * @return
	 */
	public static String getNextDay() {
		String ret = "";
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1); // 日期数加1
		ret = formatDate2SmallDateString(cal.getTime());
		return ret;
	}

	/**
	 * 获取前一天（昨天）
	 *
	 * @return
	 */
	public static String getYesterday() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1); // 日期数加1
		String ret = formatDate2SmallDateString(cal.getTime());
		return ret;
	}

	/**
	 * 日期的加法操作
	 * 
	 * @param datePart
	 * @param count
	 * @param date
	 * @return
	 */
	public static Timestamp dateAddTimestamp(String datePart, int count, Timestamp date) // 日期加法操作
	{
		Timestamp resultDate = null;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(date.getTime());
		GregorianCalendar gc = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),
				cal.get(Calendar.SECOND));
		datePart = datePart.toLowerCase();
		if ("day".equals(datePart)) // 日
		{
			gc.add(Calendar.DATE, count);
			resultDate = new Timestamp(gc.getTimeInMillis());
		} else if ("month".equals(datePart)) // 月
		{
			gc.add(Calendar.MONTH, count);
			resultDate = new Timestamp(gc.getTimeInMillis());
		} else if ("year".equals(datePart)) // 年
		{
			gc.add(Calendar.YEAR, count);
			resultDate = new Timestamp(gc.getTimeInMillis());
		} else if ("hour".equals(datePart)) // 小时
		{
			gc.add(Calendar.HOUR, count);
			resultDate = new Timestamp(gc.getTimeInMillis());
		} else if ("minute".equals(datePart)) // 分钟
		{
			gc.add(Calendar.MINUTE, count);
			resultDate = new Timestamp(gc.getTimeInMillis());
		} else if ("second".equals(datePart)) // 秒
		{
			gc.add(Calendar.SECOND, count);
			resultDate = new Timestamp(gc.getTimeInMillis());
		}
		return resultDate;
	}

	/**
	 * 日期加法操作 如果被加日期小于现在，则从现在开始加上
	 * 
	 * @param datePart
	 * @param count
	 * @param date
	 * @return
	 */
	public static Timestamp dateAddTimestampFromNow(String datePart, int count, Timestamp date) // 日期加法操作
	{
		Timestamp timeNow = new Timestamp(new Date().getTime());
		if (date.before(timeNow))
			date = timeNow;
		return dateAddTimestamp(datePart, count, date);
	}

	/**
	 * --------------------------added at
	 * 2009-12-3-----------------------------------------------
	 */
	/**
	 * 计算两个日期的月份差
	 * 
	 * @param cal
	 *            被减数
	 * @param cal1
	 *            减数
	 * @return
	 */
	public static int getMonthDiff(Calendar cal, Calendar cal1) {
		int m = (cal.get(Calendar.MONTH)) - (cal1.get(Calendar.MONTH));
		int y = (cal.get(Calendar.YEAR)) - (cal1.get(Calendar.YEAR));
		return y * 12 + m;
	}

	/**
	 * 计算日期相差天数
	 * 
	 * @param fDate
	 * @param oDate
	 * @return
	 */
	public static int daysOfTwo(Date fDate, Date oDate) {
		Calendar aCalendar = Calendar.getInstance();
		aCalendar.setTime(fDate);
		int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);
		aCalendar.setTime(oDate);
		int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
		return day2 - day1;
	}

	/**
	 * 获取两个日期的差值 date2-date1
	 * 
	 * @param datePart
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static long dateDiff(String datePart, Date date1, Date date2) {

		long ret = 0;

		// 年
		if ("year".equalsIgnoreCase(datePart) || "y".equalsIgnoreCase(datePart)) {
			ret = (date2.getTime() - date1.getTime()) / (365 * 24 * 60 * 60 * 1000);
			return ret;
		}
		// 月
		if ("month".equalsIgnoreCase(datePart) || "m".equalsIgnoreCase(datePart)) {
			ret = (date2.getTime() - date1.getTime()) / (30 * 24 * 60 * 60 * 1000);
			return ret;
		}
		// 日
		if ("day".equalsIgnoreCase(datePart) || "d".equalsIgnoreCase(datePart)) {
			ret = (date2.getTime() - date1.getTime()) / (1 * 24 * 60 * 60 * 1000);
			return ret;
		}
		// 时
		if ("hour".equalsIgnoreCase(datePart) || "h".equalsIgnoreCase(datePart)) {
			ret = (date2.getTime() - date1.getTime()) / (1 * 60 * 60 * 1000);
			return ret;
		}
		// 分
		if ("minute".equalsIgnoreCase(datePart) || "min".equalsIgnoreCase(datePart)) {
			ret = (date2.getTime() - date1.getTime()) / (1 * 60 * 1000);
			return ret;
		}
		// 秒
		if ("second".equalsIgnoreCase(datePart) || "s".equalsIgnoreCase(datePart)) {
			ret = (date2.getTime() - date1.getTime()) / (1 * 1000);
			return ret;
		}
		//
		return ret;
	}

	/**
	 * 获取两个日期的差值 date2-date1 日期格式为字符串
	 * 
	 * @param datePart
	 * @param date1
	 * @param date2
	 * @return
	 * @throws Exception
	 */
	public static long dateDiff(String datePart, String date1, String date2) throws Exception {

		return dateDiff(datePart, parseString2Date(date1), parseString2Date(date2));
	}

	/**
	 * 获取两个日期的差值（天数） date2-date1 日期格式为字符串
	 * 
	 * @param datePart
	 * @param date1
	 * @param date2
	 * @return
	 * @throws Exception
	 */
	public static long dateDayDiff(String date1, String date2) throws Exception {

		return dateDiff("day", parseString2Date(date1), parseString2Date(date2));
	}

	/**
	 * 获取两个日期的差值（月数） date2-date1 日期格式为字符串
	 * 
	 * @param datePart
	 * @param date1
	 * @param date2
	 * @return
	 * @throws Exception
	 */
	public static long dateMonthDiff(String date1, String date2) throws Exception {

		return dateDiff("month", parseString2Date(date1), parseString2Date(date2));
	}

	/**
	 * 获取两个日期的差值（年数） date2-date1 日期格式为字符串
	 * 
	 * @param datePart
	 * @param date1
	 * @param date2
	 * @return
	 * @throws Exception
	 */
	public static long dateYearDiff(String date1, String date2) throws Exception {

		return dateDiff("year", parseString2Date(date1), parseString2Date(date2));
	}

	/**
	 * 获取当前时间戳
	 * 
	 * @return
	 */
	public static Timestamp getNowTimestamp() {
		return new Timestamp(new Date().getTime());
	}

	/**
	 * 获取过去的多少天的时间
	 * 
	 * @author zhour
	 * @version 2012-11-14 下午4:02:12
	 * @param passDay
	 *            passDay 过去1天：-1
	 * @param formatString
	 *            返回时间格式
	 * @return
	 */
	public static String getPassByDate(int passDay, String formatString) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, passDay);
		//
		return new SimpleDateFormat(formatString).format(cal.getTime());
	}

	/**
	 * 获取UTC时间
	 * 
	 * @param opDate
	 * @return
	 */
	public static Date getUTCDate(Date opDate) {

		// 1、取得本地时间：
		Calendar cal = Calendar.getInstance();
		cal.setTime(opDate);

		// 2、取得时间偏移量：
		int zoneOffset = cal.get(Calendar.ZONE_OFFSET);

		// 3、取得夏令时差：
		int dstOffset = cal.get(Calendar.DST_OFFSET);

		// 4、从本地时间里扣除这些差量，即可以取得UTC时间：
		cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));

		return new Date(cal.getTimeInMillis());
	}

	/**
	 * 将String转Timestamp
	 * 
	 * @author zhour
	 * @version 2012-12-14 上午11:33:46
	 * @param dateTime
	 * @return
	 */
	public static Timestamp getTimestampForString(String dateTime) {
		return Timestamp.valueOf(dateTime);
	}
	
	/**
	 * 获取两个日期间所有日期的集合
	 * @param beginDate
	 * @param endDate
	 * @param dateFormat
	 * @return
	 */
	public static List<String> getAllBetweenTwoDate(String beginDate, String endDate, String dateFormat) {
		Calendar startCalendar = Calendar.getInstance();
		Calendar endCalendar = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat(dateFormat);
		Date start = null;
		Date end = null;
		try {
			start = df.parse(beginDate);
			end = df.parse(endDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		startCalendar.setTime(start);
		endCalendar.setTime(end);
		List<String> dateList = new ArrayList<String>();
		while (true) {
			startCalendar.add(Calendar.DAY_OF_MONTH, 1);
			if (startCalendar.getTimeInMillis() <= endCalendar.getTimeInMillis()) {// TODO
				dateList.add(df.format(startCalendar.getTime()));
			} else {
				break;
			}
		}
		return dateList;
	}
	
	/**
	  * 周计数,为全年中的周计数
	  * @param year
	  * @param weekCount
	  * @return
	  */
	public static List<String> getWeekDateByWeekInYear(int year, int weekCount) {
		List<String> returnDayList = new ArrayList<String>();
		Calendar cal = Calendar.getInstance(Locale.CHINA);
		// 设置calendar的日期,此处可以确定某一天
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.WEEK_OF_YEAR, weekCount);
		// 获取该天的信息(理论而言,通过改变get函数的参数,可以获取该天的任意信息)
		int day = cal.get(Calendar.DAY_OF_YEAR);
		int[] days = new int[7];
		for (int i = 0; i < 7; i++) {
			days[i] = cal.get(Calendar.DAY_OF_MONTH);
			String dayStr = (cal.get(Calendar.YEAR)) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH);
			if (!returnDayList.contains(dayStr)) {
				returnDayList.add(dayStr);
			}
			day++;
			cal.set(Calendar.DAY_OF_YEAR, day);
		}
		return returnDayList;
	}    
	
	/**
	 * 得到一天是一年中的第几周     
	 */
	public static String getWeekForYear(String date) {
		Calendar cal = Calendar.getInstance(Locale.CHINA);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			cal.setTime(sdf.parse(date));
		} catch (Exception e) {
			e.printStackTrace();
		}
		int week = cal.get(Calendar.WEEK_OF_YEAR);
		int year = cal.get(Calendar.YEAR);
		return year+"-"+week;
	}
	
	
	/**
	 * 获取两个日期之间的周
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public static List<String> getAllBetweenTwoWeek(String beginDate, String endDate) {
		List<String> weekLists = new ArrayList<String>();
		//
		List<String> days = getAllBetweenTwoDate(beginDate, endDate, SMALL_DATE_FORMAT);
		for(String day : days) {
			if(!weekLists.contains(day)) {
				weekLists.add(day);
			}
		}
		//
		return weekLists;
	}
	
	/**
	 * 设置转换时间格式，可自行添加
	 */
	@SuppressWarnings("serial")
	private static final Map<Integer,String> FTimeType=new HashMap<Integer, String>(){{
		put(0, "yyyy-MM-dd HH:mm:ss");		
		put(1, "yyyy-MM-dd");
	}};
	
	/**
	 * 比较时间相差天数
	 * -1起始时间大于结束时间	
	 *  0同一天 
	 *  -2 时间格式异常
	 */
    public long compare_date(String start, String end, int TimeType) {
		try {            
			DateFormat df = new SimpleDateFormat(FTimeType.get(TimeType));//日期格式控制
			Date dt1 = df.parse(start);
			Date dt2 = df.parse(end);
			if (dt1.getTime() > dt2.getTime()) { 
				return -1;            //	"start 在end后面"
			} else if (dt1.getTime() < dt2.getTime()) {    
				return  (dt2.getTime()-dt1.getTime())/(24*60*60*1000);          //相差天数
			} else {     
				return 0;      
			}        //同一天
		} catch (Exception exception) {
			//exception.printStackTrace();        
			return -2;
		}        
	}
    
    /**
     * 获取日期之间月相差值
     * @param begin_time
     * @param end_time
     * @return
     */
    public static List<String> getBaseListByMonth(String begin_time, String end_time) {
    	List<String> returnList = new ArrayList<String>();
    	//
    	List<String> dayList = getAllBetweenTwoDate(begin_time, begin_time, DateExtendUtil.SMALL_DATE_FORMAT);
    	for(String day : dayList) {
    		day = day.substring(0,7);
    		if(!returnList.contains(day)) {
    			returnList.add(day);
    		}
    	}
    	//
    	return returnList;
    }
    
    /**
  	 * 获取日期之间周相差值
  	 * @param start
  	 * @param end
  	 * @param calendarType
  	 * @return
  	 */
  	public static List<String> getBaseListByWeek(Calendar c_begin, Calendar c_end){
  		List<String> ret = new ArrayList<String>();
  		
  		DateFormatSymbols dfs = new DateFormatSymbols();
  		String[] weeks = dfs.getWeekdays();
  		int count = 1;
  		int temp = 1;
  		String tempStr = "";
  		//
  		String timeStr = DateExtendUtil.formatDate2SmallDateString(c_begin.getTime());
  		String endStr = DateExtendUtil.formatDate2SmallDateString(c_end.getTime());
  		//
  		c_end.add(Calendar.DAY_OF_YEAR, 1); // 结束日期下滚一天是为了包含最后一天
  		while (c_begin.before(c_end)) {
  			if (temp != count) {
  				timeStr = timeStr + "~" + tempStr.substring(tempStr.indexOf("-")+1,tempStr.length());
  				ret.add(timeStr);
  				temp = count;
  				timeStr = DateExtendUtil.formatDate2SmallDateString(c_begin.getTime());
  			}
  			if (c_begin.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
  				tempStr = DateExtendUtil.formatDate2SmallDateString(c_begin.getTime());
  				count++;
  			}
  			c_begin.add(Calendar.DAY_OF_YEAR, 1);
  		}
  		ret.add(timeStr + "~" + endStr.substring(endStr.indexOf("-")+1,endStr.length()));

  		return ret;
  	}
    
    /**
  	 * 获取日期相差值
  	 * @param start
  	 * @param end
  	 * @param calendarType
  	 * @return
  	 */
  	public static List<String> getBaseListByDay(Date start, Date end , int calendarType){
  		ArrayList<String> ret = new ArrayList<String>();
  		Calendar calendar = Calendar.getInstance();
  		calendar.setTime(start);   
  		Date tmpDate = calendar.getTime();
  		
  		long endTime = end.getTime();   
  		
  		while(tmpDate.before(end)||tmpDate.getTime() == endTime){   
  			ret.add(DateExtendUtil.formatDate2SmallDateString(calendar.getTime()));   
  			calendar.add(calendarType, 1);   
  			tmpDate = calendar.getTime();   
  		}          
  		
  		return ret;         
  	}
    
    /**
  	 * 获取小时相差值
  	 * @param start
  	 * @param end
  	 * @param calendarType
  	 * @return
  	 */
  	public static List<String> getBaseListByHour(List<String> listDay){
  		List<String> ret = new ArrayList<String>();
  		String[] timePoint = {"0","1","2","3","4","5","6","7","8","9","10"
  		                      ,"11","12","13","14","15","16","17","18","19","20","21","22","23"};
  		for(int m=0;m<listDay.size();m++){
  			for(int i=0;i<timePoint.length;i++){
  				String day = listDay.get(m);
  				if(day.startsWith("0")){
  					day = day.substring(1, day.length());
  				}
  				ret.add(day+" "+timePoint[i]);
  			}
  		}
  		return ret;         
  	}
  	
  	/**
     * 验证字符串是否是时间格式
     * @param date
     * @param format
     * @return
     */
    public static boolean checkDate(String date, String format) {
      SimpleDateFormat sdf = new SimpleDateFormat(format);
      Date d = null;
      try{
          d = sdf.parse(date);
      }catch(Exception e){
          //如果不能转换,肯定是错误格式
          return false;
      }
      return true;
    }
    
    /**
     * 一个月前的日期(14-9-19)
     * @return
     */
    public static String getOneMonthBefore(){
    	Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1); // 日期数加1
		return formatDate2SmallDateString(cal.getTime());
    }
    
    public static String longToString(long millis) {
	    Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        Formatter ft=new Formatter(Locale.CHINA);
        return ft.format("%1$tY-%1$tm-%1$td %1$tT", cal).toString();
}

	/**
	 * 判断某时间是不是在当天中
	 * @param date   时间
	 * @return true  在当天
     */
	public static boolean isToday(Date date){
		SimpleDateFormat fmt=new SimpleDateFormat("yyyy-MM-dd");
		if(fmt.format(date).toString().equals(fmt.format(new Date()).toString())){//格式化为相同格式
			return true;
		}else {
			return false;
		}

	}
}