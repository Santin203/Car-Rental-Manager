/*
 * Prompt: Create a Java Swing-based UI for managing a car rental shop. 
 * The UI should allow users to rent and return cars, view transactions, 
 * and check the shop's status. Include tabs for each operation and ensure 
 * the UI is user-friendly and visually appealing.
*/

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
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
        
        // Add a new tab for Lot Management
        JPanel lotManagementPanel = new JPanel();
        lotManagementPanel.setLayout(new BoxLayout(lotManagementPanel, BoxLayout.Y_AXIS));
        
        // Initialize lot section
        JPanel initLotPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField lotNameField = new JTextField(15);
        lotNameField.setToolTipText("Enter lot name (e.g., new_lot)");
        JButton initLotButton = new JButton("Initialize Lot");
        initLotPanel.add(new JLabel("Lot Name:"));
        initLotPanel.add(lotNameField);
        initLotPanel.add(initLotButton);
        
        // Add car section
        JPanel addCarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> carTypeCombo = new JComboBox<>(new String[]{"Sedan", "SUV", "Van"});
        JSpinner countSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
        countSpinner.setToolTipText("Select number of cars to add");
        JTextField addToLotField = new JTextField(15);
        addToLotField.setToolTipText("Enter lot name to add cars to");
        JButton addCarsButton = new JButton("Add Cars");
        addCarPanel.add(new JLabel("Car Type:"));
        addCarPanel.add(carTypeCombo);
        addCarPanel.add(new JLabel("Count:"));
        addCarPanel.add(countSpinner);
        addCarPanel.add(new JLabel("To Lot:"));
        addCarPanel.add(addToLotField);
        addCarPanel.add(addCarsButton);
        
        // Remove car section
        JPanel removeCarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField plateField = new JTextField(10);
        plateField.setToolTipText("Enter license plate to remove");
        JTextField removeFromLotField = new JTextField(15);
        removeFromLotField.setToolTipText("Enter lot name to remove car from");
        JButton removeCarButton = new JButton("Remove Car");
        removeCarPanel.add(new JLabel("License Plate:"));
        removeCarPanel.add(plateField);
        removeCarPanel.add(new JLabel("From Lot:"));
        removeCarPanel.add(removeFromLotField);
        removeCarPanel.add(removeCarButton);
        
        // Results area for lot management operations
        JTextArea lotManagementResults = new JTextArea(10, 50);
        lotManagementResults.setEditable(false);
        JScrollPane lotResultsScrollPane = new JScrollPane(lotManagementResults);
        
        // Add all the sections to the lot management panel
        lotManagementPanel.add(initLotPanel);
        lotManagementPanel.add(addCarPanel);
        lotManagementPanel.add(removeCarPanel);
        lotManagementPanel.add(lotResultsScrollPane);
        
        tabbedPane.addTab("Lot Management", lotManagementPanel);

        add(tabbedPane);
        
        // Add action listeners for lot management buttons
        initLotButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String lotName = lotNameField.getText().trim() + "_lot";
                if (lotName.isEmpty()) {
                    lotManagementResults.setText("Please enter a lot name.");
                    return;
                }
                
                // Create the lot file if it doesn't exist
                java.io.File lotFile = new java.io.File(lotName);
                try {
                    if (lotFile.createNewFile()) {
                        lotManagementResults.setText("Lot '" + lotName + "' initialized successfully.");
                    } else {
                        lotManagementResults.setText("Lot '" + lotName + "' already exists.");
                    }
                } catch (java.io.IOException ex) {
                    lotManagementResults.setText("Error creating lot: " + ex.getMessage());
                }
            }
        });
        
        addCarsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String lotName = addToLotField.getText().trim() + "_lot";
                String carType = (String) carTypeCombo.getSelectedItem();
                int count = (int) countSpinner.getValue();
                
                if (lotName.isEmpty()) {
                    lotManagementResults.setText("Please enter a lot name.");
                    return;
                }

                ILotManager lotManager = new LotManager(lotName);
                lotManager.addCars(carType, count);
                
                lotManagementResults.setText("Added " + count + " " + carType + " to lot '" + lotName + "'.");
            }
        });
        
        removeCarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String lotName = removeFromLotField.getText().trim() + "_lot";
                String plate = plateField.getText().trim().toUpperCase();
                
                if (lotName.isEmpty()) {
                    lotManagementResults.setText("Please enter a lot name.");
                    return;
                }
                
                if (plate.isEmpty()) {
                    lotManagementResults.setText("Please enter a license plate.");
                    return;
                }
                
                ILotManager lotManager = new LotManager(lotName);
                lotManager.removeCar(plate);
                
                lotManagementResults.setText("Attempted to remove car with plate '" + plate + "' from lot '" + lotName + "'.");
            }
        });

        rentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String type = rentTypeField.getText().trim();
                rentMessagesArea.setText("");
                if (!type.isEmpty()) {
                    ICar rentedCar = shop.removeCarByType(type);
                    String source = "Shop";
                    boolean discountApplied = false;

                    if (rentedCar == null) {

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
        
        /*
         * Prompt: Update return car action to display messages in the text area
         */
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String plate = returnPlateField.getText().trim().toUpperCase();
                String kmStr = returnKmField.getText().trim();
                returnMessagesArea.setText("");
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
                FileHandler.removeRentedCarFromFile("rented_cars.txt", returningCar);

                double amount = shop.returnCar(returningCar, kilometers, discountApplied);
                double discountedAmount = 0.0;
                if (discountApplied) {
                    discountedAmount = kilometers * 0.1;
                }
                ITransaction transaction = new Transaction(amount, discountApplied, returningCar, discountedAmount);
                shop.addTransaction(transaction);

                FileHandler.saveShopCarsToFile(shop.getLocation(), shop.getCars());
                FileHandler.updateLicensePlateLocation("licensePlates.txt", plate, shop.getLocation());
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
                DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer(); //
                centerRenderer.setHorizontalAlignment(SwingConstants.CENTER); //
                for (int i = 0; i < transactionsTable.getColumnModel().getColumnCount(); i++) { //
                    transactionsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer); //
                } //
            }
        });

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
                DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer(); //
                centerRenderer.setHorizontalAlignment(SwingConstants.CENTER); //
                for (int i = 0; i < shopStatusTable.getColumnModel().getColumnCount(); i++) { //
                    shopStatusTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer); //
                } //

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
    
    private void appendOutput(String text) {
        outputArea.append(text + "\n");
    }

    private void updateShopSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Shop Summary:\n");
        sb.append("Full Spaces: " + shop.getCars().size() + "\n");
        sb.append("Available Spaces: " + shop.availableSpaces() + "\n");
        sb.append("Cars details:\n");
        for (ICar car : shop.getCars()) {
            sb.append("- " + car.getType() + " | " + car.getPlate() + " | " 
                      + car.getMileage() + " km\n");
        }
        appendOutput(sb.toString());
    }
    
    /*
     * Prompt: Make a main method to run this GUI, allowing the user to specify
     * the shop location, maximum spaces, and allowed lots via command-line arguments.
     */
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

        // Check if a shop configuration file exists for this location
        if (FileHandler.shopConfigExists(shopLocation)) {
            // If configuration exists, load it and ignore command line values
            List<Object> config = FileHandler.loadShopConfig(shopLocation);
            maxSpaces = (int) config.get(0);
            allowedLots = (List<String>) config.get(1);
            System.out.println("Loading existing shop configuration for " + shopLocation + ":");
            System.out.println("- Spaces available: " + maxSpaces);
            System.out.println("- Allowed lots: " + String.join(", ", allowedLots));
        } else {
            // If no configuration exists, create one with the command line values
            if (allowedLots.isEmpty()) {
                System.out.println("No lots specified. Exiting.");
                return;
            }
            FileHandler.saveShopConfig(shopLocation, maxSpaces, allowedLots);
            System.out.println("Created new shop configuration for " + shopLocation);
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
