package dao;

import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;

import model.Employee;
import model.Product;

public class DaoImplMongoDB implements Dao {
	
	// MongoDB client and database references
	private MongoClient mongoClient;
	private MongoDatabase database;

	@Override
	public void connect() {
	    if (mongoClient == null) {
	        String uri = "mongodb://localhost:27017";
	        mongoClient = new MongoClient(new MongoClientURI(uri));
	        
	        // connect to database "shop"
	        database = mongoClient.getDatabase("shop");
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

		// connect to database
		connect();

		try {
			// get employee collection
			MongoCollection<Document> collection = database.getCollection("employee");

			// find employee by employeeId AND password
			Document doc = collection.find(
					and(
						eq("employeeId", employeeId),
						eq("password", password)
					)
			).first();

			// if found, parse document and create Employee object
			if (doc != null) {
				employee = new Employee(
						doc.getInteger("employeeId"),
						doc.getString("name"),
						doc.getString("password")
				);
			}

		} catch (Exception e) {
			// in case error in MongoDB
			e.printStackTrace();

		} finally {
			// disconnect from database
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
	    if (mongoClient != null) {
	        mongoClient.close();
	        mongoClient = null;
	        database = null;
	    }
	}

}
