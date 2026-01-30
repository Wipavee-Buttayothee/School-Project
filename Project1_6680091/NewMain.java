package Project1_6680091;

import java.io.*;
import java.util.*;

abstract class Display {

    public abstract void display();

    void displaySum(ArrayList<?> list) {
    }
;

}

class InvalidInputException extends Exception {

    public InvalidInputException(String message) {
        super(message);
    }
}

class Myfile {

    String filepath;

    public String createFile(String fileName) {
        String path = "src/main/Java/Project1_6680091/";
        Scanner input = new Scanner(System.in);

        boolean opensuccess = false;
        while (!opensuccess) {
            try {
                File file = new File(path + fileName);

                if (!file.exists()) {
                    throw new FileNotFoundException();
                }

                opensuccess = true;
                System.out.println("Read from " + path + fileName);
            } catch (FileNotFoundException e) {
                System.err.println(e + ": " + path + fileName + " (The system cannot find the file specified)");
                System.err.println("Enter a new file name: ");
                fileName = input.nextLine();
            }
        }
        filepath = path + fileName;
        return filepath;

    }
}

class Product extends Display implements Comparable<Product> {

    private String productCode;
    private String productName;
    private double unitPrice;
    private int Totalsalesunit = 0;
    private double Totalsalescash;
    private ArrayList<String[]> cusList = new ArrayList<>();

    Product() {
    }

    ;

    Product(String p_code, String p_name, double price) {
        this.productCode = p_code;
        this.productName = p_name;
        this.unitPrice = price;
    }

    public void ReadProduct(String fileName, ArrayList<Product> productList) {
        File info = new File(fileName);
        try (Scanner scan = new Scanner(info)) {

            if (scan.hasNextLine()) {
                scan.nextLine();
            }
            while (scan.hasNext()) {
                String line = scan.nextLine();
                String[] cols = line.split(",");
                try {
                    String code = cols[0].trim();
                    String name = cols[1].trim();
                    double unitPrice = Double.parseDouble(cols[2].trim());
                    if (unitPrice < 0) {
                        throw new InvalidInputException("For unitPrice : \"" + unitPrice + "\"");
                    }
                    if (cols.length < 3) {
                        throw new ArrayIndexOutOfBoundsException();
                    }
                    productList.add(new Product(code, name, unitPrice));

                } catch (InvalidInputException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.err.println(e);
                    System.err.println(line + "   --> skip this line\n");
                }
            }

        } catch (FileNotFoundException e) {
            System.err.println(e);
        }
        for (Product p : productList) {
            p.display();
        }
    }

    public void getWinner() {
        try {
            Random rand = new Random();
            int winner = rand.nextInt(cusList.size());
            System.out.printf("lucky draw winner = %s (order %2s)\n", cusList.get(winner)[0], cusList.get(winner)[1]);
        } catch (IllegalArgumentException e) {
        }

    }

    @Override
    public void display() {
        System.out.printf("%-15s (%2s)    unit price = %,6.0f\n", productName, productCode, unitPrice);
    }

    public String getProductCode() {
        return this.productCode;
    }

    public double get_unitPrice() {
        return this.unitPrice;
    }

    public String get_productName() {
        return this.productName;
    }

    public void set_total(int total, String name, int id) {
        Totalsalesunit += total;
        String[] cus = {name, Integer.toString(id)};
        cusList.add(cus);
    }

    public double get_totalcash() {
        Totalsalescash = Totalsalesunit * unitPrice;
        return Totalsalescash;
    }

    @Override
    public int compareTo(Product other) {
        if (this.Totalsalesunit != other.Totalsalesunit) {
            return Integer.compare(other.Totalsalesunit, this.Totalsalesunit);
        } else {
            return this.get_productName().compareToIgnoreCase(other.get_productName());
        }
    }

    @Override
    public void displaySum(ArrayList<?> list) {
        ArrayList<Product> productList = (ArrayList<Product>) list;
        Collections.sort(productList);
        System.out.println("\n=== Product Summary ===");
        for (Product p : productList) {
            System.out.printf("%-16s total sales =%4d units   = %,13.2f THB   ", p.get_productName(), p.Totalsalesunit, p.get_totalcash());
            p.getWinner();
        }
    }
}

class Customer extends Display implements Comparable<Customer> {

    protected int point = 0;
    protected String name;
    protected int history = 0;

    Customer() {
    }

    ;
    
    Customer(String name) {
        this.name = name;

    }

    public double GetDiscount(double sub1) {
        double discount = 0;
        if (history == 0) {
            discount = 200;

            history++;
        } else {
            if (point >= 100) {
                discount = sub1 * 0.05;
            } else {
                discount = 0;
            }
        }
        return discount;
    }

    public void GetPtsAtDc() {
        point -= 100;
    }

    @Override
    public void display() {
        System.out.printf("%s \n", name);
    }

    public String Getname() {
        return name;
    }

