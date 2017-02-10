package dgorbunov.stock.common;

import dgorbunov.stock.domain.Share;
import dgorbunov.stock.domain.Trader;
import dgorbunov.stock.domain.TraderBalance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Утилитный класс для тестов.
 */
public class StockTestingUtils {

    public static TraderBalance createTraderBalance(int money, List<Share> shares, int... balances) {
        if (balances.length != shares.size()) {
            throw new IllegalArgumentException("balances");
        }

        Map<Share, Integer> shareBalances = new HashMap<>(shares.size(), 1.0f);
        for (int i = 0; i < shares.size(); i++) {
            Share share = shares.get(i);
            int balance = balances[i];
            shareBalances.put(share, balance);
        }

        return new TraderBalance(money, shareBalances);
    }

    public static void assertShareBalance(
            Map<Trader, TraderBalance> balances,
            Trader trader,
            Share share,
            Integer quantity
    ) {
        TraderBalance traderBalance = balances.get(trader);
        assertNotNull(traderBalance);
        assertEquals(quantity, traderBalance.getShareBalance(share));
    }

}
