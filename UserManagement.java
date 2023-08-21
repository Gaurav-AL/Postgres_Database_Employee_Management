import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import org.postgresql.util.PSQLException;

/*
 * Employee Structure
 */
class Employee{
	int emp_id,emp_age;
	String emp_first_name,emp_last_name,emp_middle_name,emp_gender,emp_role,emp_dept,emp_unit,emp_contact_number,emp_address,emp_dob;	
}

/*
 * Employee User Management
 */
public class UserManagement {
	static Scanner read = new Scanner(System.in);
	static JDBCPostgreSQLConnection connection = null;
	static Connection cursor = null;
	UserManagement(){
		connection = new JDBCPostgreSQLConnection();
		cursor = connection.connect();
	}
	/*
	 * Helper Function to check whether Employee Exists or not
	 */
	private static int isEmployeePresent(int emp_id) throws SQLException {
		
		int count = 0;
		String select_query = "Select * from Employee where emp_id = ?;";
		
		PreparedStatement prepared_statement = null;
		
		try {
			prepared_statement = cursor.prepareStatement(select_query);
			prepared_statement.setInt(1, emp_id);
			ResultSet total_employee = prepared_statement.executeQuery();
			System.out.println("Employee ID | Employee First Name |	Employee Middle Name | Employee Last Name | Employee Gender | Employee Address | Employee DOB | Employee Age | Employee Role | Employee Department | Employee Unit | Employee Contact Number ");
			while(total_employee.next()) {
				count += 1;
				System.out.println(total_employee.getInt("emp_id")+" | "+total_employee.getString("emp_first_name")+" | "+total_employee.getString("emp_middle_name")+" | "+total_employee.getString("emp_last_name")+" | "+total_employee.getString("emp_gender")+" | "+total_employee.getString("emp_address")+" | "+total_employee.getString("emp_date_of_birth")+" | "+total_employee.getInt("emp_age")+" | "+total_employee.getString("emp_role")+" | "+total_employee.getString("emp_dept")+" | "+total_employee.getString("emp_unit")+" | "+total_employee.getString("emp_contact_number"));
			}
		}catch(SQLException sql) {
			sql.printStackTrace();
			System.out.println("Failed to fetch the employee records ");
		}
		finally {
			prepared_statement.close();
			System.out.println("Closing Prepared Statement Pipe !");
		}
		return count;
	}
	/*
	 * Function to check or create employee table.
	 */
	private static void createOrcheckEmployeeTable() throws SQLException {
		String create_query = "CREATE TABLE IF NOT EXISTS EMPLOYEE("
				+ "EMP_ID INTEGER PRIMARY KEY,"
				+ "EMP_FIRST_NAME VARCHAR(255) NOT NULL,"
				+ "EMP_MIDDLE_NAME VARCHAR(255),"
				+ "EMP_LAST_NAME VARCHAR(255) NOT NULL, "
				+ "EMP_GENDER VARCHAR(20) NOT NULL,"
				+ "EMP_ADDRESS VARCHAR(255) NOT NULL,"
				+ "EMP_DATE_OF_BIRTH VARCHAR(255) NOT NULL,"
				+ "EMP_AGE INTEGER NOT NULL, "
				+ "EMP_ROLE VARCHAR(255) NOT NULL,"
				+ "EMP_DEPT VARCHAR(255) NOT NULL,"
				+ "EMP_UNIT VARCHAR(255) NOT NULL,"
				+ "EMP_CONTACT_NUMBER VARCHAR(10) NOT NULL);";
		
		String grant_all_privileges = "Grant all on employee to gaurav;";
		Statement statement = null;
		try {
			statement  = cursor.createStatement();
			if(statement.execute(create_query) == false) System.out.println("Employee Table Already Exists !");
			else {
				statement.execute(grant_all_privileges);
				System.out.println("Table Employee created Successfully");
			}
		}catch(SQLException sql) {
			System.out.println("Table Employee creation Failed");
			sql.getMessage();
		}
		finally {
			statement.close();
			System.out.println("Closed Statement Pipe ");
		}
	}
	/*
	 * Function to add Employee 
	 */
	private static void addEmployee() throws SQLException {
		
		Employee employee = new Employee();
		int emp_dob_day,emp_dob_month,emp_dob_year;
		
		String insert_query = "Insert into employee values(?,?,?,?,?,?,?,?,?,?,?,?);";
		
		// Taking user input
		
		System.out.print("Enter Employee ID :");
		employee.emp_id = read.nextInt();
		read.nextLine();
		System.out.print("Enter Employee First Name :");
		employee.emp_first_name = read.nextLine();
		System.out.print("Enter Employee Middle Name (If Don't Have middle name Specify It.) :");
		employee.emp_middle_name = read.nextLine();
		System.out.print("Enter Employee Last Name :");
		employee.emp_last_name = read.nextLine();
		System.out.print("Enter Employee Gender :");
		employee.emp_gender = read.nextLine();
		System.out.print("Enter Employee Address :");
		employee.emp_address = read.nextLine();
		System.out.print("Enter Employee DOB Day :");
		emp_dob_day = read.nextInt();
		System.out.print("Enter Employee DOB Month :");
		emp_dob_month = read.nextInt();
		System.out.print("Enter Employee DOB Year :");
		emp_dob_year = read.nextInt();
		read.nextLine();
		System.out.print("Enter Employee Role :");
		employee.emp_role = read.nextLine();
		System.out.print("Enter Employee Department :");
		employee.emp_dept = read.nextLine();
		System.out.print("Enter Employee Unit :");
		employee.emp_unit = read.nextLine();
		System.out.print("Enter Employee Contact Number :");
		employee.emp_contact_number = read.nextLine();
		
		employee.emp_dob = emp_dob_day+"-"+emp_dob_month+"-"+emp_dob_year;
		
		int current_month = new java.util.Date().getMonth() + 1;
		int current_year = new java.util.Date().getYear()+1900;
		employee.emp_age= current_year - emp_dob_year;
		
		if(emp_dob_month > current_month) employee.emp_age -= 1;
		
		PreparedStatement prepared_statement = null;
		try {
			prepared_statement = cursor.prepareStatement(insert_query);
			
			//Inserting values
			
			prepared_statement.setInt(1, employee.emp_id);
			prepared_statement.setString(2, employee.emp_first_name);
			prepared_statement.setString(3, employee.emp_middle_name);
			prepared_statement.setString(4, employee.emp_last_name);
			prepared_statement.setString(5, employee.emp_gender);
			prepared_statement.setString(6, employee.emp_address);
			prepared_statement.setString(7,employee.emp_dob);
			prepared_statement.setInt(8, employee.emp_age);
			prepared_statement.setString(9, employee.emp_role);
			prepared_statement.setString(10, employee.emp_dept);
			prepared_statement.setString(11, employee.emp_unit);
			prepared_statement.setString(12, employee.emp_contact_number);
			
			System.out.println("Done Inserting values.");
			
			int values_inserted = prepared_statement.executeUpdate();
			System.out.println("Total "+values_inserted +" Row Affected .");
			
		}catch(SQLException sql){
			sql.printStackTrace();
			System.out.println("Problem Occured! Failed to insert the Employee Data.");
			
		}finally {
			System.out.println("Closing Prepared Statement Pipe!");
			prepared_statement.close();
		}
		
	}
	/*
	 * Function to delete Employee
	 */
	private static void deleteEmployee() throws SQLException{
		
		int emp_id;
		String select_query = "Delete from Employee where emp_id = ?;";
		System.out.print("Enter the Employee Id to be deleted :");
		emp_id = read.nextInt();
		
		PreparedStatement prepared_statement  = null;
		
		try {
			
			prepared_statement = cursor.prepareStatement(select_query);
			prepared_statement.setInt(1, emp_id);
			int row_deleted = prepared_statement.executeUpdate();
			
			if(row_deleted == 0) System.out.println("Employee ID Doesn't Exist!");
			else System.out.println("Total "+ row_deleted + " row Deleted.");
	
			
		}catch(SQLException sql) {
			sql.printStackTrace();
			System.out.println("Failed to fetch the employee records ");
		}
		finally {
			prepared_statement.close();
			System.out.println("Closing Prepared Statement Pipe !");
		}
		
	}
	/*
	 * Function to Update Employee
	 */
	private static void updateEmployee() throws SQLException{
		Employee employee = new Employee();
		int emp_dob_day,emp_dob_month,emp_dob_year;
	
		String update_query = "Update Employee set "
				+ " emp_first_name = ?,"
				+ " emp_middle_name = ?,"
				+ " emp_last_name = ?,"
				+ " emp_gender = ?,"
				+ " emp_address = ?,"
				+ " emp_date_of_birth = ?,"
				+ " emp_age = ?,"
				+ " emp_role = ?,"
				+ " emp_dept = ?,"
				+ " emp_unit = ?,"
				+ " emp_contact_number = ?"
				+ "where emp_id = ?;";
		
		System.out.print("Enter Employee ID to be updated :");
		employee.emp_id = read.nextInt();
		if(isEmployeePresent(employee.emp_id) == 0) {
			System.out.println("Employee Doesn't Exists !");
			return;
		}
		read.nextLine();
		System.out.print("Enter Employee First Name :");
		employee.emp_first_name = read.nextLine();
		System.out.print("Enter Employee Middle Name (If Don't Have middle name Specify It.) :");
		employee.emp_middle_name = read.nextLine();
		System.out.print("Enter Employee Last Name :");
		employee.emp_last_name = read.nextLine();
		System.out.print("Enter Employee Gender :");
		employee.emp_gender = read.nextLine();
		System.out.print("Enter Employee Address :");
		employee.emp_address = read.nextLine();
		System.out.print("Enter Employee DOB Day :");
		emp_dob_day = read.nextInt();
		System.out.print("Enter Employee DOB Month :");
		emp_dob_month = read.nextInt();
		System.out.print("Enter Employee DOB Year :");
		emp_dob_year = read.nextInt();
		read.nextLine();
		System.out.print("Enter Employee Role :");
		employee.emp_role = read.nextLine();
		System.out.print("Enter Employee Department :");
		employee.emp_dept = read.nextLine();
		System.out.print("Enter Employee Unit :");
		employee.emp_unit = read.nextLine();
		System.out.print("Enter Employee Contact Number :");
		employee.emp_contact_number = read.nextLine();
		
		employee.emp_dob = emp_dob_day+"-"+emp_dob_month+"-"+emp_dob_year;
		
		int current_month = new java.util.Date().getMonth() + 1;
		int current_year = new java.util.Date().getYear()+1900;
		employee.emp_age= current_year - emp_dob_year;
		
		if(emp_dob_month > current_month) employee.emp_age -= 1;
		
		PreparedStatement prepared_statement = null;
		try {
			prepared_statement = cursor.prepareStatement(update_query);
			
			//Inserting values
			
			prepared_statement.setInt(12, employee.emp_id);
			prepared_statement.setString(1, employee.emp_first_name);
			prepared_statement.setString(2, employee.emp_middle_name);
			prepared_statement.setString(3, employee.emp_last_name);
			prepared_statement.setString(4, employee.emp_gender);
			prepared_statement.setString(5, employee.emp_address);
			prepared_statement.setString(6,employee.emp_dob);
			prepared_statement.setInt(7, employee.emp_age);
			prepared_statement.setString(8, employee.emp_role);
			prepared_statement.setString(9, employee.emp_dept);
			prepared_statement.setString(10, employee.emp_unit);
			prepared_statement.setString(11, employee.emp_contact_number);
			
			System.out.println("Done Inserting values.");
			
			int values_inserted = prepared_statement.executeUpdate();
			if(values_inserted == 0) System.out.println(" Employee Id Doesn't Exists !");
			else System.out.println("Total "+values_inserted +" Row Updated .");
			
		}catch(SQLException sql){
			sql.printStackTrace();
			System.out.println("Problem Occured! Failed to Update the Employee Data.");
			
		}finally {
			System.out.println("Closing Prepared Statement Pipe!");
			prepared_statement.close();
		}	
	}
	/*
	 * Function to Display All Employees
	 */
	private static void displayAllEmployee()  throws SQLException{
		int count = 0;
		String select_query = "Select * from Employee ;";
		
		PreparedStatement prepared_statement  = null;
		
		try {
			prepared_statement = cursor.prepareStatement(select_query);
			ResultSet total_employee = prepared_statement.executeQuery();
			System.out.println("Employee ID | Employee First Name |	Employee Middle Name | Employee Last Name | Employee Gender | Employee Address | Employee DOB | Employee Age | Employee Role | Employee Department | Employee Unit | Employee Contact Number ");
			while(total_employee.next()) {
				count += 1;
				System.out.println(total_employee.getInt("emp_id")+" | "+total_employee.getString("emp_first_name")+" | "+total_employee.getString("emp_middle_name")+" | "+total_employee.getString("emp_last_name")+" | "+total_employee.getString("emp_gender")+" | "+total_employee.getString("emp_address")+" | "+total_employee.getString("emp_date_of_birth")+" | "+total_employee.getInt("emp_age")+" | "+total_employee.getString("emp_role")+" | "+total_employee.getString("emp_dept")+" | "+total_employee.getString("emp_unit")+" | "+total_employee.getString("emp_contact_number"));
			}
			System.out.println("Total "+count +" row fetched.");
		}catch(SQLException sql) {
			sql.printStackTrace();
			System.out.println("Failed to fetch the employee records ");
		}
		finally {
			prepared_statement.close();
			System.out.println("Closing Prepared Statement Pipe !");
		}
		
	}
	/*
	 * Function to Display Employee Info with Employee ID
	 */
	private static void displayAnEmployee() throws SQLException {
		int count = 0,emp_id;
		String select_query = "Select * from Employee where emp_id = ?;";
		System.out.print("Enter the Employee Id to be displayed :");
		emp_id = read.nextInt();
		
		PreparedStatement prepared_statement  = null;
		
		try {
			prepared_statement = cursor.prepareStatement(select_query);
			prepared_statement.setInt(1, emp_id);
			ResultSet total_employee = prepared_statement.executeQuery();
			System.out.println("Employee ID | Employee First Name |	Employee Middle Name | Employee Last Name | Employee Gender | Employee Address | Employee DOB | Employee Age | Employee Role | Employee Department | Employee Unit | Employee Contact Number ");
			while(total_employee.next()) {
				count += 1;
				System.out.println(total_employee.getInt("emp_id")+" | "+total_employee.getString("emp_first_name")+" | "+total_employee.getString("emp_middle_name")+" | "+total_employee.getString("emp_last_name")+" | "+total_employee.getString("emp_gender")+" | "+total_employee.getString("emp_address")+" | "+total_employee.getString("emp_date_of_birth")+" | "+total_employee.getInt("emp_age")+" | "+total_employee.getString("emp_role")+" | "+total_employee.getString("emp_dept")+" | "+total_employee.getString("emp_unit")+" | "+total_employee.getString("emp_contact_number"));
			}
			if(count == 0) System.out.println("Employee Doesn't Exist or Employee ID is wrong ");
			System.out.println("Total "+count +" row fetched.");
		}catch(SQLException sql) {
			sql.printStackTrace();
			System.out.println("Failed to fetch the employee records ");
		}
		finally {
			prepared_statement.close();
			System.out.println("Closing Prepared Statement Pipe !");
		}	
	}
	/*
	 * Function to Drop Employee Table
	 */
	private static void dropEmployeeTable() throws SQLException{
		String drop_query = "drop table employee;";
		Statement statement = null;
		try {
			statement = cursor.createStatement();
			
			try{
				statement.executeUpdate(drop_query);
				System.out.println("Table Dropped Successfully .");
			}catch(PSQLException psql) {
				System.out.println("Table Doesn't Exists !");
			}
			
		}catch(SQLException sql) {
			sql.printStackTrace();
			System.out.println("Failed to Drop Employee Table ");
		}finally {
			System.out.println("Closing Statement Pipe !");
			statement.close();
		}
	}
	public static void main(String... gaurav) throws Exception{
		new UserManagement();
		int choice;
		do {
			System.out.println("================================================================================================================");
			System.out.println("Press \n1. Create or Check Employee Table\n2. Add a Employee\n3. Delete a Employee\n4. Update a Employee\n5. Display All Employees\n6. Display a Employee with Employee ID\n7. Drop Table\n8+. Exit.");
			System.out.print("Enter Your Choice :");
			choice = read.nextInt();
			switch(choice) {
			case 1:
				createOrcheckEmployeeTable();
			break;
			case 2:
				addEmployee();
			break;
			case 3:
				deleteEmployee();
			break;
			case 4:
				updateEmployee();
			break;
			case 5:
				displayAllEmployee();
			break;
			case 6:
				displayAnEmployee();
			break;
			case 7:
				dropEmployeeTable();
			break;
			default:
				System.out.println("Wrong Choice !! Exiting ...");
			break;
			}
			System.out.println("================================================================================================================");
		} while(choice < 8);	
	}
}
