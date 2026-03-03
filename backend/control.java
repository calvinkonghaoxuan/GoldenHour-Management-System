package stockjava;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.File; // <--- New Addition
import java.io.FilenameFilter; // <--- New Addition
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter; // <--- New Addition
import java.util.ArrayList;
import java.util.Collections; // <--- New Addition
import java.util.Comparator; // <--- New Addition
import java.util.HashMap; // <--- New Addition
import java.util.List;
import java.util.Map; // <--- New Addition
import java.util.Scanner;
import java.util.regex.Matcher; // <--- New Addition
import java.util.regex.Pattern; // <--- New Addition

public class control {

    static Scanner input = new Scanner(System.in);

    // ===== CURRENT USER CONTEXT =====
    static String currentEmployeeID;
    static String currentEmployeeName;
    static String currentRole;

    // Selected outlet for stock counting
    //static String selectedOutletCode;
    
    // gui getters

    public static String getCurrentEmployeeID() {
        return currentEmployeeID;
    }

    public static String getCurrentEmployeeName() {
        return currentEmployeeName;
    }

    // ==================== SET CURRENT USER ====================
    public static void setCurrentUser(String id, String name, String role) {
        currentEmployeeID = id;
        currentEmployeeName = name;
        currentRole = role;
    }

    // ==================== WRITE FILE ====================
    public static void writeFile(ArrayList<Stock> memory) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("stock.txt"))) {
            for (Stock elem : memory) {
                pw.println(elem);
            }
        } catch (Exception e) {
            System.out.println("Write error: " + e.getMessage());
        }
    }

    // ==================== READ FILE ====================
    public static void readFile(ArrayList<Stock> memory) {
        try (BufferedReader br = new BufferedReader(new FileReader("stock.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] s = line.split("\\s*,\\s*");

                LocalDate date = LocalDate.parse(s[0]);
                LocalTime time = LocalTime.parse(s[1]);
                String model = s[2];
                double price = Double.parseDouble(s[3].replace("RM", ""));
                int qty = Integer.parseInt(s[4].replace("Qty: ", ""));

                int klcc = Integer.parseInt(s[5].replace("KLCC: ", ""));
                int mv = Integer.parseInt(s[6].replace("MIDVALLEY: ", ""));
                int sv = Integer.parseInt(s[7].replace("SUNWAYVELOCITY: ", ""));
                int icm = Integer.parseInt(s[8].replace("IOICM: ", ""));
                int ll = Integer.parseInt(s[9].replace("LALAPORT: ", ""));
                int klem = Integer.parseInt(s[10].replace("KLEASTM: ", ""));
                int ns = Integer.parseInt(s[11].replace("NSENTRAL: ", ""));
                int pkl = Integer.parseInt(s[12].replace("PAVILLIONKL: ", ""));
                int u1 = Integer.parseInt(s[13].replace("U1: ", ""));
                int mt = Integer.parseInt(s[14].replace("MYTOWN: ", ""));
                int hq = Integer.parseInt(s[15].replace("HQ: ", ""));

                memory.add(new Stock(date, time, model, price,
                        klcc, mv, sv, icm, ll, klem, ns, pkl, u1, mt, hq));
            }
        } catch (Exception e) {
            System.out.println("Read error: " + e.getMessage());
        }
    }

    // ==================== SAFE INPUT METHODS ====================
    public static double readDouble(Scanner sc, String prompt) {
        double value;
        while (true) {
            System.out.print(prompt);
            if (sc.hasNextDouble()) {
                value = sc.nextDouble();
                sc.nextLine();
                break;
            } else {
                System.out.println("Invalid input! Enter a number.");
                sc.nextLine();
            }
        }
        return value;
    }

    public static int readInt(Scanner sc, String prompt) {
        int value;
        while (true) {
            System.out.print(prompt);
            if (sc.hasNextInt()) {
                value = sc.nextInt();
                sc.nextLine();
                break;
            } else {
                System.out.println("Invalid input! Enter an integer.");
                sc.nextLine();
            }
        }
        return value;
    }

    public static String readNonEmptyString(Scanner sc, String prompt) {
        String s;
        while (true) {
            System.out.print(prompt);
            s = sc.nextLine().trim();
            if (!s.isEmpty()) break;
            System.out.println("Input cannot be empty.");
        }
        return s;
    }

    public static String readCode(Scanner sc, String prompt) {
        String code;
        while (true) {
            System.out.print(prompt);
            code = sc.nextLine().toUpperCase().trim();
            if (code.matches("C6[0-9]|HQ")) break;
            System.out.println("Invalid code! Must be C60–C69 or HQ.");
        }
        return code;
    }

    // ==================== ADD STOCK ====================
    public static void addStock(ArrayList<Stock> memory) {
        Scanner sc = new Scanner(System.in);

        String model = readNonEmptyString(sc, "Model: ");
        double price = readDouble(sc, "Price: ");

        int[] c = new int[11]; // 10 outlets + HQ
        int sum = 0;
        for (int i = 0; i < 10; i++) {
            c[i] = readInt(sc, "C" + (60 + i) + " stock: ");
            sum += c[i];
        }
        c[10] = readInt(sc, "HQ stock: ");
        sum += c[10];

        memory.add(new Stock(
                LocalDate.now(),
                LocalTime.now(),
                model,
                price,
                c[0], c[1], c[2], c[3], c[4],
                c[5], c[6], c[7], c[8], c[9], c[10]
        ));

        System.out.println("✅ Stock added successfully!");
    }

    // ==================== SHOW ALL STOCK ====================
    public static void allStock(ArrayList<Stock> list) {
        for (Stock s : list) {
            System.out.println(s);
        }
    }

    // ==================== UPDATE STOCK ====================
    public static void updateStock(ArrayList<Stock> list,
                                   String model,
                                   double priceChange,
                                   int qtyChange,
                                   String code) {

        for (Stock s : list) {
            if (!s.getModel().equalsIgnoreCase(model)) continue;

            if (s.getPrice() + priceChange < 0) {
                System.out.println("❌ Invalid price: cannot be negative");
                return;
            }

            int currentOutletStock = s.getQuantityByOutlet(code);
            if (currentOutletStock + qtyChange < 0) {
                System.out.println("❌ Update failed: Stock for " + code + " cannot be negative!");
                return;
            }

            s.updatePrice(priceChange);
            s.plusStock(code, qtyChange);
            s.updateDate(LocalDate.now());
            s.updateTime(LocalTime.now());

            recordUpdate(model, priceChange, qtyChange, code);

            System.out.println("✅ Update successful and recorded");
            return;
        }
        System.out.println("❌ Model not found");
    }

    // ==================== RECORD UPDATE ====================
    public static void recordUpdate(String model,
                                    double priceChange,
                                    int qtyChange,
                                    String code) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("update_log.txt", true))) {
            pw.println(
                    LocalDate.now() + "," +
                            LocalTime.now() + "," +
                            currentEmployeeID + "," +
                            currentEmployeeName + "," +
                            currentRole + "," +
                            "MODEL=" + model + "," +
                            "PRICE_CHANGE=" + priceChange + "," +
                            "QTY_CHANGE=" + qtyChange + "," +
                            "CODE=" + code
            );
        } catch (Exception e) {
            System.out.println("❌ Failed to record update: " + e.getMessage());
        }
    }

    // ==================== CHECK ONE STOCK ====================
    public static void checkOneStock(ArrayList<Stock> list, String model) {
        for (Stock s : list) {
            if (s.getModel().equalsIgnoreCase(model)) {
                System.out.println("=== STOCK INFO ===");
                System.out.println(s);
                return;
            }
        }
        System.out.println("❌ Stock not found");
    }

    // ==================== STOCK COUNT ====================
    public static void performStockCount(String sessionType, List<Stock> stockList) {
        int total = 0, correct = 0, mismatch = 0;

        System.out.println("=== " + sessionType + " Stock Count ===");

        for (Stock s : stockList) {
            total++;
            System.out.println("Model: " + s.getModel() + " - Counted: " + s.getCountedQuantity());
            System.out.println("Store Record: " + s.getQuantityByOutlet(selectedOutletCode));

            if (s.isTallyCorrectByOutlet(selectedOutletCode)) {
                System.out.println("Stock tally correct.");
                correct++;
            } else {
                System.out.println("! Mismatch detected (" + s.getDifferenceByOutlet(selectedOutletCode) + " units)");
                mismatch++;
            }
            System.out.println();
        }

        System.out.println("Total Models Checked: " + total);
        System.out.println("Tally Correct: " + correct);
        System.out.println("Mismatches: " + mismatch);
        System.out.println(sessionType + " stock count completed.");
    }

    // ==================== STOCK IN/OUT ====================
    public static void stockIn(ArrayList<Stock> stockList, String receivingOutlet) {
        Scanner sc = new Scanner(System.in);

        String model = readNonEmptyString(sc, "Enter model to receive: ");
        String from = readCode(sc, "Receiving from outlet (C60-C69/HQ): ");
        int qty = readInt(sc, "Enter quantity to transfer: ");

        for (Stock s : stockList) {
            if (!s.getModel().equalsIgnoreCase(model)) continue;

            int available = s.getQuantityByOutlet(from);
            if (available < qty) {
                System.out.println("❌ Not enough stock in " + from);
                return;
            }
            
            s.plusStock(receivingOutlet, qty);
            s.minusStock(from, qty);

            recordTransfer(model, from, receivingOutlet, qty, "IN");
            System.out.println("✅ Stock received successfully!");
            return;
        }
        System.out.println("❌ Model not found");
    }

    public static void stockOut(ArrayList<Stock> stockList, String sendingOutlet) {
        Scanner sc = new Scanner(System.in);

        String model = readNonEmptyString(sc, "Enter model to send: ");
        String to = readCode(sc, "Sending to outlet (C60-C69/HQ): ");
        int qty = readInt(sc, "Enter quantity to transfer: ");

        for (Stock s : stockList) {
            if (!s.getModel().equalsIgnoreCase(model)) continue;

            int available = s.getQuantityByOutlet(sendingOutlet);
            if (available < qty) {
                System.out.println("❌ Not enough stock in " + sendingOutlet);
                return;
            }
            
            s.minusStock(sendingOutlet, qty);
            s.plusStock(to, qty);

            recordTransfer(model, sendingOutlet, to, qty, "OUT");
            System.out.println("✅ Stock sent successfully!");
            return;
        }
        System.out.println("❌ Model not found");
    }

    // ==================== RECORD TRANSFER ====================
    public static void recordTransfer(String model, String from, String to, int qty, String type) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("transaction_log.txt", true))) {
            pw.println(LocalDate.now() + "," + LocalTime.now() + "," +
                    currentEmployeeID + "," + currentEmployeeName + "," + currentRole + "," +
                    "TYPE=" + type + ",MODEL=" + model + ",FROM=" + from + ",TO=" + to + ",QTY=" + qty);
        } catch (Exception e) {
            System.out.println("❌ Failed to record transfer: " + e.getMessage());
        }
    }

    // ==================== AFTER LOGIN LOOP ====================
    public static void afterLogin(ArrayList<Stock> stockList) {
        readFile(stockList);

        while (true) {
            String cmd = readNonEmptyString(input, "Command (stock/update/check/showall/stockcount/stockin/stockout/sales/done): ").toLowerCase();

            switch (cmd) {
                case "stock":
                    addStock(stockList);
                    break;
                case "update":
                    String m = readNonEmptyString(input, "Model: ");
                    double p = readDouble(input, "Price change: ");
                    int q = readInt(input, "Qty change: ");
                    String c = readCode(input, "Code (C60-C69/HQ): ");
                    updateStock(stockList, m, p, q, c);
                    break;
                case "showall":
                    allStock(stockList);
                    break;
                case "check":
                    String model = readNonEmptyString(input, "Enter model to check: ");
                    checkOneStock(stockList, model);
                    break;
                case "stockcount":
                    selectedOutletCode = readCode(input, "Enter outlet code (C60-C69/HQ): ");
                    String session = readNonEmptyString(input, "Session (Morning/Night): ");
                    for (Stock s : stockList) {
                        int counted = readInt(input, "Enter counted quantity for model " + s.getModel() + " at outlet " + selectedOutletCode + ": ");
                        s.setCountedQuantity(counted, selectedOutletCode);
                    }
                    performStockCount(session, stockList);
                    break;
                case "stockin":
                    stockIn(stockList, Login.yourOutletCode());
                    break;
                case "stockout":
                    stockOut(stockList, Login.yourOutletCode());
                    break;
                case "sales":
                    Sale.recordNewSale(stockList, currentEmployeeID, currentEmployeeName);
                    break;
                case "done":
                    writeFile(stockList);
                    return;
                default:
                    System.out.println("Invalid command!");
            }
        }
    }
    
    public static ArrayList<Stock> stockList;
    private static String selectedOutletCode;

    public static String getSelectedOutletCode() { return selectedOutletCode; }
    public static void setSelectedOutletCode(String code) { selectedOutletCode = code; }

    // ===== NEW: record sale at specific outlet for GUI =====
    public static boolean recordSaleAtOutlet(ArrayList<Stock> stockList, String model, int qty, String outletCode) throws Exception {
        for (Stock s : stockList) {
            if (s.getModel().equalsIgnoreCase(model)) {
                if (s.getQuantityByOutlet(outletCode) < qty) {
                    throw new Exception("Not enough stock for " + model + " at " + outletCode);
                }
                s.removeStock(outletCode, qty); // deduct stock
                return true;
            }
        }
        throw new Exception("Model " + model + " not found!");
    }
    
    // ==================== GUI-FRIENDLY STOCK IN ====================
    public static void stockIn(ArrayList<Stock> stockList, String receivingOutlet, String model, String fromOutlet, int qty) {
        for (Stock s : stockList) {
            if (!s.getModel().equalsIgnoreCase(model)) continue;

            int available = s.getQuantityByOutlet(fromOutlet);
            if (available < qty) throw new RuntimeException("Not enough stock in " + fromOutlet);

            s.plusStock(receivingOutlet, qty);
            s.minusStock(fromOutlet, qty);
            recordTransfer(model, fromOutlet, receivingOutlet, qty, "IN");
            return;
        }
        throw new RuntimeException("Model not found");
    }

    // ==================== GUI-FRIENDLY STOCK OUT ====================
    public static void stockOut(ArrayList<Stock> stockList, String sendingOutlet, String model, String toOutlet, int qty) {
        for (Stock s : stockList) {
            if (!s.getModel().equalsIgnoreCase(model)) continue;

            int available = s.getQuantityByOutlet(sendingOutlet);
            if (available < qty) throw new RuntimeException("Not enough stock in " + sendingOutlet);

            s.minusStock(sendingOutlet, qty);
            s.plusStock(toOutlet, qty);
            recordTransfer(model, sendingOutlet, toOutlet, qty, "OUT");
            return;
        }
        throw new RuntimeException("Model not found");
    }
    
    // ==================== GUI SAFE UPDATE STOCK ====================
    public static void updateStockGUI(ArrayList<Stock> list,
                                      String model,
                                      double priceChange,
                                      int qtyChange,
                                      String code) throws Exception {

        for (Stock s : list) {
            if (!s.getModel().equalsIgnoreCase(model)) continue;

            if (s.getPrice() + priceChange < 0) {
                throw new Exception("Price cannot be negative");
            }

            int currentOutletStock = s.getQuantityByOutlet(code);
            if (currentOutletStock + qtyChange < 0) {
                throw new Exception("Stock at " + code + " cannot be negative");
            }

            s.updatePrice(priceChange);
            s.plusStock(code, qtyChange);
            s.updateDate(java.time.LocalDate.now());
            s.updateTime(java.time.LocalTime.now());

            recordUpdate(model, priceChange, qtyChange, code);
            return;
        }

        throw new Exception("Model not found");
    }
    
    // ==================== GUI SAFE SALE ====================
    public static void recordSaleGUI(ArrayList<Stock> stockList,
                                     String model,
                                     int qty,
                                     String outletCode) throws Exception {

        for (Stock s : stockList) {
            if (!s.getModel().equalsIgnoreCase(model)) continue;

            if (s.getQuantityByOutlet(outletCode) < qty) {
                throw new Exception("Not enough stock for " + model + " at " + outletCode);
            }

            s.removeStock(outletCode, qty); // deduct stock
            return;
        }

        throw new Exception("Model not found: " + model);
    }
    
    public static boolean processPayment(double amount, double paid) {
        if (paid < amount) return false;
        return true;   // you can expand later (cash, card, receipt, etc)
    }
    
    public static Stock findStock(ArrayList<Stock> stockList, String model) {
        for (Stock s : stockList) {
            if (s.getModel().equalsIgnoreCase(model)) {
                return s;
            }
        }
        return null;
    }

    public static class SaleItem {
        public String model;
        public int qty;

        public SaleItem(String model, int qty) {
            this.model = model;
            this.qty = qty;
        }
    }

    public static void completeSale(
            ArrayList<Stock> stockList,
            ArrayList<SaleItem> cart,
            String outletCode,
            String buyerName,
            String paymentMethod,
            String empID,
            String empName
    ) throws Exception {

        double total = 0;

        // 1. Check stock availability & calculate total
        for (SaleItem item : cart) {
            Stock s = findStock(stockList, item.model);
            if (s == null) throw new Exception("Model not found: " + item.model);
            if (s.getQuantityByOutlet(outletCode) < item.qty)
                throw new Exception("Not enough stock for " + item.model);
            total += item.qty * s.getPrice();
        }

        // 2. Deduct stock
        for (SaleItem item : cart) {
            recordSaleAtOutlet(stockList, item.model, item.qty, outletCode);
        }

        // 3. Save updated stock
        writeFile(stockList);

        // 4. Prepare daily sale file
        String filename = "sales_" + java.time.LocalDate.now() + ".txt";

        try (PrintWriter pw = new PrintWriter(new FileWriter(filename, true))) {
            pw.println("================================");
            pw.println("Date      : " + java.time.LocalDate.now());
            pw.println("Time      : " + java.time.LocalTime.now());
            pw.println("Employee  : " + empID + " (" + empName + ")");
            pw.println("Outlet    : " + outletCode);
            pw.println("Buyer     : " + buyerName);
            pw.println("Payment   : " + paymentMethod);
            pw.println("Items Purchased:");

            // 5. Log items
            for (SaleItem item : cart) {
                Stock s = findStock(stockList, item.model);
                double price = s.getPrice();
                pw.printf("%s x%d @ RM%.2f = RM%.2f%n",
                        item.model, item.qty, price, item.qty * price);
            }

            pw.printf("Subtotal  : RM%.2f%n", total);
            pw.println("================================\n");
        }
    }

    // ==================== MAIN CONTROL ====================
    public static void control() {
        ArrayList<Stock> stockList = new ArrayList<>();
        afterLogin(stockList);
        writeFile(stockList);
    }

    // In control.java
    private static String currentUserID;
    private static String currentUserName;
    private static String currentUserRole;


    public static String getCurrentUserID() {
        return currentUserID;
    }

    public static String getCurrentUserName() {
        return currentUserName;
    }

    public static String getCurrentUserRole() {
        return currentUserRole;
    }

