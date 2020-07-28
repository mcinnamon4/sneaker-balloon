import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;

public class TestClass {
    private static Stock testStock;
    private Date currDate = new Date();

    @BeforeClass
    public static void testSetup() {
        testStock = new Stock("input/products-data.json", "input/sale_rules.json");
    }

    @Test
    public void testType1() {
        double totalCost = 0;
        totalCost += testStock.calculatePriceForTreat("Cookie", 1, currDate);
        totalCost += testStock.calculatePriceForTreat("Brownie", 4, currDate);
        totalCost += testStock.calculatePriceForTreat("Key Lime Cheesecake", 1, currDate);
        totalCost += testStock.calculatePriceForTreat("Mini Gingerbread Donut", 0, currDate);
        assertTrue(16.25 == totalCost);
    }

    @Test
    public void testType2() {
        double totalCost = 0;
        totalCost += testStock.calculatePriceForTreat("Cookie", 8, currDate);
        totalCost += testStock.calculatePriceForTreat("Brownie", 0, currDate);
        totalCost += testStock.calculatePriceForTreat("Key Lime Cheesecake", 0, currDate);
        totalCost += testStock.calculatePriceForTreat("Mini Gingerbread Donut", 0, currDate);
        assertTrue(8.50 == totalCost);
    }

    @Test
    public void testType3() {
        double totalCost = 0;
        totalCost += testStock.calculatePriceForTreat("Cookie", 1, currDate);
        totalCost += testStock.calculatePriceForTreat("Brownie", 1, currDate);
        totalCost += testStock.calculatePriceForTreat("Key Lime Cheesecake", 1, currDate);
        totalCost += testStock.calculatePriceForTreat("Mini Gingerbread Donut", 2, currDate);
        assertTrue(12.25 == totalCost);
    }

    //test 10/1/2021
    @Test
    public void testDate1() {
        double totalCost = 0;
        Date date = new Date(1633077830L * 1000);
        totalCost += testStock.calculatePriceForTreat("Cookie", 8, date);
        totalCost += testStock.calculatePriceForTreat("Brownie", 0, date);
        totalCost += testStock.calculatePriceForTreat("Key Lime Cheesecake", 4, date);
        totalCost += testStock.calculatePriceForTreat("Mini Gingerbread Donut", 0, date);
        assertTrue(30 == totalCost);
    }

    //test Tuesday
    @Test
    public void testDate2() {
        double totalCost = 0;
        Date date = new Date(1595925830L * 1000);
        totalCost += testStock.calculatePriceForTreat("Cookie", 6, date);
        totalCost += testStock.calculatePriceForTreat("Brownie", 0, date);
        totalCost += testStock.calculatePriceForTreat("Key Lime Cheesecake", 1, date);
        totalCost += testStock.calculatePriceForTreat("Mini Gingerbread Donut", 5, date);
        assertTrue(15.5 == totalCost);
    }

    //test Friday
    @Test
    public void testDate3() {
        double totalCost = 0;
        Date date = new Date(1595580230L * 1000);
        totalCost += testStock.calculatePriceForTreat("Cookie", 10, date);
        totalCost += testStock.calculatePriceForTreat("Brownie", 6, date);
        totalCost += testStock.calculatePriceForTreat("Key Lime Cheesecake", 1, date);
        totalCost += testStock.calculatePriceForTreat("Mini Gingerbread Donut", 1, date);
        System.out.println(totalCost);
        assertTrue(28.0 == totalCost);
    }

    //test Oct 1, 2020
    @Test
    public void testDate4() {
        double totalCost = 0;
        Date date = new Date(1601552735L * 1000);
        totalCost += testStock.calculatePriceForTreat("Cookie", 10, date);
        totalCost += testStock.calculatePriceForTreat("Brownie", 6, date);
        totalCost += testStock.calculatePriceForTreat("Key Lime Cheesecake", 1, date);
        totalCost += testStock.calculatePriceForTreat("Mini Gingerbread Donut", 1, date);
        System.out.println(totalCost);
        assertTrue(28.5 == totalCost);
    }

}
