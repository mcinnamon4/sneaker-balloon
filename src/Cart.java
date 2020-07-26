import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;


public class Cart {
    private static Connection c;
    private static Stock stock;
    private static ArrayList<String> treatTypes;
    private static Scanner scanner;
    //change date here - epoch time
    private static final Date DATE = new Date(1633077830L * 1000);

    public static void main(String[] args) {
        connect();
        createIfNecessary();
        scanner = new Scanner(System.in);
        if(args.length < 2){
            stock = new Stock("input/products-data.json", "input/sale_rules.json");
        } else{
            stock = new Stock(args[0], args[1]);
        }
        treatTypes= stock.getTreatTypes();
        System.out.println("Welcome to the CAI Bakery! What is your name?");
        String userName = scanner.nextLine();
        if(getRecord(userName).size() == 0) {
            service(userName);
        } else {
            System.out.println("\nWelcome back. Your cart contains:");
            for(int x = 0; x < 4; x++){
                System.out.print(getRecord(userName).get(x) + " " + treatTypes.get(x) + "s, ");
            }
            System.out.print("\n");
            service(userName);
        }
    }

    public static void service(String name) {
        double totalCost = 0;
        while (true) {
            System.out.println("\n====Menu====");
            for (String treat : treatTypes) {
                System.out.print(treat + ": $" + stock.getTreatCost(treat)+ ", ");
            }
            System.out.println("\n============================================================================");
            System.out.println("How many treats would you like to add to your cart?" +
                    " Please enter a list of ints separated by commas in order of treat (no spaces).");
            System.out.println("---> Type the name of a treat to learn more.");
            System.out.println("---> You can clear your cart at any time by typing \"clear\".");
            String input = scanner.nextLine();
            while(!validInputForAmounts(input)){
                System.out.println("Please enter a valid input.");
                input = scanner.nextLine();
            }
            if(input.equals("clear")){
                clear(name);
                totalCost=0;
            }
            else if(treatTypes.contains(input)){
                learnMore(input);
            } else {
                try {
                    ArrayList<Integer> amountList = (ArrayList<Integer>) Arrays.asList(input.split(","))
                            .stream()
                            .map(a -> Integer.parseInt(a.replaceAll("\\s+", "")))
                            .collect(Collectors.toList());
                    ArrayList<Integer> updatedAmounts = setRecord(name, amountList);
                    System.out.println("Your cart contains: \n");
                    for (int x = 0; x < 4; x++) {
                        System.out.print(updatedAmounts.get(x) + " " + treatTypes.get(x) + "s, ");
                        totalCost += stock.calculatePriceForTreat(treatTypes.get(x), updatedAmounts.get(x), DATE);
                    }
                    System.out.println("\nSubtotal: $" + totalCost);
                    totalCost = 0;
                } catch (Exception e){
                    System.err.println("Malformed request. Please enter a list of ints separated by commas.");
                }
            }
        }
    }

    public static void connect() {
        c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:bakery.db");
            System.out.println("Opened database successfully.");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            //System.exit(0);
        }
    }

    public static void createIfNecessary() {
        String sql = "CREATE TABLE IF NOT EXISTS cart2 (\n"
                + " name text PRIMARY KEY NOT NULL,\n"
                + " item1 integer,\n"
                + " item2 integer,\n"
                + " item3 integer,\n"
                + " item4 integer\n"
                + ");";

        try(Statement stmt = c.createStatement()){
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public static ArrayList<Integer> getRecord(String name) {
        String sql = "SELECT * FROM cart2 WHERE name LIKE '"+name+"';";
        ArrayList<Integer> result = new ArrayList<Integer>();
        try{
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                result.add(rs.getInt("item1"));
                result.add(rs.getInt("item2"));
                result.add(rs.getInt("item3"));
                result.add(rs.getInt("item4"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    //updates sql database and also returns the updated amounts
    public static ArrayList<Integer> setRecord(String name, ArrayList<Integer> amountList) {
        ArrayList<Integer> original_amount = getRecord(name);
        String sql = "REPLACE INTO cart2(name,item1,item2,item3,item4) VALUES('"+ name+"',?,?,?,?)";
        ArrayList<Integer> totalAmountList = new ArrayList<Integer>();
        try( PreparedStatement pstmt = c.prepareStatement(sql) ){
            for (int x = 0; x < amountList.size(); x++){
                int total_amount = amountList.get(x);
                if (original_amount.size()>0){
                    total_amount += original_amount.get(x);
                }
                totalAmountList.add(total_amount);
                pstmt.setInt(x+1, total_amount);
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return totalAmountList;
    }

    //clear cart
    public static void clear(String name) {
        String sql = "REPLACE INTO cart2(name,item1,item2,item3,item4) VALUES('"+ name+"',0,0,0,0)";
        try{
            Statement stmt = c.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Subtotal: $0.0");
    }

    //get more info
    public static void learnMore(String treat) {
        if(treatTypes.contains(treat)){
            Treat t = stock.getTreat(treat);
            System.out.println(t.getName());
            System.out.println("Standard Price: " + t.getPrice());
            System.out.println("Image: " + t.getImageURL());
            if(t.getBulkPricing().isPresent()){
                System.out.println("Bulk Pricing Info: " + t.getBulkPricing().get().getAmount() + " for $"
                        + t.getBulkPricing().get().getTotalPrice());
            }
            if(stock.getRuleForTreat(treat) != null){
                SaleRule rule = stock.getRuleForTreat(treat);
                if(rule.getConditions().getDayOfWeek().isPresent()){
                    System.out.println("Sale Days: " + rule.getConditions().getDayOfWeek().get());
                } else if(rule.getConditions().getDate().isPresent()){
                    System.out.println("Sale Date: " + rule.getConditions().getDate().get());
                }
                if(rule.getBulkPricing().isPresent()){
                    System.out.println("===> " + rule.getBulkPricing().get().getAmount() + " for $"
                            + rule.getBulkPricing().get().getTotalPrice());
                }
                if(rule.getPercentage().isPresent()){
                    double perc = rule.getPercentage().get()*100;
                    System.out.println("===> " + perc + "% off");
                }
            }

        }
        else{
            System.out.println("Not a valid treat.");
        }
    }

    //check that user input is valid
    private static boolean validInputForAmounts(String input){
        if (input.equals("clear") || treatTypes.contains(input)){
            return true;
        } else {
            String[] splitArray = input.split(",");
            if(splitArray.length != treatTypes.size()){
                return false;
            }
            for (String i : splitArray) {
                try {
                    Integer.parseInt(i.replaceAll("\\s+", ""));
                } catch (Exception e) {
                    return false;
                }
            }
        }
        return true;
    }
}
