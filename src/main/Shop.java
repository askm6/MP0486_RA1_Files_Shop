package main;

import model.Product;
import model.Sale;
import model.Amount;
import model.Client;
import model.Employee;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import dao.Dao;
import dao.DaoImplFile;
import dao.DaoImplJDBC;

public class Shop {
	private Amount cash = new Amount(100.00);
//	private Product[] inventory;
	private ArrayList<Product> inventory;
	private int numberProducts;
//	private Sale[] sales;
	private ArrayList<Sale> sales;
	private int numberSales;

	final static double TAX_RATE = 1.04;
	
	private Dao dao = new DaoImplJDBC();

	public Shop() {
		inventory = new ArrayList<Product>();
		sales = new ArrayList<Sale>();
	}
	
	

	public Amount getCash() {
		return cash;
	}



	public void setCash(Amount cash) {
		this.cash = cash;
	}



	public ArrayList<Product> getInventory() {
		return inventory;
	}



	public void setInventory(ArrayList<Product> inventory) {
		this.inventory = inventory;
	}



	public int getNumberProducts() {
		return numberProducts;
	}



	public void setNumberProducts(int numberProducts) {
		this.numberProducts = numberProducts;
	}



	public ArrayList<Sale> getSales() {
		return sales;
	}



	public void setSales(ArrayList<Sale> sales) {
		this.sales = sales;
	}



	public int getNumberSales() {
		return numberSales;
	}



	public void setNumberSales(int numberSales) {
		this.numberSales = numberSales;
	}



	public static void main(String[] args) {
		Shop shop = new Shop();

		// load inventory from external data
		shop.loadInventory();
		
		// init session as employee
		shop.initSession();

		Scanner scanner = new Scanner(System.in);
		int opcion = 0;
		boolean exit = false;

		do {
			System.out.println("\n");
			System.out.println("===========================");
			System.out.println("Menu principal miTienda.com");
			System.out.println("===========================");
			System.out.println("0) Exportar inventario");
			System.out.println("1) Contar caja");
			System.out.println("2) Añadir producto");
			System.out.println("3) Añadir stock");
			System.out.println("4) Marcar producto proxima caducidad");
			System.out.println("5) Ver inventario");
			System.out.println("6) Venta");
			System.out.println("7) Ver ventas");
			System.out.println("8) Ver venta total");
			System.out.println("9) Eliminar producto");
			System.out.println("10) Salir programa");
			System.out.print("Seleccione una opción: ");
			opcion = scanner.nextInt();

			switch (opcion) {
			case 0:
				shop.exportInventory();
				break;
			case 1:
				shop.showCash();
				break;

			case 2:
				shop.addProduct();
				break;

			case 3:
				shop.addStock();
				break;

			case 4:
				shop.setExpired();
				break;

			case 5:
				shop.showInventory();
				break;

			case 6:
				shop.sale();
				break;

			case 7:
				shop.showSales();
				break;

			case 8:
				shop.showSalesAmount();
				break;

			case 9:
				shop.removeProduct();
				break;

			case 10:
				System.out.println("Cerrando programa ...");
				exit = true;
				break;
			}

		} while (!exit);

	}

	private void initSession() {
		// TODO Auto-generated method stub
		
		Employee employee = new Employee("test");
		boolean logged=false;
		
		do {
			Scanner scanner = new Scanner(System.in);
			System.out.println("Introduzca numero de empleado: ");
			int employeeId = scanner.nextInt();
			
			System.out.println("Introduzca contraseña: ");
			String password = scanner.next();
			
			logged = employee.login(employeeId, password);
			if (logged) {
				System.out.println("Login correcto ");
			} else {
				System.out.println("Usuario o password incorrectos ");
			}
		} while (!logged);
				
	}

	/**
	 * load initial inventory to shop
	 */
	public void loadInventory() {
//		addProduct(new Product("Manzana", new Amount(10.00), true, 10));
//		addProduct(new Product("Pera", new Amount(20.00), true, 20));
//		addProduct(new Product("Hamburguesa", new Amount(30.00), true, 30));
//		addProduct(new Product("Fresa", new Amount(5.00), true, 20));
		// now read from file
		this.readInventory();
	}

	/**
	 * read inventory from file
	 */
	private void readInventory() {
		inventory = dao.getInventory();
	}

	/**
	 * write inventory to file
	 */
	public boolean writeInventory() {
		return dao.writeInventory(inventory);
	}
	
