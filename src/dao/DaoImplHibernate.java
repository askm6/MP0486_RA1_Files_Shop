package dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import model.Amount;
import model.Employee;
import model.Product;
import model.Sale;
import main.Shop;

public class DaoImplHibernate implements Dao {
	private SessionFactory sessionFactory;
	private Session session;
	private Transaction tx;


	@Override
	public void connect() {
	    if (sessionFactory == null) {
	        sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
	    }
	    if (session == null || !session.isOpen()) {
	        session = sessionFactory.openSession();
	    }
	}


	@Override
	public ArrayList<Product> getInventory() {
        ArrayList<Product> inventory = new ArrayList<>();

		try {
			if (session == null || !session.isOpen()) {
	            connect();
	        }
			tx = session.beginTransaction();

			// We create a manual query. Remember that "*" does not exist
			Query<Product> q = session.createQuery("select p from Product p", Product.class);

			// We get a List of Products
			List<Product> productsList = q.list();

			// we add this products to our ArrayList
			inventory.addAll(productsList);

			tx.commit();

		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback(); // Roll back if any exception occurs.
			e.printStackTrace();
		} finally {
	        disconnect();
	    }

		return inventory;
	}

	@Override
	public boolean writeInventory(ArrayList<Product> inventory) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Employee getEmployee(int employeeId, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addProduct(Product product) {
		try {
			if (session == null || !session.isOpen()) {
	            connect();
	        }
			
			tx = session.beginTransaction();
			
			//we save the product object on database
			session.save(product);
			
			tx.commit();

		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback(); // Roll back if any exception occurs.
			e.printStackTrace();
		} finally {
	        disconnect();
	    }
	}

	@Override
	public void updateProduct(Product product) {
		try {
			if (session == null || !session.isOpen()) {
	            connect();
	        }
			
			tx = session.beginTransaction();
			
			session.update(product);
			
			tx.commit();

		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback(); // Roll back if any exception occurs.
			e.printStackTrace();
		} finally {
	        disconnect();
	    }
	}

	@Override
	public void deleteProduct(int productId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnect() {
		if (session != null && session.isOpen()) {
	        session.close();
	    }
	}


}
