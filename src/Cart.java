import java.sql.*;
import java.util.*;
import java.util.Date;


public class Cart {
    private static Connection c;
    private static Stock stock;
    private static ArrayList<String> treatTypes;
    private static Scanner scanner;

    public static void main(String[] args) {
        connect();
        createIfNecessary();
        scanner = new Scanner(System.in);
        stock = new Stock();
        treatTypes= stock.getTreatTypes();
        System.out.println("Welcome to the CAI Bakery! What is your name?");
        String userName = scanner.next();
        if (getRecord(userName).size() == 0) {
            service(userName);
        } else {
            System.out.println("Your cart contains: \n");
            for(int x = 0; x < 4; x++){
                System.out.println(getRecord(userName).get(x) + " " + treatTypes.get(x) + "s");
            }
            service(userName);
        }


    }

    public static void service(String name) {
        double totalCost = 0;
        while (true) {
            for (String treat : treatTypes) {
                System.out.println("How many " + treat + "s?");
                int amount = Integer.parseInt(scanner.next());
                setRecord(name, 1, amount);
                totalCost += stock.calculatePriceForTreat(treat, amount, new Date(1633077830L * 1000));
                System.out.println("Subtotal " + totalCost);
            }
            totalCost = 0;
        }
    }

    public static void connect() {
        c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:bakery.db");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            //System.exit(0);
        }
        System.out.println("Opened database successfully");

    }

//    public static boolean tableExists(String tableName){
//        try{
//            DatabaseMetaData md = c.getMetaData();
//            ResultSet rs = md.getTables(null, null, tableName, null);
//            rs.last();
//            return rs.getRow() > 0;
//        }catch(SQLException ex){
//            System.err.println( ex.getClass().getName() + ": " + ex.getMessage() );
//        }
//        return false;
//    }

    public static void createIfNecessary() {
        String sql = "CREATE TABLE IF NOT EXISTS cart2 (\n"
                + " name text PRIMARY KEY NOT NULL,\n"
                + " item1 INTEGER,\n"
                + " item2 INTEGER,\n"
                + " item3 INTEGER,\n"
                + " item4 INTEGER\n"
                + ");";

        try{
            Statement stmt = c.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public static ArrayList<Integer> getRecord(String name) {
        String sql = "SELECT * FROM cart2 WHERE name='" + name+"';";
        ArrayList<Integer> result = new ArrayList<Integer>();
        try{
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            System.out.println("Successful");
            while (rs.next()) {
                result.add(rs.getInt("item1"));
                result.add(rs.getInt("item2"));
                result.add(rs.getInt("item3"));
                result.add(rs.getInt("item4"));
            }
        } catch (SQLException e) {
            System.out.println("HERE");
            System.out.println(e.getMessage());
        }
        return result;
    }

    public static void setRecord(String name, int itemNum, int amount) {
        String itemSql = "item" + itemNum;
        String sql = "REPLACE INTO cart2(name," + itemSql + ") VALUES(?,?)";
        try{
            PreparedStatement pstmt = c.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setDouble(2, amount);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