	/**
	 * show current total cash
	 */
	private void exportInventory() {
		Boolean validation = writeInventory();
		if (validation == false) {
			System.out.println("Inventario no exportado");
		}else {
			System.out.println("Inventario exportado con exito");
		}
		
	}

	/**
	 * show current total cash
	 */
	private void showCash() {
		System.out.println("Dinero actual: " + cash);
	}

	/**
	 * add a new product to inventory getting data from console
	 */
	public void addProduct() {
		if (isInventoryFull()) {
			System.out.println("No se pueden añadir más productos");
			return;
		}
		Scanner scanner = new Scanner(System.in);
		System.out.print("Nombre: ");
		String name = scanner.nextLine();
		System.out.print("Precio mayorista: ");
		double wholesalerPrice = scanner.nextDouble();
		System.out.print("Stock: ");
		int stock = scanner.nextInt();

		addProduct(new Product(name, new Amount(wholesalerPrice), true, stock));
	}

	/**
	 * remove a new product to inventory getting data from console
	 */
	public void removeProduct() {
		if (inventory.size() == 0) {
			System.out.println("No se pueden eliminar productos, inventario vacio");
			return;
		}
		Scanner scanner = new Scanner(System.in);
		System.out.print("Seleccione un nombre de producto: ");
		String name = scanner.next();
		Product product = findProduct(name);

		if (product != null) {
			// remove it
			if (inventory.remove(product)) {
				System.out.println("El producto " + name + " ha sido eliminado");

			} else {
				System.out.println("No se ha encontrado el producto con nombre " + name);
			}
		} else {
			System.out.println("No se ha encontrado el producto con nombre " + name);
		}
	}

	/**
	 * add stock for a specific product
	 */
	public void addStock() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Seleccione un nombre de producto: ");
		String name = scanner.next();
		Product product = findProduct(name);

