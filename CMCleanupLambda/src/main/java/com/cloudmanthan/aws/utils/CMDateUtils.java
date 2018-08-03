package com.cloudmanthan.aws.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

public class CMDateUtils {

	static Logger LOGGER = Logger.getLogger(CMDateUtils.class.getName());

	public static void main(String[] args) {

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

		String workShopStartDate = System.getenv("WORKSHOP_START_DATE"); // DD-MM-YYYY format

		// workShopStartDate = "31-07-2018";

		Calendar cal1 = null;
		Date date;
		try {
			date = sdf.parse(workShopStartDate);
			System.out.println(date.toLocaleString());
			cal1 = Calendar.getInstance();
			cal1.setTime(date);

			System.out.println("Calender " + cal1.toString());

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String workShopEndDate = System.getenv("WORKSHOP_END_DATE"); // DD-MM-YYYY format

		// workShopEndDate = "02-08-2018";

		Date date2;
		Calendar cal2 = null;
		try {
			date2 = sdf.parse(workShopEndDate);
			System.out.println(date2.toLocaleString());
			cal2 = Calendar.getInstance();
			cal2.setTime(date2);

			System.out.println("Calender " + cal2.toString());

		} catch (ParseException e) {
			e.printStackTrace();
		}

		Calendar objectCal2 = Calendar.getInstance();
		objectCal2.clear();

		objectCal2.set(2018, Calendar.AUGUST, 1);

		isDateWithinWorkShopDate(cal1, cal2, objectCal2.getTime());

	}

	public static boolean isDateLaterthanWorkShopDate(String objectDate) {

		boolean isLater = false;
		// format for lambda
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSz");

		Date date;
		try {
			date = sdf.parse(objectDate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);

			Calendar workShopCal = Calendar.getInstance();
			workShopCal.clear();

			workShopCal.set(2018, Calendar.JULY, 25);

			if (cal.after(workShopCal) == true) {

				isLater = true;
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}

		return isLater;
	}

	public static boolean isDateLaterthanWorkShopDate(Date objectDate) {
		boolean isLater = false;

		Date date;
		Calendar cal = Calendar.getInstance();
		cal.setTime(objectDate);

		Calendar workShopCal = Calendar.getInstance();
		workShopCal.clear();

		workShopCal.set(2018, Calendar.JULY, 13);

		if (cal.after(workShopCal) == true) {

			isLater = true;
		}
		return isLater;
	}

	public static boolean isDateWithinWorkShopDate(Calendar workshopStartDate, Calendar workshopEndDate,
			Date objectDate) {

		boolean isWithinWorksop = false;

		workshopStartDate.add(Calendar.DAY_OF_MONTH, -1);

		workshopEndDate.add(Calendar.DAY_OF_MONTH, 1);

		// initialize the Calendar object with the date under consideration
		Calendar cal = Calendar.getInstance();
		cal.setTime(objectDate);

		if ((cal.after(workshopStartDate) == true) && (cal.before(workshopEndDate) == true)) {

			isWithinWorksop = true;

			LOGGER.info("Found within workshop date");
		}
		return isWithinWorksop;

	}

	public static boolean isDateWithinWorkShopDate(String pWorkshopStartDate, String pWorkshopEndDate, Date pDate) {

		boolean isWithinWorksop = false;

		Calendar workshopStartDate = getDate(pWorkshopStartDate);

		Calendar workshopEndDate = getDate(pWorkshopEndDate);

		isWithinWorksop = isDateWithinWorkShopDate(workshopStartDate, workshopEndDate, pDate);

		return isWithinWorksop;
	}

	static Calendar getDate(String dateStringFormat) {
		// currently dateformat is hardcoded
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

		Calendar cal = null;
		Date date;
		try {
			date = sdf.parse(dateStringFormat);
			cal = Calendar.getInstance();
			cal.setTime(date);

			LOGGER.info("Date is " + date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cal;

	}
}
