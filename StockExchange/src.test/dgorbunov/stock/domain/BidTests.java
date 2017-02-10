package dgorbunov.stock.domain;

import org.junit.Test;

import static dgorbunov.stock.common.StockTestingPredefines.ShareA;
import static dgorbunov.stock.common.StockTestingPredefines.Trader1;
import static org.junit.Assert.assertEquals;

/**
 * Юнит-тесты класса заявки {@link Bid}.
 */
public class BidTests {
    @Test
    public void testBidSum() {
        Bid bid = new Bid(Trader1, ShareA,
                BidType.BUY, 10, 15);
        assertEquals(150, bid.getSum());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativePrice() {
        new Bid(Trader1, ShareA, BidType.BUY, -200, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeQuantity() {
        new Bid(Trader1, ShareA, BidType.BUY, 200, -10);
    }

    @Test(expected = ArithmeticException.class)
    public void testSumOverflow() {
        new Bid(Trader1, ShareA, BidType.BUY,
                Integer.MAX_VALUE, 2);
    }
}
