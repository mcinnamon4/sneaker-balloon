import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import org.json.simple.*;
import org.json.simple.parser.*;

public class Stock {

    HashMap<Integer, Treat> treats;
    HashMap<Integer, SaleRule> rules;

    public Stock(String productsFile, String salesFile) {
        //load treats in bakery
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(productsFile));
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray treatsJson = (JSONArray) jsonObject.get("treats");
            treats = new HashMap<Integer, Treat>();
            for(int i = 0; i < treatsJson.size(); i++){
                JSONObject treatObj = (JSONObject) treatsJson.get(i);
                Treat t = new Treat(treatObj);
                treats.put(t.id, t);
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
            rules = new HashMap<Integer, SaleRule>();
            for(int i = 0; i < rulesJson.size(); i++){
                JSONObject ruleObj = (JSONObject) rulesJson.get(i);
                SaleRule rule = new SaleRule(ruleObj);
                rules.put(rule.id, rule);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns any applicable rules for date
     */
    public SaleRule ruleChecker(int treatId, Date date) {
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");
        String day_of_week = simpleDateformat.format(date);
        simpleDateformat = new SimpleDateFormat("MM/dd");
        String day_of_year = simpleDateformat.format(date);
        if(rules.get(treatId) != null){
            Optional<String> ruleDate = rules.get(treatId).getConditions().getDate();
            Optional<String> ruleDayofWeek = rules.get(treatId).getConditions().getDayOfWeek();
            if(ruleDate.isPresent() && ruleDayofWeek.isPresent()){
                if (ruleDate.get().equals(day_of_year) && ruleDayofWeek.get().equals(day_of_week)){
                    return rules.get(treatId);
                }
            } else if(ruleDate.isPresent()){
                if (ruleDate.get().equals(day_of_year)){
                    return rules.get(treatId);
                }
            } else {
                if (ruleDayofWeek.get().equals(day_of_week) ) {
                    return rules.get(treatId);
                }
            }
        }
        return null;
    }

    /**
     * Returns price for treat, taking rules & bulk pricing in mind
     * Note: that rules trump bulk pricing
     */
    public double calculatePriceForTreat(Integer treatId, int amount, Date date){
        System.out.println(treatId);
        Treat t = treats.get(treatId);
        BulkPricing bulkPricing = null;
        if(t == null){
            System.out.println("Treat not recognized.");
            return 0;
        }

        // determine if rules apply
        SaleRule rule = ruleChecker(treatId, date);
       // System.out.println("RULE " + rule.toString());
        if(rule != null){
            if (rule.getBulkPricing().isPresent()){
                return applyBulkPricing(t.getPrice(), amount, rule.getBulkPricing().get());
            }
            if (rule.getPercentage().isPresent()){
                return amount * t.getPrice() * (1 - rule.getPercentage().get().floatValue());
            }
        }

        // determine if bulk pricing applies
        if (!t.getBulkPricing().isPresent()){
            return amount * t.getPrice();
        } else {
            return applyBulkPricing(t.getPrice(), amount, t.getBulkPricing().get());
        }
    }

    /**
     * returns bulk price (with excess treat quantity taken into consideration)
     */
    private double applyBulkPricing(double price, int amount, BulkPricing bulkPricing){
        int bulkPricingAmount = bulkPricing.getAmount();
        int bulkDeals = amount / bulkPricingAmount;
        return ((amount % bulkPricingAmount * price) + (bulkDeals * bulkPricing.getTotalPrice()));
    }

    /**
     * Set of helper methods to access Treat information
     */

    public ArrayList<String> getTreatTypes(){
        ArrayList<String> treatTypes = new ArrayList<String>();
        for (Integer i : treats.keySet()){
            treatTypes.add(getTreat(i).getName());
        }
        return treatTypes;
    }

    public HashMap<Integer, Treat> getTreats(){
        return treats;
    }

    public double getTreatCostFromName(String treat){
        return treats.get(getTreatIdFromName(treat)).getPrice();
    }

    public Treat getTreat(Integer id){
        return treats.get(id);
    }

    public String getTreatNameFromId(Integer id){
        return treats.get(id).getName();
    }

    // returns -1 if treat name does not exist
    public Integer getTreatIdFromName(String name){
        for (Treat t : treats.values()){
            if(t.getName().equals(name)){
                return t.getId();
            }
        }
        return -1;
    }

    public SaleRule getRuleForTreat(Integer id){
        return rules.get(id);
    }

}