// ⬇️⬇️⬇️ ==================== NEW ADDITION (DATA ANALYTICS & HISTORY) ==================== ⬇️⬇️⬇️

    // 1. Data Structure to hold a single past sale (parsed from file) - New Addition
    public static class SaleRecord {
        private LocalDate date;
        private String model;
        private int quantity;
        private double totalPrice;

        public SaleRecord(LocalDate date, String model, int quantity, double totalPrice) {
            this.date = date;
            this.model = model;
            this.quantity = quantity;
            this.totalPrice = totalPrice;
        }

        public LocalDate getDate() { return date; }
        public String getModel() { return model; }
        public int getQuantity() { return quantity; }
        public double getTotalPrice() { return totalPrice; }
        
        @Override
        public String toString() {
            return date + " | " + model + " | Qty: " + quantity + " | Total: RM" + String.format("%.2f", totalPrice);
        }
    }

    // 2. Load Sales History from Text Files - New Addition
    // This method scans all "sales_YYYY-MM-DD.txt" files and parses them into a list.
    public static ArrayList<SaleRecord> loadSalesHistory() {
        ArrayList<SaleRecord> history = new ArrayList<>();
        File dir = new File("."); // Current directory
        
        // Find files starting with "sales_" and ending with ".txt"
        File[] files = dir.listFiles((d, name) -> name.startsWith("sales_") && name.endsWith(".txt"));

        if (files == null) return history;

        for (File file : files) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                LocalDate currentDate = null;
                
                // Pattern to match: "ModelName x2 @ RM10.00 = RM20.00"
                // Regex explanation: (anything) x(digits) @ RM(digits/dots) = RM(digits/dots)
                Pattern itemPattern = Pattern.compile("(.*?) x(\\d+) @ RM([\\d\\.]+) = RM([\\d\\.]+)");

                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    
                    if (line.startsWith("Date")) {
                        // Extract date: "Date      : 2026-01-13"
                        String dateStr = line.substring(line.indexOf(":") + 1).trim();
                        try {
                            currentDate = LocalDate.parse(dateStr);
                        } catch (Exception e) { currentDate = null; } // Skip if date invalid
                    } 
                    else if (currentDate != null && line.contains("@ RM")) {
                        // Attempt to parse item line
                        Matcher m = itemPattern.matcher(line);
                        if (m.find()) {
                            String model = m.group(1).trim();
                            int qty = Integer.parseInt(m.group(2));
                            // double unitPrice = Double.parseDouble(m.group(3)); // unused but available
                            double total = Double.parseDouble(m.group(4));
                            
                            history.add(new SaleRecord(currentDate, model, qty, total));
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Error reading sales file: " + file.getName());
            }
        }
        return history;
    }

    // 3. Filter Logic (Filter by Date Range) - New Addition
    public static ArrayList<SaleRecord> filterSalesByDate(ArrayList<SaleRecord> allSales, LocalDate start, LocalDate end) {
        ArrayList<SaleRecord> filtered = new ArrayList<>();
        for (SaleRecord r : allSales) {
            if ((r.getDate().isEqual(start) || r.getDate().isAfter(start)) && 
                (r.getDate().isEqual(end) || r.getDate().isBefore(end))) {
                filtered.add(r);
            }
        }
        return filtered;
    }

    // 4. Sorting Logic - New Addition
    public static void sortSalesByDate(ArrayList<SaleRecord> list, boolean ascending) {
        Collections.sort(list, new Comparator<SaleRecord>() {
            @Override
            public int compare(SaleRecord o1, SaleRecord o2) {
                return ascending ? o1.getDate().compareTo(o2.getDate()) : o2.getDate().compareTo(o1.getDate());
            }
        });
    }

    public static void sortSalesByAmount(ArrayList<SaleRecord> list, boolean ascending) {
        Collections.sort(list, new Comparator<SaleRecord>() {
            @Override
            public int compare(SaleRecord o1, SaleRecord o2) {
                return ascending ? Double.compare(o1.getTotalPrice(), o2.getTotalPrice()) 
                                 : Double.compare(o2.getTotalPrice(), o1.getTotalPrice());
            }
        });
    }

    // 5. Analytics: Total Revenue - New Addition
    public static double calculateTotalRevenue(ArrayList<SaleRecord> list) {
        double sum = 0;
        for (SaleRecord r : list) {
            sum += r.getTotalPrice();
        }
        return sum;
    }

    // 6. Analytics: Most Sold Model - New Addition
    public static String getMostSoldModel(ArrayList<SaleRecord> list) {
        if (list.isEmpty()) return "N/A";
        
        Map<String, Integer> counts = new HashMap<>();
        for (SaleRecord r : list) {
            counts.put(r.getModel(), counts.getOrDefault(r.getModel(), 0) + r.getQuantity());
        }
        
        String bestModel = "";
        int maxQty = 0;
        
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            if (entry.getValue() > maxQty) {
                maxQty = entry.getValue();
                bestModel = entry.getKey();
            }
        }
        return bestModel + " (" + maxQty + " units)";
    }

// ⬆️⬆️⬆️ ==================== END OF NEW ADDITION ==================== ⬆️⬆️⬆️

}