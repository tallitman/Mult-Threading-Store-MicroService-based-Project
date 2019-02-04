package bgu.spl.mics.application.passiveObjects;

import java.util.List;
/**
 * Parses JSON, converting it into {@link List} that holds the initialized data of the program
 *
 */
public class JsonParser {
	private List<InitialInventory> initialInventory;
	private List<InitialResources> initialResources;
	private Services services;

	public List<InitialResources> getInitialResources() {
		return initialResources;
	}

	public Services getServices() {
		return services;
	}

	public List<InitialInventory> getInitialInventory() {
		return initialInventory;
	}

	public class InitialInventory {
		private String bookTitle;
		private int amount;
		private int price;

		public String getBookTitle() {
			return bookTitle;
		}

		public int getAmount() {
			return amount;
		}

		public int getPrice() {
			return price;
		}

	}

	public class InitialResources {
		List<Vehicles> vehicles;

		public List<Vehicles> getVehicles() {
			return vehicles;
		}

	}

	public class Vehicles {
		private int license;
		private int speed;

		public int getLicense() {
			return license;
		}

		public int getSpeed() {
			return speed;
		}

	}

	public class Services {
		private Time time;
		private int selling;
		private int inventoryService;
		private int logistics;
		private int resourcesService;
		private List<Customer> customers;

		public Time getTime() {
			return time;
		}

		public int getSelling() {
			return selling;
		}

		public int getInventoryService() {
			return inventoryService;
		}

		public int getLogistics() {
			return logistics;
		}

		public int getResourcesService() {
			return resourcesService;
		}

		public int getSellers() {
			return selling;
		}

		public int getNumberOfServices() {
			int num = 0;
			num += selling + inventoryService + logistics + resourcesService + getCustomers().size();
			return num;
		}

		public List<Customer> getCustomers() {
			return customers;
		}
	}

	public class Customer {
		private int id;
		private String name;
		private String address;
		private int distance;

		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String getAddress() {
			return address;
		}

		public int getDistance() {
			return distance;
		}

		public CreditCard getCreditCard() {
			return creditCard;
		}

		public List<orderSchedule> getOrderSchedule() {
			return orderSchedule;
		}

		private CreditCard creditCard;
		private List<orderSchedule> orderSchedule;

	}

	public class orderSchedule {
		private String bookTitle;
		private int tick;

		public String getBookTitle() {
			return bookTitle;
		}

		public int getTick() {
			return tick;
		}

	}

	public class CreditCard {
		private int number;
		private int amount;

		public int getNumber() {
			return number;
		}

		public int getAmount() {
			return amount;
		}
	}

	public class Time {
		private int speed;
		private int duration;

		public int getSpeed() {
			return this.speed;
		}

		public int getDuration() {
			return this.duration;
		}
	}

}