		if (product != null) {
			// ask for stock
			System.out.print("Seleccione la cantidad a añadir: ");
			int stock = scanner.nextInt();
			// update stock product
			product.setStock(product.getStock() + stock);
			System.out.println("El stock del producto " + name + " ha sido actualizado a " + product.getStock());

		} else {
			System.out.println("No se ha encontrado el producto con nombre " + name);
		}
	}

	/**
	 * set a product as expired
	 */
	private void setExpired() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Seleccione un nombre de producto: ");
		String name = scanner.next();

		Product product = findProduct(name);

		if (product != null) {
			product.expire();
			System.out.println("El precio del producto " + name + " ha sido actualizado a " + product.getPublicPrice());
		}
	}

	/**
	 * show all inventory
	 */
	public void showInventory() {
		System.out.println("Contenido actual de la tienda:");
		for (Product product : inventory) {
			if (product != null) {
				System.out.println(product);
			}
		}
	}

	/**
	 * make a sale of products to a client
	 */
	public void sale() {
		// ask for client name
		Scanner sc = new Scanner(System.in);
		System.out.println("Realizar venta, escribir nombre cliente");
		String nameClient = sc.nextLine();
		Client client = new Client(nameClient);

		// sale product until input name is not 0
		// Product[] shoppingCart = new Product[10];
		ArrayList<Product> shoppingCart = new ArrayList<Product>();
		int numberShopping = 0;

		Amount totalAmount = new Amount(0.0);
		String name = "";
		while (!name.equals("0")) {
			System.out.println("Introduce el nombre del producto, escribir 0 para terminar:");
			name = sc.nextLine();

			if (name.equals("0")) {
				break;
			}
			Product product = findProduct(name);
			boolean productAvailable = false;

			if (product != null && product.isAvailable()) {
				productAvailable = true;
				totalAmount.setValue(totalAmount.getValue() + product.getPublicPrice().getValue());
				product.setStock(product.getStock() - 1);
				shoppingCart.add(product);
				numberShopping++;
				// if no more stock, set as not available to sale
				if (product.getStock() == 0) {
					product.setAvailable(false);
				}
				System.out.println("Producto añadido con éxito");
			}

			if (!productAvailable) {
				System.out.println("Producto no encontrado o sin stock");
			}
		}

		totalAmount.setValue(totalAmount.getValue() * TAX_RATE);
		// show cost total
		System.out.println("Venta realizada con éxito, total: " + totalAmount);
		
		// make payment
		if(!client.pay(totalAmount)) {
			System.out.println("Cliente debe: " + client.getBalance());;
		}

		// create sale
		Sale sale = new Sale(client, shoppingCart, totalAmount);

		// add to shop
		sales.add(sale);
//		numberSales++;

		// add to cash
		cash.setValue(cash.getValue() + totalAmount.getValue());
	}

	/**
	 * show all sales
	 */
	private void showSales() {
		System.out.println("Lista de ventas:");
		for (Sale sale : sales) {
			if (sale != null) {
				System.out.println(sale);
			}
		}
		
		// ask for client name
		Scanner sc = new Scanner(System.in);
		System.out.println("Exportar fichero ventas? S / N");
		String option = sc.nextLine();
		if ("S".equalsIgnoreCase(option)) {
			this.writeSales();
		} 
		
	}

	/**
	 * write in file the sales done
	 */
	private void writeSales() {
		// define file name based on date
		LocalDate myObj = LocalDate.now();
		String fileName = "sales_" + myObj.toString() + ".txt";
		
		// locate file, path and name
		File f = new File(System.getProperty("user.dir") + File.separator + "files" + File.separator + fileName);
				
		try {
			// wrap in proper classes
			FileWriter fw;
			fw = new FileWriter(f, true);
			PrintWriter pw = new PrintWriter(fw);
			
			// write line by line
			int counterSale=1;
			for (Sale sale : sales) {				
				// format first line TO BE -> 1;Client=PERE;Date=29-02-2024 12:49:50;
				StringBuilder firstLine = new StringBuilder(counterSale+";Client="+sale.getClient()+";Date=" + sale.formatDate()+";");
				pw.write(firstLine.toString());
				fw.write("\n");
				
				// format second line TO BE -> 1;Products=Manzana,20.0€;Fresa,10.0€;Hamburguesa,60.0€;
				// build products line
				StringBuilder productLine= new StringBuilder();
				for (Product product : sale.getProducts()) {
					productLine.append(product.getName()+ "," + product.getPublicPrice()+";");
				}
				StringBuilder secondLine = new StringBuilder(counterSale+ ";" + "Products=" + productLine +";");						                                                
				pw.write(secondLine.toString());	
				fw.write("\n");
				
				// format third line TO BE -> 1;Amount=93.60€;
				StringBuilder thirdLine = new StringBuilder(counterSale+ ";" + "Amount=" + sale.getAmount() +";");						                                                
				pw.write(thirdLine.toString());	
				fw.write("\n");
				
				// increment counter sales
				counterSale++;
			}
			// close files
			pw.close();
			fw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	/**
	 * show total amount all sales
	 */
	private void showSalesAmount() {
		Amount totalAmount = new Amount(0.0);
		for (Sale sale : sales) {
			if (sale != null) {
				totalAmount.setValue(totalAmount.getValue() + sale.getAmount().getValue());
			}
		}
		System.out.println("Total cantidad ventas:");
		System.out.println(totalAmount);
	}

	/**
	 * add a product to inventory
	 * 
	 * @param product
	 */
	public void addProduct(Product product) {
		if (isInventoryFull()) {
			System.out.println("No se pueden añadir más productos, se ha alcanzado el máximo de " + inventory.size());
			return;
		}
		inventory.add(product);
		dao.addProduct(product);
		numberProducts++;
	}
	
	/**
	 * update a product in inventory
	 * 
	 * @param product
	 */
	public void updateProduct(Product product) {
	    for (int i = 0; i < inventory.size(); i++) {
	        if (inventory.get(i).getId() == product.getId()) {
	        	inventory.get(i).setName(product.getName());
	        	inventory.get(i).setWholesalerPrice(product.getWholesalerPrice());
	        	inventory.get(i).setAvailable(product.isAvailable());
	        	inventory.get(i).setStock(product.getStock());
	            break;
	        }
	    }
		dao.updateProduct(product);
	}

	/**
	 * delete a product in inventory
	 * 
	 * @param product
	 */
	public void deleteProduct(int productId) {
		for (int i = 0; i < inventory.size(); i++) {
		    if (inventory.get(i).getId() == productId) {
		        inventory.remove(i);
		        break;
		    }
		}
		dao.deleteProduct(productId);
	}

	/**
	 * check if inventory is full or not
	 */
	public boolean isInventoryFull() {
		if (numberProducts == 10) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * find product by name
	 * 
	 * @param product name
	 */
	public Product findProduct(String name) {
		for (int i = 0; i < inventory.size(); i++) {
			if (inventory.get(i) != null && inventory.get(i).getName().equalsIgnoreCase(name)) {
				return inventory.get(i);
			}
		}
		return null;

	}

}