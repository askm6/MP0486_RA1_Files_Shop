package dao;

import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import model.Amount;
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
	    ArrayList<Product> inventory = new ArrayList<>();

	    // connect to data
	    connect();

	    try {
	        // read all Product objects from ObjectDB
	        TypedQuery<Product> query = em.createQuery(
	                "SELECT p FROM inventory p",
	                Product.class
	        );

	        inventory.addAll(query.getResultList());
	        
	        for (Product product : inventory) {
	            product.setWholesalerPrice(new Amount(product.getPrice()));
	            product.setPublicPrice(new Amount(product.getPrice() * 2));
	        }

	    } catch (Exception e) {
	        // in case error in ObjectDB
	        e.printStackTrace();

	    } finally {
	        // disconnect data
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
		Employee employee = null;

		connect();

		try {
			employee = em
					.createQuery("SELECT u FROM users u WHERE u.employeeId = :id AND u.password = :pw",
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
		 // connect to data
	    connect();

	    try {
	    	// get current product id
	        int newId = product.getId();

	        // find next free id if current one already exists
	        while (em.find(Product.class, newId) != null) {
	            newId++;
	        }

	        product.setId(newId);
	        
	        // begin transaction
	        em.getTransaction().begin();

	        // persist product object in ObjectDB
	        em.persist(product);

	        // commit transaction
	        em.getTransaction().commit();

	    } catch (Exception e) {
	        // in case error in ObjectDB
	        e.printStackTrace();

	        if (em.getTransaction().isActive()) {
	            em.getTransaction().rollback();
	        }

	    } finally {
	        // disconnect data
	        disconnect();
	    }
	}

	@Override
	public void updateProduct(Product product) {
		// connect to data
	    connect();

	    try {
	        // begin transaction
	        em.getTransaction().begin();

	        // update product object in ObjectDB
	        em.merge(product);

	        // commit transaction
	        em.getTransaction().commit();

	    } catch (Exception e) {
	        // in case error in ObjectDB
	        e.printStackTrace();

	        if (em.getTransaction().isActive()) {
	            em.getTransaction().rollback();
	        }

	    } finally {
	        // disconnect data
	        disconnect();
	    }
	}

	@Override
	public void deleteProduct(int productId) {
		// connect to data
	    connect();

	    try {
	        // begin transaction
	        em.getTransaction().begin();

	        // find product by id
	        Product product = em.find(Product.class, productId);

	        // delete product if exists
	        if (product != null) {
	            em.remove(product);
	        }

	        // commit transaction
	        em.getTransaction().commit();

	    } catch (Exception e) {
	        // in case error in ObjectDB
	        e.printStackTrace();

	        if (em.getTransaction().isActive()) {
	            em.getTransaction().rollback();
	        }

	    } finally {
	        // disconnect data
	        disconnect();
	    }
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
