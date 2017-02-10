package dgorbunov.stock.domain;

import dgorbunov.stock.common.StockTestingUtils;

import static dgorbunov.stock.common.StockTestingPredefines.ShareA;
import static dgorbunov.stock.common.StockTestingPredefines.Shares;
import static dgorbunov.stock.common.StockTestingPredefines.Trader1;
import static org.junit.Assert.assertEquals;

/**
 * Юнит-тесты класса {@link TraderBalance}.
 */
public class TraderBalanceTests {
    public void applyBidTest() {
        TraderBalance traderBalance = StockTestingUtils.createTraderBalance(0, Shares, -100, 0, 100, 200);
        Bid bid = new Bid(Trader1, ShareA, BidType.BUY, 1, 100);
        traderBalance.applyBid(bid);

        assertEquals(100, traderBalance.getMoney());
        assertEquals(0, traderBalance.getShareBalance(ShareA).intValue());
    }
}
