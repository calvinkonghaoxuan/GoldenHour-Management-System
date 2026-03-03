package stockjava;

import java.time.LocalDate;
import java.time.LocalTime;

public class Stock {

    private LocalDate date;
    private LocalTime time;
    private String model;
    private double price;

    // ===== MASTER QUANTITY (TOTAL) =====
    private int quantity;

    // ===== STOCK COUNT =====
    private String countedOutlet;
    private int countedQuantity;

    // ===== OUTLET STOCK =====
    private int klcc;
    private int mv;
    private int sv;
    private int icm;
    private int ll;
    private int klem;
    private int ns;
    private int pkl;
    private int u1;
    private int mt;
    private int hq;

    // ================= CONSTRUCTOR =================
    public Stock(LocalDate date, LocalTime time, String model, double price,
                 int klcc, int mv, int sv, int icm, int ll,
                 int klem, int ns, int pkl, int u1, int mt, int hq) {

        this.date = date;
        this.time = time;
        this.model = model;
        this.price = price;

        this.klcc = klcc;
        this.mv = mv;
        this.sv = sv;
        this.icm = icm;
        this.ll = ll;
        this.klem = klem;
        this.ns = ns;
        this.pkl = pkl;
        this.u1 = u1;
        this.mt = mt;
        this.hq = hq;

        // ✅ AUTO-CALCULATE TOTAL
        this.quantity = klcc + mv + sv + icm + ll + klem + ns + pkl + u1 + mt + hq;

        this.countedQuantity = 0;
    }

    // ===== Setters =====
    public void setPrice(double price) { this.price = price; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public void setKLCC(int qty) { this.klcc = qty; }
    public void setMV(int qty) { this.mv = qty; }
    public void setSV(int qty) { this.sv = qty; }
    public void setICM(int qty) { this.icm = qty; }
    public void setLL(int qty) { this.ll = qty; }
    public void setKLEM(int qty) { this.klem = qty; }
    public void setNS(int qty) { this.ns = qty; }
    public void setPKL(int qty) { this.pkl = qty; }
    public void setU1(int qty) { this.u1 = qty; }
    public void setMT(int qty) { this.mt = qty; }
    public void setHQ(int qty) { this.hq = qty; }

    public void setDate(LocalDate date) { this.date = date; }
    public void setTime(LocalTime time) { this.time = time; }


    // ================= GETTERS =================
    public LocalDate getDate() { return date; }
    public LocalTime getTime() { return time; }
    public String getModel() { return model; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }

    public String getCountedOutlet() { return countedOutlet; }
    public int getCountedQuantity() { return countedQuantity; }

    public int getKLCC() { return klcc; }
    public int getMV() { return mv; }
    public int getSV() { return sv; }
    public int getICM() { return icm; }
    public int getLL() { return ll; }
    public int getKLEM() { return klem; }
    public int getNS() { return ns; }
    public int getPKL() { return pkl; }
    public int getU1() { return u1; }
    public int getMT() { return mt; }
    public int getHQ() { return hq; }

    // ================= BASIC UPDATE =================
    public void updatePrice(double change) {
        price += change;
    }

    public void updateDate(LocalDate date) {
        this.date = date;
    }

    public void updateTime(LocalTime time) {
        this.time = time;
    }

    public void updateQuantity(int delta) {
        this.quantity += delta;
    }


    // ================= STOCK COUNT =================
    public void setCountedQuantity(int countedQuantity, String outletCode) {
        this.countedQuantity = countedQuantity;
        this.countedOutlet = outletCode;
    }

    public boolean isTallyCorrect() {
        return countedQuantity == quantity;
    }

    public int getDifference() {
        return Math.abs(countedQuantity - quantity);
    }

    public boolean isTallyCorrectByOutlet(String outletCode) {
        return countedQuantity == getQuantityByOutlet(outletCode);
    }

    public int getDifferenceByOutlet(String outletCode) {
        return Math.abs(countedQuantity - getQuantityByOutlet(outletCode));
    }

    // ================= OUTLET LOGIC (Fixed for Lower Java Version) =================
    public int getQuantityByOutlet(String outletCode) {
        String code = outletCode.toUpperCase();
        
        switch (code) {
            case "C60": return klcc;
            case "C61": return mv;
            case "C62": return sv;
            case "C63": return icm;
            case "C64": return ll;
            case "C65": return klem;
            case "C66": return ns;
            case "C67": return pkl;
            case "C68": return u1;
            case "C69": return mt;
            case "HQ":  return hq;
            default:    return 0;
        }
    }

    // ================= STOCK IN (Fixed for Lower Java Version) =================
    public void plusStock(String outletCode, int qty) {
        if (qty <= 0) return;

        switch (outletCode.toUpperCase()) {
            case "C60": klcc += qty; break;
            case "C61": mv += qty; break;
            case "C62": sv += qty; break;
            case "C63": icm += qty; break;
            case "C64": ll += qty; break;
            case "C65": klem += qty; break;
            case "C66": ns += qty; break;
            case "C67": pkl += qty; break;
            case "C68": u1 += qty; break;
            case "C69": mt += qty; break;
            case "HQ":  hq += qty; break;
            default:
                System.out.println("Invalid outlet code");
                return; // Ends method here so total quantity isn't updated
        }
        quantity += qty; // ✅ SYNC TOTAL
    }

    public void minusStock(String outletCode, int qty) {
        if (qty <= 0) return;

        switch (outletCode.toUpperCase()) {
            case "C60": klcc -= qty; break;
            case "C61": mv -= qty; break;
            case "C62": sv -= qty; break;
            case "C63": icm -= qty; break;
            case "C64": ll -= qty; break;
            case "C65": klem -= qty; break;
            case "C66": ns -= qty; break;
            case "C67": pkl -= qty; break;
            case "C68": u1 -= qty; break;
            case "C69": mt -= qty; break;
            case "HQ":  hq -= qty; break;
            default:
                System.out.println("Invalid outlet code");
                return;
        }
        quantity -= qty; // total stock
    }

    //================= STOCK OUT =================
    public boolean removeStock(String outletCode, int qty) {
       if (qty <= 0 || getQuantityByOutlet(outletCode) < qty) {
           return false;
       }
       minusStock(outletCode, qty);
       return true;
    }

    // ================= TRANSFER =================
    public boolean transferStock(String fromOutlet, String toOutlet, int qty) {
        if (qty <= 0 || getQuantityByOutlet(fromOutlet) < qty) {
            return false;
        }

        removeStock(fromOutlet, qty);
        plusStock(toOutlet, qty);
        return true;
    }

    // ================= DISPLAY =================
    @Override
    public String toString() {
        return date + " , " + time +
               " , " + model +
               " , RM" + price +
               " , Qty: " + quantity +
               " , KLCC: " + klcc +
               " , MIDVALLEY: " + mv +
               " , SUNWAYVELOCITY: " + sv +
               " , IOICM: " + icm +
               " , LALAPORT: " + ll +
               " , KLEASTM: " + klem +
               " , NSENTRAL: " + ns +
               " , PAVILLIONKL: " + pkl +
               " , U1: " + u1 +
               " , MYTOWN: " + mt +
               " , HQ: " + hq;
    }
}