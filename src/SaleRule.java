import org.json.simple.JSONObject;
import java.util.Optional;

public class SaleRule {
    int id;
    String name;
    Conditions conditions;
    Optional<BulkPricing> bulkPricing;;
    Optional<Double> percentage;

    public SaleRule(int id, String name, Conditions conditions, BulkPricing bulkPricing, Optional<Double> percentage) {
        this.id = id;
        this.name = name;
        this.conditions = conditions;
        this.bulkPricing = Optional.ofNullable(bulkPricing);
        this.percentage = percentage;
    }

    public SaleRule(JSONObject obj){
        try{
            this.id = Integer.parseInt(obj.get("id").toString());
            this.name = (String) obj.get("name");
            JSONObject conditionsObj = (JSONObject) obj.get("conditions");
            this.conditions = new Conditions(conditionsObj);
            if(obj.get("percentage") == null){
                this.percentage = Optional.empty();
            } else {
                this.percentage = Optional.of((Double) obj.get("percentage"));
            }
            if(obj.get("bulkPricing") == null){
                this.bulkPricing = Optional.empty();
            } else {
                JSONObject bulkObj = (JSONObject) obj.get("bulkPricing");
                this.bulkPricing = Optional.of(new BulkPricing(bulkObj));
            }
        } catch (Exception e){
            System.out.println ("Missing JSON values");
            e.printStackTrace();
        }
    }

    int getId(){
        return id;
    }

    String getName(){
        return name;
    }

    Conditions getConditions(){
        return conditions;
    }

    Optional<BulkPricing> getBulkPricing(){
        return bulkPricing;
    }

    Optional<Double> getPercentage(){
        return percentage;
    }
    public String toString(){
        return "Id: " + getId() + "\nName: " + name + "\nConditions: " + conditions
                + "\nBulk Pricing: " + bulkPricing.toString() + "\nPercentage: " + percentage;
    }
}

class Conditions {
    Optional<String> day_of_week;
    Optional<String> date;

    public Conditions(JSONObject obj) {
        if (obj.get("day_of_week") == null){
            this.day_of_week = Optional.empty();
        } else {
            this.day_of_week = Optional.of(obj.get("day_of_week").toString());
        }
        if (obj.get("date") == null) {
            this.date = Optional.empty();
        } else {
            this.date = Optional.of(obj.get("date").toString());
        }
    }

    public Conditions(String day_of_week, String date) {
        this.day_of_week = Optional.of(day_of_week);
        this.date = Optional.of(date);
    }

    Optional<String> getDayOfWeek() {
        return day_of_week;
    }

    Optional<String> getDate() {
        return date;
    }

    public String toString() {
        return "Day of Week: " + day_of_week + "\nDate: " + date;
    }
}
