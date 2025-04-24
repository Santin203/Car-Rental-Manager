import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class CarRentalManagerUI extends JFrame {
    private ShopManager shopManager;
    private RentalShop shop;
    
    // Components for shop operations
    private JTextArea outputArea;
    private JTextField rentTypeField;
    private JTextField returnPlateField;
    private JTextField returnKmField;
    
    public CarRentalManagerUI(ShopManager shopManager, RentalShop shop) {
        this.shop = shop;
        this.shopManager = shopManager;
        initUI();
    }
    
    private void initUI() {
        setTitle("Car Rental Manager");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // Create a TabbedPane for separate views
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Modify the Shop tab to include separate tabs for Renting and Returning
        JPanel shopPanel = new JPanel(new BorderLayout());
        JTabbedPane shopTabbedPane = new JTabbedPane();

        // Renting tab
        JPanel rentPanel = new JPanel(new FlowLayout());
        rentTypeField = new JTextField(10);
        rentTypeField.setToolTipText("Enter car type (e.g., Sedan)");
        JButton rentButton = new JButton("Rent Car");
        rentPanel.add(new JLabel("Car Type:"));
        rentPanel.add(rentTypeField);
        rentPanel.add(rentButton);

        // Add a text area below the Rent Car button to display messages
        JTextArea rentMessagesArea = new JTextArea(5, 50);
        rentMessagesArea.setEditable(false);
        JScrollPane rentMessagesScrollPane = new JScrollPane(rentMessagesArea);
        rentPanel.add(rentMessagesScrollPane);

        shopTabbedPane.addTab("Rent", rentPanel);

        // Returning tab
        JPanel returnPanel = new JPanel(new FlowLayout());
        returnPlateField = new JTextField(10);
        returnPlateField.setToolTipText("Enter license plate");
        returnKmField = new JTextField(5);
        returnKmField.setToolTipText("Enter kilometers driven");
        JButton returnButton = new JButton("Return Car");
        returnPanel.add(new JLabel("License Plate:"));
        returnPanel.add(returnPlateField);
        returnPanel.add(new JLabel("Km:"));
        returnPanel.add(returnKmField);
        returnPanel.add(returnButton);

        // Add a text area below the Return Car button to display messages
        JTextArea returnMessagesArea = new JTextArea(5, 50);
        returnMessagesArea.setEditable(false);
        JScrollPane returnMessagesScrollPane = new JScrollPane(returnMessagesArea);
        returnPanel.add(returnMessagesScrollPane);

        shopTabbedPane.addTab("Return", returnPanel);

        // Ensure the output area is added to the shop panel
        JPanel shopMainPanel = new JPanel(new BorderLayout());
        shopMainPanel.add(shopTabbedPane, BorderLayout.CENTER);
        JScrollPane shopOutputScrollPane = new JScrollPane(outputArea);
        shopMainPanel.add(shopOutputScrollPane, BorderLayout.SOUTH);
        tabbedPane.addTab("Shop", shopMainPanel);
        
        // Output area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        shopPanel.add(scrollPane, BorderLayout.CENTER);

        // Enhance the appearance of the subtabs
        shopTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        shopTabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Transactions panel
        JPanel transactionsPanel = new JPanel(new BorderLayout());
        JTable transactionsTable = new JTable();
        JScrollPane transactionsScrollPane = new JScrollPane(transactionsTable);
        transactionsPanel.add(transactionsScrollPane, BorderLayout.CENTER);
        JButton refreshTransactions = new JButton("Refresh Transactions");
        transactionsPanel.add(refreshTransactions, BorderLayout.SOUTH);
        tabbedPane.addTab("Transactions", transactionsPanel);
        
        // Add a new tab for Shop Status
        JPanel shopStatusPanel = new JPanel(new BorderLayout());
        JTable shopStatusTable = new JTable();
        JScrollPane shopStatusScroll = new JScrollPane(shopStatusTable);
        shopStatusPanel.add(shopStatusScroll, BorderLayout.CENTER);
        JButton refreshShopStatus = new JButton("Refresh Shop Status");
        shopStatusPanel.add(refreshShopStatus, BorderLayout.SOUTH);
        tabbedPane.addTab("Shop Status", shopStatusPanel);

        add(tabbedPane);
        
        // Update rent car action to display messages in the text area
        rentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String type = rentTypeField.getText().trim();
                rentMessagesArea.setText(""); // Clear previous messages
                if (!type.isEmpty()) {
                    ICar rentedCar = shop.removeCarByType(type);
                    String source = "Shop";
                    boolean discountApplied = false;

                    if (rentedCar == null) {
                        // Try to rent from lots if not available locally
                        for (String lotName : shopManager.getAllowedLots()) {
                            ICarLot carLot = new CarLot(lotName);
                            FileHandler.loadFromFile(lotName + ".txt", carLot);

                            rentedCar = carLot.removeCarByType(shop.getLocation(), type);
                            if (rentedCar != null) {
                                source = lotName;
                                discountApplied = true;
                                FileHandler.saveCarsToFile(lotName + ".txt", carLot.getCars(), false);
                                rentMessagesArea.append("Car " + rentedCar.getPlate() + " moved to shop from lot: " + lotName + " to be rented\n");
                                break;
                            }
                        }
                    }

                    if (rentedCar != null) {
                        FileHandler.saveRentedCarsToFileAppend("rented_cars.txt", List.of(rentedCar), discountApplied);
                        FileHandler.updateLicensePlateLocation("licensePlates.txt", rentedCar.getPlate(), "rented");

                        JOptionPane.showMessageDialog(
                            CarRentalManagerUI.this,
                            "License Plate: " + rentedCar.getPlate() + "\n" +
                            "Type: " + rentedCar.getType() + "\n" +
                            "Source: " + source + "\n" +
                            "Discount Applied: " + (discountApplied ? "Yes" : "No"),
                            "Car Rented Successfully",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        appendOutput("Rent operation performed for type: " + type);
                        rentMessagesArea.append(shopManager.balanceShopCars());
                        updateShopSummary();
                    } else {
                        rentMessagesArea.append("No cars of type '" + type + "' are available in stock or lots.\n");
                        JOptionPane.showMessageDialog(
                            CarRentalManagerUI.this,
                            "No cars of type '" + type + "' are available in stock or lots.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                } else {
                    rentMessagesArea.append("Please enter a car type to rent.\n");
                    appendOutput("Please enter a car type to rent.");
                }
            }
        });
        
        // Update return car action to display messages in the text area
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String plate = returnPlateField.getText().trim().toUpperCase();
                String kmStr = returnKmField.getText().trim();
                returnMessagesArea.setText(""); // Clear previous messages
                int kilometers;

                if (plate == null || plate.isEmpty()) {
                    returnMessagesArea.append("Invalid license plate.\n");
                    JOptionPane.showMessageDialog(
                        CarRentalManagerUI.this,
                        "Invalid license plate.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                try {
                    kilometers = Integer.parseInt(kmStr);
                    if (kilometers < 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException ex) {
                    returnMessagesArea.append("Invalid kilometers entered.\n");
                    JOptionPane.showMessageDialog(
                        CarRentalManagerUI.this,
                        "Invalid kilometers entered.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                // Load rented cars from file
                List<ICar> rentedCars = FileHandler.getCarsFromFile("rented_cars.txt");
                if (rentedCars == null) {
                    returnMessagesArea.append("Error loading rented cars.\n");
                    JOptionPane.showMessageDialog(
                        CarRentalManagerUI.this,
                        "Error loading rented cars.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                // Find the car to return
                ICar returningCar = null;
                for (ICar car : rentedCars) {
                    if (car.getPlate().equals(plate)) {
                        returningCar = car;
                        break;
                    }
                }

                if (returningCar == null) {
                    returnMessagesArea.append("Car with license plate '" + plate + "' not found in rented cars.\n");
                    JOptionPane.showMessageDialog(
                        CarRentalManagerUI.this,
                        "Car with license plate '" + plate + "' not found in rented cars.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                boolean discountApplied = FileHandler.getDiscountApplied(plate);

                rentedCars.remove(returningCar);
                FileHandler.saveCarsToFile("rented_cars.txt", rentedCars, false);

                // Add the car back to the shop
                double amount = shop.returnCar(returningCar, kilometers, discountApplied);
                double discountedAmount = 0.0;
                if (discountApplied) {
                    discountedAmount = kilometers * 0.1;
                }
                ITransaction transaction = new Transaction(amount, discountApplied, returningCar, discountedAmount);
                shop.addTransaction(transaction);

                FileHandler.saveShopCarsToFile(shop.getLocation(), shop.getCars());
                FileHandler.updateLicensePlateLocation("licensePlates.txt", plate, shop.getLocation());

                // Save the transaction to the shop's transaction file
                FileHandler.saveTransactionToFile(shop.getLocation() + "_transactions.txt", transaction);

                String message = "Car Returned Successfully:\n" +
                                 "License Plate: " + returningCar.getPlate() + "\n" +
                                 "Type: " + returningCar.getType() + "\n" +
                                 "Total to Pay: $" + amount + "\n" +
                                 "Discount Applied: " + (discountApplied ? "Yes" : "No");

                returnMessagesArea.append(shopManager.balanceShopCars());

                JOptionPane.showMessageDialog(
                    CarRentalManagerUI.this,
                    message,
                    "Return Confirmation",
                    JOptionPane.INFORMATION_MESSAGE
                );

                appendOutput("Return operation performed for plate: " + plate);
                updateShopSummary();
            }
        });
        
        // Refresh transactions action
        refreshTransactions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] columnNames = {"Car", "Amount", "Discount Applied"};
                List<ITransaction> transactions = shop.getTransactions();
                Object[][] data = new Object[transactions.size()][3];
                for (int i = 0; i < transactions.size(); i++) {
                    ITransaction transaction = transactions.get(i);
                    data[i][0] = transaction.getCar().getPlate();
                    data[i][1] = transaction.getAmount();
                    data[i][2] = transaction.isDiscountApplied() ? "Yes" : "No";
                }
                transactionsTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));

                // Retrieve and display the summary line
                List<Object> summary = FileHandler.getTransactionSummary(shop.getLocation() + "_transactions.txt");
                double totalRevenue = (double) summary.get(0);
                double totalDiscounts = (double) summary.get(1);

                JOptionPane.showMessageDialog(
                    CarRentalManagerUI.this,
                    "Total Revenue: $" + totalRevenue + "\n" +
                    "Total Discounts: $" + totalDiscounts + "\n" +
                    "Total Transactions: " + transactions.size(),
                    "Transactions Summary",
                    JOptionPane.INFORMATION_MESSAGE
                );

                // Center the contents of each cell in the transactions table
                DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
                centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
                for (int i = 0; i < transactionsTable.getColumnModel().getColumnCount(); i++) {
                    transactionsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
                }
            }
        });

        // Refresh shop status action
        refreshShopStatus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shopManager.balanceShopCars();
                updateShopSummary();
                String[] columnNames = {"Car Type", "License Plate", "Mileage"};
                List<ICar> cars = shop.getCars();
                Object[][] data = new Object[cars.size()][3];
                for (int i = 0; i < cars.size(); i++) {
                    ICar car = cars.get(i);
                    data[i][0] = car.getType();
                    data[i][1] = car.getPlate();
                    data[i][2] = car.getMileage();
                }
                shopStatusTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));

                // Center the contents of each cell in the shop status table
                DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
                centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
                for (int i = 0; i < shopStatusTable.getColumnModel().getColumnCount(); i++) {
                    shopStatusTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
                }

                // Display additional shop status information
                int emptySpaces = shop.availableSpaces();
                int fullSpaces = shop.getCars().size();
                double totalRevenue = shop.getTotalRevenue();

                JOptionPane.showMessageDialog(
                    CarRentalManagerUI.this,
                    "Empty Spaces: " + emptySpaces + "\n" +
                    "Full Spaces: " + fullSpaces + "\n" +
                    "Total Revenue: $" + totalRevenue,
                    "Shop Status Summary",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
    }
    
    // Append text to the output area
    private void appendOutput(String text) {
        outputArea.append(text + "\n");
    }
    
    // Refresh and display current shop summary
    private void updateShopSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Shop Summary:\n");
        sb.append("Total Cars: " + shop.getCars().size() + "\n");
        sb.append("Available Spaces: " + shop.availableSpaces() + "\n");
        sb.append("Cars details:\n");
        for (ICar car : shop.getCars()) {
            sb.append("- " + car.getType() + " | " + car.getPlate() + " | " 
                      + car.getMileage() + " km\n");
        }
        appendOutput(sb.toString());
    }
    
    public static void main(String[] args) {
        String shopLocation = "default_location";
        int maxSpaces = 10; // Default value
        List<String> allowedLots = new ArrayList<>();

        // Parse command-line arguments
        for (String arg : args) {
            if (arg.startsWith("--location=")) {
                shopLocation = arg.substring("--location=".length());
            } else if (arg.startsWith("--spaces-available=")) {
                try {
                    maxSpaces = Integer.parseInt(arg.substring("--spaces-available=".length()));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid value for spaces-available. Using default: 10");
                }
            } else if (arg.startsWith("--lots=")) {
                String lots = arg.substring("--lots=".length());
                for (String lot : lots.split(",")) {
                    allowedLots.add(lot.trim());
                }
            }
        }

        if (allowedLots.isEmpty()) {
            System.out.println("No lots specified. Exiting.");
            return;
        }

        RentalShop shop = new RentalShop(shopLocation, maxSpaces);
        ShopManager manager = new ShopManager(shop, allowedLots);

        // Load existing shop data (if any)
        manager.loadShop();

        // Launch the UI on the Event Dispatch Thread
        EventQueue.invokeLater(() -> {
            CarRentalManagerUI ui = new CarRentalManagerUI(manager, shop);
            ui.setVisible(true);
        });
    }
}
