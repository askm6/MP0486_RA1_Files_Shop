package dao;

import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;

import model.Amount;
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
	    ArrayList<Product> inventory = new ArrayList<>();

	    // connect to data
	    connect();

	    try {
	        // get collection "inventory"
	        MongoCollection<Document> collection = database.getCollection("inventory");

	        // read all documents from inventory
	        Iterable<Document> docs = collection.find();

	        // build Product objects and add to ArrayList
	        for (Document doc : docs) {

	            String name = doc.getString("name");

	            // price must be stored as number (double) in Mongo
	            Double price = doc.getDouble("price");
	            if (price == null) {
	                // in case it was stored as int/long, try to convert
	                Number n = doc.get("price", Number.class);
	                if (n != null) {
	                    price = n.doubleValue();
	                } else {
	                    price = 0.0;
	                }
	            }

	            Integer stock = doc.getInteger("stock");
	            if (stock == null) {
	                Number n = doc.get("stock", Number.class);
	                if (n != null) {
	                    stock = n.intValue();
	                } else {
	                    stock = 0;
	                }
	            }

	            Boolean available = doc.getBoolean("available");
	            if (available == null) {
	                available = true;
	            }

	            // create Product style
	            inventory.add(new Product(name, new Amount(price), available, stock));
	        }

	    } catch (Exception e) {
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
