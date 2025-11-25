package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import model.Amount;
import model.Employee;
import model.Product;

public class DaoImplJDBC implements Dao {
	Connection connection;

	@Override
	public void connect() {
		// Define connection parameters
		String url = "jdbc:mysql://localhost:3306/shop";
		String user = "root";
		String pass = "";
		try {
			this.connection = DriverManager.getConnection(url, user, pass);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public ArrayList<Product> getInventory() {
		ArrayList<Product> inventory = new ArrayList<>();
		String query = "select * from inventory";

		try (PreparedStatement ps = connection.prepareStatement(query)) {
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					inventory.add(new Product(rs.getString(2), new Amount(rs.getDouble(3)), true, rs.getInt(5)));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return inventory;
	}

	@Override
	public boolean writeInventory(ArrayList<Product> inventory) {
		String query = "INSERT INTO historical_inventory (name, amount, active, stock) VALUES (?, ?, ?, ?)";

		try (PreparedStatement ps = connection.prepareStatement(query)) {

			for (Product p : inventory) {
				ps.setString(1, p.getName());
				ps.setDouble(2, p.getPublicPrice().getValue());
				ps.setBoolean(3, p.isAvailable());
				ps.setInt(4, p.getStock());

				ps.addBatch();
			}

			ps.executeBatch();

			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Employee getEmployee(int employeeId, String password) {
		Employee employee = null;
		String query = "select * from employee where employeeId= ? and password = ? ";

		try (PreparedStatement ps = connection.prepareStatement(query)) {
			ps.setInt(1, employeeId);
			ps.setString(2, password);
			// System.out.println(ps.toString());
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					employee = new Employee(rs.getInt(1), rs.getString(2), rs.getString(3));
				}
			}
		} catch (SQLException e) {
			// in case error in SQL
			e.printStackTrace();
		}
		return employee;
	}

	@Override
	public void addProduct(Product product) {
		String query = "INSERT INTO inventory (name, amount, active, stock) VALUES (?, ?, ?, ?)";

		try (PreparedStatement ps = connection.prepareStatement(query)) {
			ps.setString(1, product.getName());
			ps.setDouble(2, product.getPublicPrice().getValue());
			ps.setBoolean(3, product.isAvailable());
			ps.setInt(4, product.getStock());

			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void updateProduct(Product product) {
		String query = "UPDATE inventory SET name = ?, amount = ?, active = ?, stock = ? WHERE id = ?";

		try (PreparedStatement ps = connection.prepareStatement(query)) {

			ps.setString(1, product.getName());
			ps.setDouble(2, product.getPublicPrice().getValue());
			ps.setBoolean(3, product.isAvailable());
			ps.setInt(4, product.getStock());
			ps.setInt(5, product.getId());

			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteProduct(int productId) {
		String query = "DELETE FROM inventory WHERE id = ?";

		try (PreparedStatement ps = connection.prepareStatement(query)) {

			ps.setInt(1, productId);
			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
