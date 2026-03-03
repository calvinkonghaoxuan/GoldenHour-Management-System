package stockjava;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Scanner;

public class Login {

    // ===== EMPLOYEE DATA =====
    //static ArrayList<String> employeeID = new ArrayList<>();
    static ArrayList<String> employeeName = new ArrayList<>();
    static ArrayList<String> role = new ArrayList<>();
    static ArrayList<String> password = new ArrayList<>();

    // ===== OUTLET DATA =====
    static ArrayList<String> outletCode = new ArrayList<>();
    static ArrayList<String> outletName = new ArrayList<>();


    // ===== ATTENDANCE =====
    //static boolean[] isClockedIn;
    //static String[] clockInTime;
    //static String[] clockOutTime;
    //getters
    static int selectedOutlet = -1;
    public static String getEmployeeID(int index) {
        return employeeID.get(index);
    }

    public static String getEmployeeName(int index) {
        return employeeName.get(index);
    }

    public static String getRole(int index) {
        return role.get(index);
    }
    public static int getCurrentUserIndex() {
        for (int i = 0; i < employeeID.size(); i++) {
            if (employeeID.get(i).equalsIgnoreCase(currentEmployeeID)) return i;
        }
        return -1;
    }

    // Get all outlet codes
    public static String[] getAllOutletCodes() {
        return outletCode.toArray(new String[0]);
    }

    // Set selected outlet by code
    public static void setSelectedOutletByCode(String code) {
        for (int i = 0; i < outletCode.size(); i++) {
            if (outletCode.get(i).equalsIgnoreCase(code)) {
                selectedOutlet = i;
                return;
            }
        }
    }

    private static String currentEmployeeID;
    private static String currentEmployeeName;
    private static String currentRole;

    public static void setCurrentUser(String id, String name, String role) {
        currentEmployeeID = id;
        currentEmployeeName = name;
        currentRole = role;
    }

    public static String getCurrentUserID() { return currentEmployeeID; }
    public static String getCurrentUserName() { return currentEmployeeName; }
    public static String getCurrentUserRole() { return currentRole; }
    //gui getters
    public static int getEmployeeIndex(String empID) {
        return employeeID.indexOf(empID);
    }


    // ================= MAIN LOGIN SYSTEM =================
    public static void login() {

        loadEmployees("employee.txt");
        loadOutlets("outlet.txt");

        isClockedIn = new boolean[employeeID.size()];
        clockInTime = new String[employeeID.size()];
        clockOutTime = new String[employeeID.size()];

        Scanner sc = new Scanner(System.in);

        selectOutlet(sc);
        loginLoop(sc);

        sc.close();
    }

