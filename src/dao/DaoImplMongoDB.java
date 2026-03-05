package dao;

import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Updates.set;
import static com.mongodb.client.model.Updates.combine;

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

	        // read all documents from collection
	        Iterable<Document> docs = collection.find();

	        // iterate documents and convert them into Product objects
	        for (Document doc : docs) {

	            // read product id
	            Integer id = doc.getInteger("id");

	            // read product name
	            String name = doc.getString("name");

	            // read wholesalerPrice object
	            Document wholesalerPriceDoc = (Document) doc.get("wholesalerPrice");

	            Double value = 0.0;

	            // read value from wholesalerPrice
	            if (wholesalerPriceDoc != null) {
	                Double temp = wholesalerPriceDoc.getDouble("value");
	                if (temp != null) {
	                    value = temp;
	                }
	            }

	            // read availability
	            Boolean available = doc.getBoolean("available");
	            if (available == null) {
	                available = true;
	            }

	            // read stock
	            Integer stock = doc.getInteger("stock");
	            if (stock == null) {
	                stock = 0;
	            }

	            // create Product object
	            Product product = new Product(name, new Amount(value), available, stock);

	            // set id if present
	            if (id != null) {
	                product.setId(id);
	            }

	            // add product to inventory
	            inventory.add(product);
	        }

	    } catch (Exception e) {
	        // in case error in MongoDB
	        e.printStackTrace();

	    } finally {
	        // disconnect data
	        disconnect();
	    }

	    return inventory;
	}

	@Override
	public boolean writeInventory(ArrayList<Product> inventory) {

	    // connect to data
	    connect();

	    try {

	        // get collection "historical_inventory"
	        MongoCollection<Document> collection = database.getCollection("historical_inventory");

	        // insert each product as a new historical document
	        for (Product product : inventory) {

	            // build wholesalerPrice object
	            Document wholesalerPriceDoc = new Document("value", product.getWholesalerPrice().getValue())
	                    .append("currency", "€");

	            // build historical document
	            Document document = new Document("id", product.getId())
	                    .append("name", product.getName())
	                    .append("wholesalerPrice", wholesalerPriceDoc)
	                    .append("available", product.isAvailable())
	                    .append("stock", product.getStock())
	                    .append("created_at", new java.util.Date());

	            // insert into historical inventory collection
	            collection.insertOne(document);
	        }

	        return true;

	    } catch (Exception e) {
	        // in case error in MongoDB
	        e.printStackTrace();
	        return false;

	    } finally {
	        // disconnect data
	        disconnect();
	    }
	}

	@Override
	public Employee getEmployee(int employeeId, String password) {
		Employee employee = null;

		// connect to database
		connect();

		try {
			// get employee collection
			MongoCollection<Document> collection = database.getCollection("users");

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

	    // connect to data
	    connect();

	    try {

	        // get collection "inventory"
	        MongoCollection<Document> collection = database.getCollection("inventory");

	        // build wholesalerPrice object
	        Document wholesalerPriceDoc = new Document("value", product.getWholesalerPrice().getValue())
	                .append("currency", "€");

	        // build document to insert
	        Document document = new Document("id", product.getId())
	                .append("name", product.getName())
	                .append("wholesalerPrice", wholesalerPriceDoc)
	                .append("available", product.isAvailable())
	                .append("stock", product.getStock());

	        // insert document into MongoDB
	        collection.insertOne(document);

	    } catch (Exception e) {
	        // in case error in MongoDB
	        e.printStackTrace();

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

	        // get collection "inventory"
	        MongoCollection<Document> collection = database.getCollection("inventory");

	        // filter to find the product by id
	        Document filter = new Document("id", product.getId());

	        // build wholesalerPrice object
	        Document wholesalerPriceDoc = new Document("value", product.getWholesalerPrice().getValue())
	                .append("currency", "€");

	        // fields to update
	        Document updatedValues = new Document("name", product.getName())
	                .append("wholesalerPrice", wholesalerPriceDoc)
	                .append("available", product.isAvailable())
	                .append("stock", product.getStock());

	        // MongoDB update operation
	        Document updateOperation = new Document("$set", updatedValues);

	        // execute update
	        collection.updateOne(filter, updateOperation);

	    } catch (Exception e) {
	        // in case error in MongoDB
	        e.printStackTrace();

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

	        // get collection "inventory"
	        MongoCollection<Document> collection = database.getCollection("inventory");

	        // filter to find the product by id
	        Document filter = new Document("id", productId);

	        // delete document from MongoDB
	        collection.deleteOne(filter);

	    } catch (Exception e) {
	        // in case error in MongoDB
	        e.printStackTrace();

	    } finally {
	        // disconnect data
	        disconnect();
	    }
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
