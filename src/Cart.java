import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class Cart {
    private static Connection c;
    private static Stock stock;
    private static HashMap<Integer, Treat> treats;
    private static ArrayList<String> treatNames;
    private static Scanner scanner;
    //default time set to Oct 21 2021 unless input is provided.
    private static Date date = new Date(1633077830L * 1000);

    public static void main(String[] args) {
        //connect to SQL database and create table if it doesn't exit
        connect();
        createIfNecessary();

        scanner = new Scanner(System.in);
        if(args.length < 3){
            stock = new Stock("input/products-data.json", "input/sale_rules.json");
        } else {
            stock = new Stock(args[0], args[1]);
            DateFormat format = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);
            try {
                date = format.parse(args[2]);
                System.out.println(date);
            } catch (ParseException e) {
                System.err.println(e.getMessage());
            }
        }

        //get Treats from Stock
        treats = stock.getTreats();
        treatNames= stock.getTreatTypes();
        System.out.println("Welcome to the CAI Bakery! What is your name?");
        String userName = scanner.nextLine();

        //determine whether or not user has been to Bakery prior
        if(getRecord(userName).size() == 0) {
            service(userName);
        } else {
            System.out.println("\nWelcome back. Your cart contains:");
            for(int x = 0; x < 4; x++){
                System.out.print(getRecord(userName).get(x) + " " + stock.getTreats().get(x+1).getName() + "s, ");
            }
            System.out.print("\n");
            service(userName);
        }
    }

    /**
     * Gathers user's cart additions & calculates cost
     */
    public static void service(String name) {
        double totalCost = 0;
        while (true) {
            //Menu & Instruction printing
            System.out.println("\n====Menu====");
            for (String treat : treatNames) {
                System.out.print(treat + ": $" + stock.getTreatCostFromName(treat)+ ", ");
            }
            System.out.println("\n============================================================================");
            System.out.println("How many treats would you like to add to your cart?" +
                    " Please enter the treat followed by the amount, separated by comma & space. (Ex. Brownie:1, Key Lime Cheesecake:2)");
            System.out.println("---> Type the name of a treat to learn more.");
            System.out.println("---> You can clear your cart at any time by typing \"clear\".");
            String input = scanner.nextLine();

            //validating input
            while(!validInputForAmounts(input)){
                System.out.println("Please enter a valid input.");
                input = scanner.nextLine();
            }

            //handling input
            if(input.equals("clear")){
                clear(name);
                totalCost=0;
            }
            else if(treatNames.contains(input)){
                learnMore(input);
            } else {
                try {
                    HashMap<String, Integer> amountList = generateAmountMap(input);
                    ArrayList<Integer> updatedAmounts = setRecord(name, amountList);
                    System.out.println("Your cart contains: \n");
                    //calculates cost
                    for (int x = 0; x < treatNames.size(); x++) {
                        System.out.print(updatedAmounts.get(x) + " " + stock.getTreatNameFromId(x+1) + "s, ");
                        totalCost += stock.calculatePriceForTreat(x+1, updatedAmounts.get(x), date);
                    }
                    System.out.println("\nSubtotal: $" + totalCost);
                    totalCost = 0;
                } catch (Exception e){
                    System.err.println(e);
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

    /**
     * Updates sql database and returns the updated amounts
     **/
    public static ArrayList<Integer> setRecord(String name, HashMap<String, Integer> amountList) {
        ArrayList<Integer> original_amount = getRecord(name);
        //converts treat name to id to prep for sql query
        HashMap<Integer, Integer> amountListMappedToId = new HashMap<Integer, Integer>();
        for (String treatName : amountList.keySet()){
            amountListMappedToId.put(stock.getTreatIdFromName(treatName), amountList.get(treatName));
        }
        String sql = "REPLACE INTO cart2(name,item1,item2,item3,item4) VALUES('"+ name+"',?,?,?,?)";

        //update amounts stored in sql table; if provided, adds new quantity to previously entered values
        ArrayList<Integer> totalAmountList = new ArrayList<Integer>();
        try( PreparedStatement pstmt = c.prepareStatement(sql) ){
            for (int x = 0; x < treatNames.size(); x++) {
                int total_amount = 0;
                if (original_amount.size() > 0) {
                    total_amount = original_amount.get(x);
                }
                if (amountListMappedToId.get(x+1) != null) {
                    total_amount += amountListMappedToId.get(x + 1);
                }
                totalAmountList.add(total_amount);
                pstmt.setInt(x+1, total_amount);
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return totalAmountList;
    }

    /**
     * clear cart by setting row to all 0 values
     **/
    public static void clear(String name) {
        String sql = "REPLACE INTO cart2(name,item1,item2,item3,item4) VALUES('"+ name+"',0,0,0,0)";
        try{
            Statement stmt = c.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("\nSubtotal: $0.0");
    }

    /**
     * prints info about treat
     **/
    public static void learnMore(String treat) {
        if(treatNames.contains(treat)){
            int treatId = stock.getTreatIdFromName(treat);
            Treat t = stock.getTreat(treatId);
            System.out.println("Standard Price: " + t.getPrice());
            System.out.println("Image: " + t.getImageURL());
            if(t.getBulkPricing().isPresent()){
                System.out.println("Bulk Pricing Info: " + t.getBulkPricing().get().getAmount() + " for $"
                        + t.getBulkPricing().get().getTotalPrice());
            }
            if(stock.getRuleForTreat(treatId) != null){
                SaleRule rule = stock.getRuleForTreat(treatId);
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


    /**
     * checks validity of user input
     **/
    private static boolean validInputForAmounts(String input){
        if (input.equals("clear") || treatNames.contains(input)){
            return true;
        } else {
            try{
                HashMap<String, Integer> results = generateAmountMap(input);
                for (String treat : results.keySet()){
                    if (stock.getTreatIdFromName(treat) == -1){
                        return false;
                    }
                }
            } catch(Exception e){
                return false;
            }
        }
        return true;
    }

    /**
     * maps user's arbitrarly ordered input to a Map of treat name & amount
     **/
    private static HashMap<String, Integer> generateAmountMap(String input){
        HashMap<String, Integer> amountList = new HashMap<String, Integer>();
        for (String i : input.split(", ")){
            String[] valArr = i.split(":");
            amountList.put(valArr[0], Integer.parseInt(valArr[1]));
        }
        return amountList;
    }

}
