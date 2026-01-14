package model;

import main.Logable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import dao.*;

@Entity
@Table(name="employee")
public class Employee extends Person implements Logable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int employeeId;
	@Column
	private String password;
	// connection using JDBC SQL
	@Transient
	private Dao dao = new DaoImplHibernate();
	@Transient
	public static final int USER = 123;
	@Transient
	public static final String PASSWORD = "test";
	
	public Employee(String name) {
		super(name);
	}
	
	public Employee(int employeeId, String name, String password) {
		super(name);
		this.employeeId = employeeId;
		this.password = password;
	}
	
	public Employee() {
		super();
	}
	
	/**
	 * @return the employeeId
	 */
	public int getEmployeeId() {
		return employeeId;
	}

	/**
	 * @param employeeId the employeeId to set
	 */
	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @param user from application, password from application
	 * @return true if credentials are correct or false if not
	 */
	@Override
	public boolean login(int user, String password) {
//		if (USER == user && PASSWORD.equals(password)) {
//			return true;
//		} 
		boolean success = false;
		
		// connect to data
		dao.connect();
		
		// get employee data
		if(dao.getEmployee(user, password) != null) {
			success =  true;
		}
		
		// disconnect data
		dao.disconnect();
		return success;
	}

}
