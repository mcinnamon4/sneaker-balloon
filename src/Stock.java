import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import org.json.simple.*;
import org.json.simple.parser.*;

public class Stock {

    HashMap<String, Treat> treats;
    HashMap<String, SaleRule> rules;

    public Stock(String productsFile, String salesFile) {
        //load treats in bakery
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(productsFile));
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray treatsJson = (JSONArray) jsonObject.get("treats");
            treats = new HashMap<String, Treat>();
            for(int i = 0; i < treatsJson.size(); i++){
                JSONObject treatObj = (JSONObject) treatsJson.get(i);
                Treat t = new Treat(treatObj);
                treats.put(t.name, t);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        //load rules
        JSONParser parser2 = new JSONParser();
        try {
            Object obj = parser2.parse(new FileReader(salesFile));
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray rulesJson = (JSONArray) jsonObject.get("rules");
            rules = new HashMap<String, SaleRule>();
            for(int i = 0; i < rulesJson.size(); i++){
                JSONObject ruleObj = (JSONObject) rulesJson.get(i);
                SaleRule rule = new SaleRule(ruleObj);
                rules.put(rule.name, rule);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public SaleRule ruleChecker(String treat, Date date) {
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");
        String day_of_week = simpleDateformat.format(date);
        simpleDateformat = new SimpleDateFormat("MM/dd");
        String day_of_year = simpleDateformat.format(date);
        if(rules.get(treat) != null){
            Optional<String> ruleDate = rules.get(treat).getConditions().getDate();
            Optional<String> ruleDayofWeek = rules.get(treat).getConditions().getDayOfWeek();
            if(ruleDate.isPresent() && ruleDayofWeek.isPresent()){
                if (ruleDate.get().equals(day_of_year) && ruleDayofWeek.get().equals(day_of_week)){
                    return rules.get(treat);
                }
            } else if(ruleDate.isPresent()){
                if (ruleDate.get().equals(day_of_year)){
                    return rules.get(treat);
                }
            } else {
                if (ruleDayofWeek.get().equals(day_of_week) ) {
                    return rules.get(treat);
                }
            }
        }
        return null;
    }

    public double calculatePriceForTreat(String treat, int amount, Date date){
        Treat t = treats.get(treat);
        BulkPricing bulkPricing = null;
        if(t == null){
            System.out.println("Treat not recognized.");
            return 0;
        }

        // see if rules apply
        SaleRule rule = ruleChecker(treat, date);
        if(rule != null){
            if (rule.getBulkPricing().isPresent()){
                return applyBulkPricing(t.getPrice(), amount, rule.getBulkPricing().get());
            }
            if (rule.getPercentage().isPresent()){
                return amount * t.getPrice() * (1 - rule.getPercentage().get().floatValue());
            }
        }

        if (!t.getBulkPricing().isPresent()){
            return amount * t.getPrice();
        } else {
            return applyBulkPricing(t.getPrice(), amount, t.getBulkPricing().get());
        }
    }

    public ArrayList<String> getTreatTypes(){
        ArrayList<String> treatTypes = new ArrayList<String>();
        for (String treat : treats.keySet()){
            treatTypes.add(treat);
        }
        return treatTypes;
    }

    public double getTreatCost(String treat){
        return treats.get(treat).getPrice();
    }

    public Treat getTreat(String treat){
        return treats.get(treat);
    }

    public SaleRule getRuleForTreat(String treat){
        return rules.get(treat);
    }

    private double applyBulkPricing(double price, int amount, BulkPricing bulkPricing){
        int bulkPricingAmount = bulkPricing.getAmount();
        int bulkDeals = amount / bulkPricingAmount;
        return ((amount % bulkPricingAmount * price) + (bulkDeals * bulkPricing.getTotalPrice()));
    }
}
