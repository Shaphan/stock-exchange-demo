package dgorbunov.stock.domain;

import dgorbunov.stock.common.StockTestingUtils;
import org.junit.Test;

import static dgorbunov.stock.common.StockTestingPredefines.ShareA;
import static dgorbunov.stock.common.StockTestingPredefines.Shares;
import static dgorbunov.stock.common.StockTestingPredefines.Trader1;
import static dgorbunov.stock.common.StockTestingUtils.assertBalancesEqual;
import static org.junit.Assert.assertEquals;

/**
 * Юнит-тесты класса {@link TraderBalance}.
 */
public class TraderBalanceTests {
    @Test
    public void applyBuyBidTest() {
        TraderBalance traderBalance = StockTestingUtils.createTraderBalance(0, Shares, -100, 0, 100, 200);
        Bid bid = new Bid(Trader1, ShareA, BidType.BUY, 1, 100);
        traderBalance.applyBid(bid);

        assertEquals(-100, traderBalance.getMoney());
        assertEquals(Integer.valueOf(0), traderBalance.getShareBalance(ShareA));
    }

    @Test
    public void applySellBidTest() {
        TraderBalance traderBalance = StockTestingUtils.createTraderBalance(0, Shares, -100, 0, 100, 200);
        Bid bid = new Bid(Trader1, ShareA, BidType.SELL, 1, 100);
        traderBalance.applyBid(bid);

        assertEquals(100, traderBalance.getMoney());
        assertEquals(Integer.valueOf(-200), traderBalance.getShareBalance(ShareA));
    }

    @Test
    public void copyTest() {
        TraderBalance traderBalance = StockTestingUtils.createTraderBalance(1000, Shares, -100, 0, 100, 200);
        assertBalancesEqual(Shares, traderBalance, traderBalance.copy());
    }
}
