package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "inventory")
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	@Column
    private String name;
	@Column
	private double price;
	@Transient
    private Amount publicPrice;
	@Transient
    private Amount wholesalerPrice;
    @Column
    private boolean available;
    @Column
    private int stock;
    @Transient
    private static int totalProducts;
    
    @Transient
    public final static double EXPIRATION_RATE=0.60;
    
    public Product() {}
    
	public Product(String name, Amount wholesalerPrice, boolean available, int stock) {
		super();
		this.id = totalProducts+1;
		this.name = name;
		this.wholesalerPrice = wholesalerPrice;
		this.publicPrice = new Amount(wholesalerPrice.getValue() * 2);
		this.price = wholesalerPrice.getValue();
		this.available = available;
		this.stock = stock;
		totalProducts++;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
	    return price;
	}

	public void setPrice(double price) {
	    this.price = price;
	    this.wholesalerPrice = new Amount(price);
	    this.publicPrice = new Amount(price * 2);
	}

	public Amount getPublicPrice() {
		return publicPrice;
	}

	public void setPublicPrice(Amount publicPrice) {
		this.publicPrice = publicPrice;
	}

	public Amount getWholesalerPrice() {
		return wholesalerPrice;
	}

	public void setWholesalerPrice(Amount wholesalerPrice) {
		this.wholesalerPrice = wholesalerPrice;
	    setPrice(wholesalerPrice.getValue());
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public static int getTotalProducts() {
		return totalProducts;
	}

	public static void setTotalProducts(int totalProducts) {
		Product.totalProducts = totalProducts;
	}
	
	public void expire() {
		this.publicPrice.setValue(this.getPublicPrice().getValue()*EXPIRATION_RATE); ;
	}

	@Override
	public String toString() {
		return "Product [name=" + name + ", publicPrice=" + publicPrice + ", wholesalerPrice=" + wholesalerPrice
				+ ", available=" + available + ", stock=" + stock + "]";
	}

	
	
	
	
	

    

    
}