    public int Getpts() {
        return point;
    }

    public void Setpoint(int Pointperorder) {
        point += Pointperorder;
    }

    @Override
    public int compareTo(Customer other) {
        if (this.Getpts() != other.Getpts()) {
            return Integer.compare(other.Getpts(), this.Getpts());
        } else {
            return this.Getname().compareToIgnoreCase(other.Getname());
        }
    }

    @Override
    public void displaySum(ArrayList<?> list) {
        ArrayList<Customer> customerList = (ArrayList<Customer>) list;
        Collections.sort(customerList);
        System.out.println("\n=== Customer Summary ===");
        for (Customer c : customerList) {
            if (c.Getpts() >= 0) {
                System.out.printf("%-8sremaining points = %,6d\n", c.Getname(), c.Getpts());
            }
        }
    }
}

class Order extends Display {

    protected String name;
    protected String code;
    protected int units;
    protected int ip;
    protected int id;
    protected double sub1;
    protected double sub2;
    protected double discount;
    protected int pointsPerOrder;
    protected double totalpayment;
    protected String productname;

    Order() {
    }

    ;

    Order(int id, String name, String code, int units, int installment) {
        this.name = name;
        this.code = code;
        this.units = units;
        this.ip = installment;
        this.id = id;
    }

    public void ReadOrder(String fileName, ArrayList<Order> Myorder, ArrayList<Product> productList, ArrayList<Installment> Installment_plan) {
        File info = new File(fileName);

        try (Scanner scan = new Scanner(info)) {

            if (scan.hasNextLine()) {
                scan.nextLine();
            }
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                String[] cols = line.split(",");
                try {
                    id = Integer.parseInt(cols[0].trim());

                    name = cols[1].trim();
                    code = cols[2].trim();
                    Checkcode(code, productList);
                    units = Integer.parseInt(cols[3].trim());
                    if (units < 0) {
                        throw new InvalidInputException("For units : \"" + units + "\"");
                    }
                    ip = Integer.parseInt(cols[4].trim());
                    Checkip(ip, Installment_plan);

                    if (cols.length < 5) {
                        throw new ArrayIndexOutOfBoundsException();
                    }
                    Order list = new Order(id, name, code, units, ip);
                    Myorder.add(list);
                } catch (InvalidInputException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.err.println(e);
                    System.err.println(line + "   --> skip this line\n");
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println(e);
        }
    }

    public void Getproductname(ArrayList<Product> productList) {
        for (int i = 0; i < productList.size(); i++) {
            if (code.equalsIgnoreCase(productList.get(i).getProductCode())) {
                productname = productList.get(i).get_productName();
            }
        }
    }

    public double calculateSub1(ArrayList<Product> productList) {
        double unitprice = 0;
        for (int i = 0; i < productList.size(); i++) {
            if (code.equalsIgnoreCase(productList.get(i).getProductCode())) {
                unitprice = productList.get(i).get_unitPrice();
                productname = productList.get(i).get_productName();
                productList.get(i).set_total(units, name, id);
            }
        }
        sub1 = unitprice * units;
        pointsPerOrder = (int) (sub1 / 500);

        return sub1;
    }

    public int getPoints() {
        return pointsPerOrder;
    }

    public double calculateSub2() {
        sub2 = sub1 - discount;
        return sub2;
    }

    public void Checkcode(String code, ArrayList<Product> productList) throws InvalidInputException {
        int flag = 0;
        for (int i = 0; i < productList.size(); i++) {

            if (code.equalsIgnoreCase(productList.get(i).getProductCode())) {
                flag = 1;
            }
        }
        if (flag == 0) {
            throw new InvalidInputException(": For product: \"" + code + "\"");
        }
    }

    public void Checkip(int ip, ArrayList<Installment> Installment_plan) throws InvalidInputException {
        int flag = 0;
        for (int i = 0; i < Installment_plan.size(); i++) {

            if (ip == Installment_plan.get(i).Getmonth()) {
                flag++;
            }
        }
        if (flag == 0) {
            throw new InvalidInputException(": For installment plan: \"" + ip + "\"");
        }
    }

    @Override
    public void display() {
        System.out.printf("Order %2d >> %-6s  %-14s x %2d   %2d-month installments \n", id, name, productname, units, ip);

    }

    public void displayOrderDetails(Order orderlist, Customer CurrentCustomer, Installment selectedPlan) {

        System.out.printf("%2d.%-6s(%,6d pts)   %-8s = %-14s x %-4d %-12s   = %,12.2f  (+%,6d pts next order)\n", orderlist.id, orderlist.name, CurrentCustomer.Getpts(), "order", orderlist.productname, orderlist.units, "sub-total(1)", orderlist.sub1, orderlist.pointsPerOrder);
        System.out.printf("%32s = %,12.2f  %20s   = %,12.2f", "discount", orderlist.discount, "sub-total(2)", orderlist.sub2);

        if (CurrentCustomer.Getpts() >= 100) {
            System.out.printf("  (-   100 pts)\n");
            CurrentCustomer.GetPtsAtDc();
        } else {
            System.out.println();
        }

        if (orderlist.ip == 0) {
            System.out.printf("%36s\n", "full payment");
            System.out.printf("%29s    = %,12.2f\n\n", "total", orderlist.sub2);
        } else {
            if (orderlist.ip >= 10) {
                System.out.printf("%19s     %d%s%23s = %,12.2f\n", " ", orderlist.ip, "-month installments   ", "total interest", selectedPlan.total_interest);
            } else {
                System.out.printf("%19s     %d%s%24s = %,12.2f\n", " ", orderlist.ip, "-month installments   ", "total interest", selectedPlan.total_interest);
            }
            System.out.printf("%29s    = %,12.2f %22s  = %,12.2f\n\n", "total", selectedPlan.total, "monthly total", selectedPlan.monthly_payment);
        }
    }
}

class Installment extends Display {

