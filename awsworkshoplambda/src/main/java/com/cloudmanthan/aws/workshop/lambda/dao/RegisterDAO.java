package com.cloudmanthan.aws.workshop.lambda.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cloudmanthan.aws.workshop.lambda.model.RegistrationInfo;

public class RegisterDAO {

	Logger logger = Logger.getLogger(this.getClass().getName());

	public boolean register(RegistrationInfo regInfo) {

		boolean success = false;

		logger.log(Level.INFO, regInfo.toString());

		try {
			Class.forName("com.mysql.jdbc.Driver");

			String db_host = System.getenv("DB_HOST");
			String db_port = System.getenv("DB_PORT");
			String db_name = System.getenv("DB_NAME");
			String db_user = System.getenv("DB_USER");
			String db_password = System.getenv("DB_PASSWORD");

			String conString = "jdbc:mysql://" + db_host + ":" + db_port + "/" + db_name;

			logger.log(Level.INFO, conString);
			Connection con = DriverManager.getConnection(conString, db_user, db_password);

			PreparedStatement stmt = con.prepareStatement("insert into registration values(?,?)");
			stmt.setString(1, regInfo.getFirstName());// 1 specifies the first parameter in the query
			stmt.setString(2, regInfo.getLastName());

			int i = stmt.executeUpdate();
			System.out.println(i + " records inserted");

			con.close();

		} catch (Exception e) {
			System.out.println(e);
			logger.log(Level.SEVERE, e.getMessage());

		}

		return success;

	}
}
