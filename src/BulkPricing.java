import org.json.simple.JSONObject;

public class BulkPricing {
    int amount;
    double totalPrice;

    public BulkPricing(JSONObject obj) {
        if (obj != null) {
            this.amount = Integer.parseInt(obj.get("amount").toString());
            this.totalPrice = Double.parseDouble(obj.get("totalPrice").toString());
        }
    }

    public BulkPricing(int amount, double totalPrice) {
        this.amount = amount;
        this.totalPrice = totalPrice;
    }

    int getAmount() {
        return amount;
    }

    double getTotalPrice() {
        return totalPrice;
    }

    public String toString() {
        return "Amount: " + amount + "\nTotal Price: " + totalPrice;
    }
}