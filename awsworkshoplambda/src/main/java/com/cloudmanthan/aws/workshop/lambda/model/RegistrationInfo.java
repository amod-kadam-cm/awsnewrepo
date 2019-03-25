package com.cloudmanthan.aws.workshop.lambda.model;

public class RegistrationInfo {

	String firstName;
	@Override
	public String toString() {
		return "RegistrationInfo [firstName=" + firstName + ", lastName=" + lastName + "]";
	}
	String lastName;
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	
}
