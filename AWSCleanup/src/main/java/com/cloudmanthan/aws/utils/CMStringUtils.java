package com.cloudmanthan.aws.utils;

public class CMStringUtils {

	
	String getExpectedRegionName(String regionName) {
		return "us-east-1";
	}
	
	public static void main(String args[]) {
		//
		String errorMessage  = "The authorization header is malformed; the region 'us-east-1' is wrong; expecting 'ap-south-1'";
				
		
		String anotherRegionString = errorMessage.substring(83, errorMessage.length()-1);
		
		
		System.out.println("AnotherRegionString:" + anotherRegionString );
	}
}
