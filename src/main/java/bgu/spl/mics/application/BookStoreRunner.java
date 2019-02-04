package bgu.spl.mics.application;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.JsonParser;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
import bgu.spl.mics.application.passiveObjects.JsonParser.InitialInventory;
import bgu.spl.mics.application.passiveObjects.JsonParser.Vehicles;
import bgu.spl.mics.application.passiveObjects.JsonParser.orderSchedule;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderSchedule;
import bgu.spl.mics.application.services.APIService;
import bgu.spl.mics.application.services.InventoryService;
import bgu.spl.mics.application.services.LogisticsService;
import bgu.spl.mics.application.services.ResourceService;
import bgu.spl.mics.application.services.SellingService;
import bgu.spl.mics.application.services.TimeService;

/**
 * This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system. In the
 * end, you should output serialized objects.
 */
public class BookStoreRunner {
	public static void main(String[] args) {
		Gson gson = new Gson();
		System.out.println("--------------------------------------------------------------------------");
		System.out.println("Assignment2: by Tal Litman & Ron Ohana");
		System.out.println("--------------------------------------------------------------------------");
		String jsonPath = args[0];
		try {
			JsonParser data = gson.fromJson(new FileReader(jsonPath), JsonParser.class);
			int countMicroServices = data.getServices().getNumberOfServices();
			CountDownLatch latchObject = new CountDownLatch(countMicroServices);

			// create time service
			TimeService myTimeService = new TimeService(data.getServices().getTime().getSpeed(),
					data.getServices().getTime().getDuration(), latchObject);

			// load inventory
			Inventory inv = Inventory.getInstance();
			List<InitialInventory> list = data.getInitialInventory();
			BookInventoryInfo[] loadBooks = new BookInventoryInfo[list.size()];
			for (int i = 0; i < list.size(); i++) {
				loadBooks[i] = new BookInventoryInfo(list.get(i).getBookTitle(), list.get(i).getAmount(),
						list.get(i).getPrice());
			}
			inv.load(loadBooks);

			// load ResourcesHolder
			ResourcesHolder rh = ResourcesHolder.getInstance();
			List<Vehicles> rList = data.getInitialResources().get(0).getVehicles();
			DeliveryVehicle[] vehicles = new DeliveryVehicle[rList.size()];
			for (int i = 0; i < rList.size(); i++) {
				vehicles[i] = new DeliveryVehicle(rList.get(i).getLicense(), rList.get(i).getSpeed());
			}
			rh.load(vehicles);

			// load selling Services
			ArrayList<SellingService> mySellers = new ArrayList<SellingService>();
			for (int i = 1; i <= data.getServices().getSellers(); i++)
				mySellers.add(new SellingService("selling " + i, latchObject));

			// load inventoryService
			ArrayList<InventoryService> myInventoryServices = new ArrayList<InventoryService>();
			for (int i = 1; i <= data.getServices().getInventoryService(); i++)
				myInventoryServices.add(new InventoryService("inventory " + i, latchObject));

			// load logistics
			ArrayList<LogisticsService> myLogisticsService = new ArrayList<LogisticsService>();
			for (int i = 1; i <= data.getServices().getLogistics(); i++)
				myLogisticsService.add(new LogisticsService("logistics " + i, latchObject));

			// load Resource
			ArrayList<ResourceService> myResourceServices = new ArrayList<ResourceService>();
			for (int i = 1; i <= data.getServices().getResourcesService(); i++)
				myResourceServices.add(new ResourceService("resource " + i, latchObject));

			// load customers
			HashMap<Integer, Customer> hashMapCustomers = new HashMap<>();
			ArrayList<APIService> myAPIServices = new ArrayList<APIService>();
			List<JsonParser.Customer> customers = data.getServices().getCustomers();
			for (int i = 0; i < customers.size(); i++) {
				Customer c = new Customer(customers.get(i).getId(), customers.get(i).getName(),
						customers.get(i).getAddress(), customers.get(i).getDistance(),
						customers.get(i).getCreditCard().getNumber(), customers.get(i).getCreditCard().getAmount());
				List<orderSchedule> orders = data.getServices().getCustomers().get(i).getOrderSchedule();
				ArrayList<OrderSchedule> myOrders = new ArrayList<>();
				for (int j = 0; j < orders.size(); j++) {
					myOrders.add(new OrderSchedule(orders.get(j).getBookTitle(), orders.get(j).getTick()));

				}
				hashMapCustomers.put(c.getId(), c);
				myAPIServices.add(new APIService(c, myOrders, latchObject));
			}

			// Build and start threads
			ArrayList<Thread> threads = new ArrayList<>();
			Thread t = new Thread(myTimeService, "Time thread");
			threads.add(t);
			t.start();

			for (int i = 0; i < mySellers.size(); i++) {
				t = new Thread(mySellers.get(i), mySellers.get(i).getName());
				threads.add(t);
				t.start();
			}
			for (int i = 0; i < myInventoryServices.size(); i++) {
				t = new Thread(myInventoryServices.get(i), myInventoryServices.get(i).getName());
				threads.add(t);
				t.start();
			}
			for (int i = 0; i < myLogisticsService.size(); i++) {
				t = new Thread(myLogisticsService.get(i), myLogisticsService.get(i).getName());
				threads.add(t);
				t.start();
			}
			for (int i = 0; i < myResourceServices.size(); i++) {
				t = new Thread(myResourceServices.get(i), myResourceServices.get(i).getName());
				threads.add(t);
				t.start();
			}
			for (int i = 0; i < myAPIServices.size(); i++) {
				t = new Thread(myAPIServices.get(i), myAPIServices.get(i).getName());
				threads.add(t);
				t.start();
			}

			for (Thread tempT : threads)
				try {
					tempT.join();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			// Generate the hashmaps
		generateOutput(hashMapCustomers, args);

		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void generateOutput(HashMap<Integer, Customer> hashMapCustomers, String[] args) {
		try {
			FileOutputStream file = new FileOutputStream(args[1]);
			ObjectOutputStream out = new ObjectOutputStream(file);
			out.writeObject(hashMapCustomers);
			out.close();
			file.close();
		}

		catch (IOException ex) {
			System.out.println("IOEXception is caught When Trying To Write The Customers hashmap at " + args[1]);
		}
		Inventory.getInstance().printInventoryToFile(args[2]);
		MoneyRegister.getInstance().printOrderReceipts(args[3]);
		MoneyRegister moneyRegister = MoneyRegister.getInstance();
		try {
			FileOutputStream file = new FileOutputStream(args[4]);
			ObjectOutputStream out = new ObjectOutputStream(file);
			out.writeObject(moneyRegister);
			out.close();
			file.close();
		} catch (IOException ex) {
			System.out.println("IOEXception is caught When Trying To Write The Money Register");
		}

	}
}
