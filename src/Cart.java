import java.time.Instant;
import java.util.Date;
import java.util.Scanner;
import java.util.Set;

public class Cart {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Stock stock = new Stock();
        Set<String> treatTypes = stock.getTreatTypes();
        double totalCost = 0;
        while (true) {
            for (String treat : treatTypes) {
                System.out.println("How many " + treat + "s?");
                int amount = Integer.parseInt(scanner.next());
                totalCost += stock.calculatePriceForTreat(treat, amount, new Date(1633077830L * 1000));
                System.out.println("Subtotal " + totalCost);
            }
            totalCost = 0;
        }

    }
}