    protected int months;
    protected double monthly_interest;
    protected double total, monthly_payment, total_interest;

    Installment() {
    }

    ;

    Installment(int months, double monthly_interest) {
        this.months = months;
        this.monthly_interest = monthly_interest;
    }

    public void ReadInstallment(String fileName, ArrayList<Installment> Installment_plan) {
        File info = new File(fileName);
        try (Scanner scan = new Scanner(info)) {

            if (scan.hasNextLine()) {
                scan.nextLine();
            }
            while (scan.hasNext()) {

                String line = scan.nextLine();
                String[] cols = line.split(",");
                try {
                    months = Integer.parseInt(cols[0].trim());
                    if (months < 0) {
                        throw new InvalidInputException("For month : \"" + months + "\"");
                    }
                    monthly_interest = Double.parseDouble(cols[1].trim());
                    if (monthly_interest < 0) {
                        throw new InvalidInputException("For monthly interest : \"" + monthly_interest + "\"");
                    }
                    if (cols.length < 2) {
                        throw new ArrayIndexOutOfBoundsException();
                    }
                    Installment list = new Installment(months, monthly_interest);
                    Installment_plan.add(list);
                } catch (InvalidInputException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.err.println(e);
                    System.err.println(line + "   --> skip this line\n");
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println(e);
        }

        for (Installment i : Installment_plan) {
            i.display();
        }
    }

    @Override
    public void display() {

        System.out.printf("%2d-month plan    monthly interest = %.2f%s\n", months, monthly_interest, "%");
    }

    public void calinterest(double subtotal2, int m) {
        total = (double) subtotal2 + (subtotal2 * ((monthly_interest * m) / 100));
        monthly_payment = (double) total / m;
        total_interest = total - subtotal2;

    }

    public int Getmonth() {
        return months;
    }
}

public class NewMain {

    public static void main(String[] args) {

        Myfile confile = new Myfile();

        String info = confile.createFile("product.txt");
        ArrayList<Product> productList = new ArrayList<>();
        Product p = new Product();
        p.ReadProduct(info, productList);
        System.out.println();

        info = confile.createFile("installment.txt");
        ArrayList<Installment> Installment_plan = new ArrayList<>();
        Installment in = new Installment();
        in.ReadInstallment(info, Installment_plan);
        System.out.println();

        info = confile.createFile("order.txt");
        ArrayList<Order> Myorder = new ArrayList<>();
        ArrayList<Customer> Mycustomer = new ArrayList<>();
        Order o = new Order();
        Customer c = new Customer();
        o.ReadOrder(info, Myorder, productList, Installment_plan);
        for (Order d : Myorder) {
            d.Getproductname(productList);
            d.display();
        }
        System.out.println("\n=== Order processing ===");
        for (Order orderlist : Myorder) {
            orderlist.calculateSub1(productList);
            Customer CurrentCustomer = null;
            for (Customer cus : Mycustomer) {
                if (cus.Getname().equalsIgnoreCase(orderlist.name)) {
                    CurrentCustomer = cus;
                    break;
                }
            }
            if (CurrentCustomer == null) {
                CurrentCustomer = new Customer(orderlist.name);
                Mycustomer.add(CurrentCustomer);
            }
            orderlist.discount = CurrentCustomer.GetDiscount(orderlist.sub1);
            orderlist.calculateSub2();
            Installment selectedPlan = null;
            for (Installment find : Installment_plan) {
                if (orderlist.ip == find.months) {
                    selectedPlan = find;
                    break;
                }
            }

            if (selectedPlan != null) {
                selectedPlan.calinterest(orderlist.sub2, orderlist.ip);
            }
            o.displayOrderDetails(orderlist, CurrentCustomer, selectedPlan);
            CurrentCustomer.Setpoint(orderlist.pointsPerOrder);
        }

        p.displaySum(productList);
        c.displaySum(Mycustomer);
    }
}
