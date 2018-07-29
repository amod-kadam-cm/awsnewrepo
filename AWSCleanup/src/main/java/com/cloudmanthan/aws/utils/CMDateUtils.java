package com.cloudmanthan.aws.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import com.amazonaws.services.ec2.model.DeregisterImageRequest;
import com.cloudmanthan.aws.AWSCleanup.LambdaCleanup;

public class CMDateUtils {

	
	static Logger LOGGER = Logger.getLogger(CMDateUtils.class.getName());

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static boolean isDateLaterthanWorkShopDate(String objectDate){
		
		boolean isLater = false ;
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

				isLater = true ;
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
		return isLater ;
	}

}
