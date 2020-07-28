import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;

public class TestClass {
    private static Stock testStock;
    private Date no_rule_date = new Date(1595882682L * 1000);

    @BeforeClass
    public static void testSetup() {
        testStock = new Stock("input/products-data.json", "input/sale_rules.json");
    }

    @Test
    public void testType1() {
        double totalCost = 0;
        totalCost += testStock.calculatePriceForTreat(3, 1, no_rule_date);
        totalCost += testStock.calculatePriceForTreat(1, 4, no_rule_date);
        totalCost += testStock.calculatePriceForTreat(2, 1, no_rule_date);
        totalCost += testStock.calculatePriceForTreat(4, 0, no_rule_date);
        assertTrue(16.25 == totalCost);
    }

    @Test
    public void testType2() {
        double totalCost = 0;
        totalCost += testStock.calculatePriceForTreat(3, 8, no_rule_date);
        totalCost += testStock.calculatePriceForTreat(1, 0, no_rule_date);
        totalCost += testStock.calculatePriceForTreat(2, 0, no_rule_date);
        totalCost += testStock.calculatePriceForTreat(4, 0, no_rule_date);
        assertTrue(8.50 == totalCost);
    }

    @Test
    public void testType3() {
        double totalCost = 0;
        totalCost += testStock.calculatePriceForTreat(3, 1, no_rule_date);
        totalCost += testStock.calculatePriceForTreat(1, 1, no_rule_date);
        totalCost += testStock.calculatePriceForTreat(2, 1, no_rule_date);
        totalCost += testStock.calculatePriceForTreat(4, 2, no_rule_date);
        assertTrue(12.25 == totalCost);
    }

    //test 10/1/2021
    @Test
    public void testDate1() {
        double totalCost = 0;
        Date date = new Date(1633077830L * 1000);
        totalCost += testStock.calculatePriceForTreat(3, 8, date);
        totalCost += testStock.calculatePriceForTreat(1, 0, date);
        totalCost += testStock.calculatePriceForTreat(2, 4, date);
        totalCost += testStock.calculatePriceForTreat(4, 0, date);
        assertTrue(30 == totalCost);
    }

    //test Tuesday
    @Test
    public void testDate2() {
        double totalCost = 0;
        Date date = new Date(1595925830L * 1000);
        totalCost += testStock.calculatePriceForTreat(3, 6, date);
        totalCost += testStock.calculatePriceForTreat(1, 0, date);
        totalCost += testStock.calculatePriceForTreat(2, 1, date);
        totalCost += testStock.calculatePriceForTreat(4, 5, date);
        assertTrue(15.5 == totalCost);
    }

    //test Friday
    @Test
    public void testDate3() {
        double totalCost = 0;
        Date date = new Date(1595580230L * 1000);
        totalCost += testStock.calculatePriceForTreat(3, 10, date);
        totalCost += testStock.calculatePriceForTreat(1, 6, date);
        totalCost += testStock.calculatePriceForTreat(2, 1, date);
        totalCost += testStock.calculatePriceForTreat(4, 1, date);
        System.out.println(totalCost);
        assertTrue(28.0 == totalCost);
    }

    //test Oct 1, 2020
    @Test
    public void testDate4() {
        double totalCost = 0;
        Date date = new Date(1601552735L * 1000);
        totalCost += testStock.calculatePriceForTreat(3, 10, date);
        totalCost += testStock.calculatePriceForTreat(1, 6, date);
        totalCost += testStock.calculatePriceForTreat(2, 1, date);
        totalCost += testStock.calculatePriceForTreat(4, 1, date);
        System.out.println(totalCost);
        assertTrue(28.5 == totalCost);
    }

}
