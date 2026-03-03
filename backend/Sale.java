package stockjava;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.BufferedReader; // <--- New Addition
import java.io.FileReader; // <--- New Addition
import java.io.File; // <--- New Addition
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException; // <--- New Addition
import java.util.ArrayList;
import java.util.Collections; // <--- New Addition
import java.util.Comparator; // <--- New Addition
import java.util.HashMap; // <--- New Addition
import java.util.List; // <--- New Addition
import java.util.Map; // <--- New Addition
import java.util.Scanner;
import java.util.regex.Matcher; // <--- New Addition
import java.util.regex.Pattern; // <--- New Addition

public class Sale {

    // ================== GUI / direct call version ==================
    public static void recordNewSale(
            ArrayList<Stock> stockList,
            String employeeID,
            String employeeName,
            String model,
            int qty,
            String outletCode
    ) {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();

        Stock selected = null;
        for (Stock s : stockList) {
            if (s.getModel().equalsIgnoreCase(model)) {
                selected = s;
                break;
            }
        }

        if (selected == null) {
            System.out.println("❌ Model not found: " + model);
            return;
        }

        int available = selected.getQuantityByOutlet(outletCode);
        if (qty <= 0 || qty > available) {
            System.out.println("❌ Invalid quantity. Available: " + available);
            return;
        }

        // Update stock
        selected.removeStock(outletCode, qty);

        double price = selected.getPrice();
        double total = qty * price;

        // ===== SAVE RECEIPT =====
        String filename = "sales_" + date + ".txt";

        try (PrintWriter pw = new PrintWriter(new FileWriter(filename, true))) {
            pw.println("================================");
            pw.println("Date      : " + date);
            pw.println("Time      : " + time);
            pw.println("Employee  : " + employeeID + " (" + employeeName + ")");
            pw.println("Outlet    : " + outletCode);
            pw.println("Items Purchased:");
            pw.println(model + " x" + qty + " @ RM" + price + " = RM" + total);
            pw.println("Subtotal  : RM" + total);
            pw.println("================================\n");
        } catch (Exception e) {
            System.out.println("❌ Failed to generate receipt: " + e.getMessage());
        }

        System.out.println("✅ Sale recorded successfully: " + model + " x" + qty + " from " + outletCode);
    }

    // ================== Console version (interactive) ==================
    public static void recordNewSale(
            ArrayList<Stock> stockList,
            String employeeID,
            String employeeName
    ) {
        Scanner sc = new Scanner(System.in);
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();

        System.out.println("\n=== Record New Sale ===");
        System.out.println("Date: " + date);
        System.out.println("Time: " + time);

        System.out.print("Customer Name: ");
        String customer = sc.nextLine();

        System.out.print("Outlet Code (C60–C69 / HQ): ");
        String outletCode = sc.nextLine();

        double subtotal = 0;
        StringBuilder itemsPurchased = new StringBuilder();

        while (true) {
            System.out.print("Enter Model: ");
            String model = sc.nextLine();

            Stock selected = null;
            for (Stock s : stockList) {
                if (s.getModel().equalsIgnoreCase(model)) {
                    selected = s;
                    break;
                }
            }

            if (selected == null) {
                System.out.println("❌ Model not found.");
                continue;
            }

            int available = selected.getQuantityByOutlet(outletCode);
            System.out.println("Available stock at " + outletCode + ": " + available);

            System.out.print("Enter Quantity: ");
            int qty = sc.nextInt();
            sc.nextLine();

            if (qty <= 0 || qty > available) {
                System.out.println("❌ Invalid quantity.");
                continue;
            }

            double price = selected.getPrice();
            double total = qty * price;

            // Update stock
            selected.removeStock(outletCode, qty);

            subtotal += total;
            itemsPurchased.append(model + " x" + qty + " @ RM" + price + " = RM" + total + "\n");

            System.out.print("Are there more items purchased? (Y/N): ");
            String more = sc.nextLine();
            if (more.equalsIgnoreCase("N")) break;
        }

        System.out.print("Enter transaction method: ");
        String method = sc.nextLine();

        // Save receipt
        String filename = "sales_" + date + ".txt";

        try (PrintWriter pw = new PrintWriter(new FileWriter(filename, true))) {
            pw.println("================================");
            pw.println("Date      : " + date);
            pw.println("Time      : " + time);
            pw.println("Employee  : " + employeeID + " (" + employeeName + ")");
            pw.println("Outlet    : " + outletCode);
            pw.println("Items Purchased:");
            pw.print(itemsPurchased);
            pw.println("Payment   : " + method);
            pw.println("Subtotal  : RM" + subtotal);
            pw.println("================================\n");
        } catch (Exception e) {
            System.out.println("❌ Failed to generate receipt: " + e.getMessage());
        }

        System.out.println("\n✅ Transaction successful. Stock updated for outlet " + outletCode);
        System.out.println("Receipt generated: " + filename);
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
                        } catch (DateTimeParseException e) { currentDate = null; } // Skip if date invalid
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
}