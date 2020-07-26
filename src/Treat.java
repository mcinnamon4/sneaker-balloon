import org.json.simple.JSONObject;

import java.util.Optional;

public class Treat {
    int id;
    String name;
    String imageURL;
    double price;
    Optional<BulkPricing> bulkPricing;

    public Treat(int id, String name, String imageURL, double price, BulkPricing bulkPricing) {
        this.id = id;
        this.name = name;
        this.imageURL = imageURL;
        this.price = price;
        this.bulkPricing = Optional.ofNullable(bulkPricing);
    }

    public Treat(JSONObject obj){
        try{
            this.id = Integer.parseInt(obj.get("id").toString());
            this.name = (String) obj.get("name");
            this.imageURL= (String) obj.get("imageURL");
            this.price = Double.parseDouble(obj.get("price").toString());
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

    String getImageURL(){
        return imageURL;
    }

    double getPrice(){
        return price;
    }

    Optional<BulkPricing> getBulkPricing(){
        return bulkPricing;
    }

    public String toString(){
        return "Id: " + getId() + "\nName: " + name + "\nImageURL: " + imageURL + "\nPrice: " + price
                + "\nBulk Pricing: " + bulkPricing.toString();
    }
}
