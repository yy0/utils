package com.jriver.commons.tools

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;

/**
 * Description：时区工具类
 * 
 * @author zhouxin
 * @version IMSP 1.0
 */
public class TimeZoneUtil {
	public static final Logger loggger = Logger.getLogger(TimeZoneUtil.class);

	public static final TimeZone TIMEZONE_GMT_0 = TimeZone.getTimeZone("GMT+0");

	public static final TimeZone TIMEZONE_GMT_8 = TimeZone.getTimeZone("GMT+8");

	/**
	 * Description：时间转换时区
	 * 
	 * @param sourceDate
	 *            需要转换的时间
	 * @param sourceTimeZone
	 *            原始时区，如GMT-0时区为TimeZone.getTimeZone("GMT-0:00")
	 * @param targetTimeZone
	 *            目标时区，如GMT-8时区为TimeZone.getTimeZone("GMT-8:00")
	 * @return
	 */
	public static Date converDateGMT(Date sourceDate, TimeZone sourceTimeZone, TimeZone targetTimeZone) {
		if (sourceDate == null || sourceTimeZone == null || targetTimeZone == null)
			return sourceDate;

		Long time = sourceDate.getTime();
		Long targetTime = time - (sourceTimeZone.getRawOffset() - targetTimeZone.getRawOffset());
		Date date = new Date(targetTime);

		return date;
	}

	/**
	 * Description：GMT0时间转换为北京时间GMT8
	 * 
	 * @param sourceDate
	 *            处理时间对象
	 * @return Date
	 */
	public static Date converDateGMT8(Date sourceDate) {
		return converDateGMT(sourceDate, TIMEZONE_GMT_0, TIMEZONE_GMT_8);
	}

	/**
	 * Description：GMT0时间转换为北京时间GMT8
	 * 
	 * @param sourceDateStr
	 *            处理时间字符串
	 * @param pattern
	 *            时间字符串格式
	 * @return Date
	 */
	public static Date converDateGMT8(String sourceDateStr, String pattern) {
		SimpleDateFormat sdf = null;
		Date sourceDate = null;

		try {
			sdf = new SimpleDateFormat(pattern);
		} catch (Exception e) {
			loggger.info(e.getMessage());
			return sourceDate;
		}

		try {
			sourceDate = sdf.parse(sourceDateStr);
		} catch (ParseException e) {
			loggger.info(e.getMessage());
			return sourceDate;
		}

		return converDateGMT8(sourceDate);
	}
}