    // ================= LOAD EMPLOYEES =================
    public static void loadEmployees(String fileName) {
        employeeID.clear();
        employeeName.clear();
        role.clear();
        password.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length == 4) {
                    employeeID.add(p[0].trim());
                    employeeName.add(p[1].trim());
                    role.add(p[2].trim());
                    password.add(p[3].trim());
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Cannot read employee.txt");
            e.printStackTrace();
        }
    }

    // ================= LOAD OUTLETS =================
    public static void loadOutlets(String fileName) {
        outletCode.clear();
        outletName.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length == 2) {
                    outletCode.add(p[0].trim());
                    outletName.add(p[1].trim());
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Cannot read outlet.txt");
            e.printStackTrace();
        }
    }

    // ================= SELECT OUTLET =================
    public static void selectOutlet(Scanner sc) {
        while (selectedOutlet == -1) {
            System.out.println("\n=== GoldenHour Management System ===");
            System.out.print("Enter Outlet Code: ");
            String input = sc.nextLine().trim();

            for (int i = 0; i < outletCode.size(); i++) {
                if (outletCode.get(i).equalsIgnoreCase(input)) {
                    selectedOutlet = i;
                    break;
                }
            }

            if (selectedOutlet == -1) {
                System.out.println("❌ Invalid outlet code.");
            }
        }

        System.out.println("✅ Outlet Selected: " +
                outletCode.get(selectedOutlet) + " - " +
                outletName.get(selectedOutlet));
    }


    // ================= LOGIN LOOP =================
    public static void loginLoop(Scanner sc) {

        boolean systemOn = true;

        while (systemOn) {

            int userIndex = -1;

            while (userIndex == -1) {
                System.out.print("\nUser ID: ");
                String id = sc.nextLine().trim();

                System.out.print("Password: ");
                String pass = sc.nextLine().trim();

                userIndex = authenticate(id, pass);

                if (userIndex == -1) {
                    System.out.println("❌ Invalid ID or Password");
                }
            }

            System.out.println("\n✅ Login Successful");
            System.out.println("Welcome " + employeeName.get(userIndex)
                    + " (" + role.get(userIndex) + ")");

            control.setCurrentUser(
                    employeeID.get(userIndex),
                    employeeName.get(userIndex),
                    role.get(userIndex)
            );

            boolean loggedIn = true;

            while (loggedIn) {
                System.out.println("\n1. Control");
                System.out.println("2. Clock In");
                System.out.println("3. Clock Out");
                System.out.println("0. Logout");
                System.out.print("Choice: ");

                String choice = sc.nextLine();

                // ✅ CHANGED: Traditional Switch Syntax (Compatible with older Java)
                switch (choice) {
                    case "1":
                        control.control();
                        break;
                    case "2":
                        clockIn(userIndex);
                        break;
                    case "3":
                        clockOut(userIndex);
                        break;
                    case "0":
                        loggedIn = false;
                        break;
                    default:
                        System.out.println("❌ Invalid option");
                        break;
                }
            }

            System.out.print("Exit system? (yes/no): ");
            if (sc.nextLine().equalsIgnoreCase("yes")) {
                systemOn = false;
            }
        }

        System.out.println("🔒 System Closed");
    }

    // ================= AUTHENTICATE =================
    public static int authenticate(String id, String pass) {
        for (int i = 0; i < employeeID.size(); i++) {
            if (employeeID.get(i).equalsIgnoreCase(id)
                    && password.get(i).equals(pass)) {
                return i;
            }
        }
        return -1;
    }

    // ================= CLOCK IN =================
    public static void clockIn(int i) {
        if (isClockedIn[i]) {
            System.out.println("⚠ Already clocked in");
            return;
        }

        isClockedIn[i] = true;
        clockInTime[i] = LocalTime.now().toString();

        recordAttendance(i, "CLOCK_IN", clockInTime[i]);

        System.out.println("\n✅ CLOCK IN");
        System.out.println("Employee: " + employeeName.get(i));
        System.out.println("Outlet  : " + outletName.get(selectedOutlet));
        System.out.println("Date    : " + LocalDate.now());
        System.out.println("Time    : " + clockInTime[i]);
    }


    // ================= CLOCK OUT =================
    public static void clockOut(int i) {
        if (!isClockedIn[i]) {
            System.out.println("⚠ Not clocked in yet");
            return;
        }

        isClockedIn[i] = false;
        clockOutTime[i] = LocalTime.now().toString();

        recordAttendance(i, "CLOCK_OUT", clockOutTime[i]);

        System.out.println("\n✅ CLOCK OUT");
        System.out.println("Employee: " + employeeName.get(i));
        System.out.println("Outlet  : " + outletName.get(selectedOutlet));
        System.out.println("Date    : " + LocalDate.now());
        System.out.println("Time    : " + clockOutTime[i]);
    }


    // ================= RECORD ATTENDANCE =================
    public static void recordAttendance(int userIndex, String action, String time) {

        String date = LocalDate.now().toString();

        try (PrintWriter pw = new PrintWriter(new FileWriter("attendance.txt", true))) {
            pw.println(
                    date + "," +
                            time + "," +
                            employeeID.get(userIndex) + "," +
                            employeeName.get(userIndex) + "," +
                            role.get(userIndex) + "," +
                            outletCode.get(selectedOutlet) + "," +
                            outletName.get(selectedOutlet) + "," +
                            action
            );
        } catch (IOException e) {
            System.out.println("❌ Failed to record attendance.");
            e.printStackTrace();
        }
    }
    public static String yourOutletCode() {
        return outletCode.get(selectedOutlet);
    }

    public static String yourOutletName(){
        return outletName.get(selectedOutlet);
    }
    // gui getters
    public static String getClockInTime(String empID) {
        if (empID == null) return "Unknown Employee";
        int idx = employeeID.indexOf(empID);
        return (idx != -1 && clockInTime[idx] != null) ? clockInTime[idx] : "Not clocked in";
    }

    public static String getClockOutTime(String empID) {
        if (empID == null) return "Unknown Employee";
        int idx = employeeID.indexOf(empID);
        return (idx != -1 && clockOutTime[idx] != null) ? clockOutTime[idx] : "Not clocked out";
    }

    // ===== Inside Login.java =====

    // Clock in for GUI
    public static String clockInGUI() {
        String empID = control.getCurrentEmployeeID();
        int index = getEmployeeIndex(empID);

        if (index == -1) return "❌ Employee not found";

        String time = LocalTime.now().toString();
        clockInTime[index] = time;

        recordAttendance(index, "IN", time);

        return time;
    }




    // Clock out for GUI
    public static String clockOutGUI() {
        String empID = control.getCurrentEmployeeID();
        int index = getEmployeeIndex(empID);

        if (index == -1) return "❌ Employee not found";

        String time = LocalTime.now().toString();
        clockOutTime[index] = time;

        recordAttendance(index, "OUT", time);

        return time;
    }

    public static ArrayList<String> employeeID = new ArrayList<>();
    public static boolean[] isClockedIn;
    public static String[] clockInTime;
    public static String[] clockOutTime;

    // Call this **once** after loading employeeID
    public static void initAttendanceArrays() {
        int n = employeeID.size();
        isClockedIn = new boolean[n];
        clockInTime = new String[n];
        clockOutTime = new String[n];
    }

    public static void initClockArrays() {
        int n = employeeID.size();
        isClockedIn = new boolean[n];
        clockInTime = new String[n];
        clockOutTime = new String[n];
    }
    public static void initForGUI() {
        loadEmployees("employee.txt");
        loadOutlets("outlet.txt");

        int size = employeeID.size();

        if (isClockedIn == null || isClockedIn.length != size) {
            isClockedIn = new boolean[size];
            clockInTime = new String[size];
            clockOutTime = new String[size];
        }
    }

}