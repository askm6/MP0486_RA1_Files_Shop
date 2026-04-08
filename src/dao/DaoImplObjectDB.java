package dao;

import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import model.Employee;
import model.Product;

public class DaoImplObjectDB implements Dao {

	private EntityManagerFactory emf;
	private EntityManager em;

	@Override
	public void connect() {
		if (emf == null || !emf.isOpen()) {
			emf = Persistence.createEntityManagerFactory("$objectdb/db/objects/shop.odb");
		}
		if (em == null || !em.isOpen()) {
			em = emf.createEntityManager();
		}
	}

	@Override
	public ArrayList<Product> getInventory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean writeInventory(ArrayList<Product> inventory) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Employee getEmployee(int employeeId, String password) {
		Employee employee = null;

		connect();

		try {
			employee = em
					.createQuery("SELECT e FROM Employee e WHERE e.employeeId = :id AND e.password = :pw",
							Employee.class)
					.setParameter("id", employeeId).setParameter("pw", password).getSingleResult();

		} catch (Exception e) {
			employee = null;
		} finally {
			disconnect();
		}

		return employee;
	}

	@Override
	public void addProduct(Product product) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateProduct(Product product) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteProduct(int productId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnect() {
		if (em != null && em.isOpen()) {
			em.close();
		}
		if (emf != null && emf.isOpen()) {
			emf.close();
		}
	}

}
