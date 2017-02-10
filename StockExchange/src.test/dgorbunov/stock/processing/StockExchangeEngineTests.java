package dgorbunov.stock.processing;

import dgorbunov.stock.common.StockTestingUtils;
import dgorbunov.stock.domain.*;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.*;
import java.util.function.Supplier;

import static dgorbunov.stock.common.StockTestingPredefines.*;
import static dgorbunov.stock.common.StockTestingUtils.assertShareBalance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class StockExchangeEngineTests {

    // region Tests

    @Test
    public void stockEngineTest() {
        // region Arrange

        TraderBalance traderBalance1 = StockTestingUtils.createTraderBalance(0, Shares, 100, 200, 300, 400);
        TraderBalance traderBalance2 = StockTestingUtils.createTraderBalance(1000, Shares, 0, 0, 0, 0);
        TraderBalance traderBalance3 = StockTestingUtils.createTraderBalance(-500, Shares, 200, -10, -20, 0);
        TraderBalance traderBalance4 = StockTestingUtils.createTraderBalance(0, Shares, 0, 0, 0, 0);
        TraderBalance traderBalance5 = StockTestingUtils.createTraderBalance(0, Shares, 0, 0, 0, 0);

        Map<Trader, TraderBalance> traderBalances = new HashMap<>(3, 1.0f);
        traderBalances.put(Trader1, traderBalance1);
        traderBalances.put(Trader2, traderBalance2);
        traderBalances.put(Trader3, traderBalance3);
        traderBalances.put(Trader4, traderBalance4);
        traderBalances.put(Trader5, traderBalance5);

        StockExchangeEngine exchangeEngine = new StockExchangeEngineImpl(copy(traderBalances));
        FakeBidsSupplier bidsSupplier = new FakeBidsSupplier(
                new Bid(Trader1, ShareA, BidType.SELL, 50, 1),
                new Bid(Trader1, ShareB, BidType.SELL, 30, 5),
                new Bid(Trader3, ShareC, BidType.BUY, 100, 3));

        // endregion Arrange

        // region Act
        exchangeEngine.processBids(bidsSupplier);
        Map<Trader, TraderBalance> resultingBalances = exchangeEngine.getTraderBalancesModifiable();
        // endregion Act

        // region Assert
        assertShareBalance(resultingBalances, Trader1, ShareA, 100);
        assertBalancesEqual(Shares, traderBalances, resultingBalances);
        // endregion Assert

        // region Arrange
        bidsSupplier = new FakeBidsSupplier(
                new Bid(Trader2, ShareA, BidType.BUY, 50, 1),
                new Bid(Trader2, ShareB, BidType.BUY, 20, 5),
                new Bid(Trader2, ShareB, BidType.BUY, 30, 2),
                new Bid(Trader2, ShareC, BidType.SELL, 100, 3));
        // endregion

        // region Act
        exchangeEngine.processBids(bidsSupplier);
        traderBalances = exchangeEngine.getTraderBalancesModifiable();
        // endregion Act

        // region Assert
        assertMoneyBalance(traderBalances, Trader1, 50);
        assertMoneyBalance(traderBalances, Trader2, 1250);
        assertMoneyBalance(traderBalances, Trader3, -800);
        assertShareBalance(traderBalances, Trader1, ShareA, 99);
        assertShareBalance(traderBalances, Trader2, ShareA, 1);
        assertShareBalance(traderBalances, Trader1, ShareB, 200);
        assertShareBalance(traderBalances, Trader2, ShareC, -3);
        // endregion
    }

    @Test
    public void sameTraderBidsTest() {
        // region Arrange

        TraderBalance traderBalance1 = StockTestingUtils.createTraderBalance(1000, Shares, 100, 0, 0, 0);
        TraderBalance traderBalance2 = StockTestingUtils.createTraderBalance(2000, Shares, 200, 0, 0, 0);
        Map<Trader, TraderBalance> traderBalances = new HashMap<>(2, 1.0f);
        traderBalances.put(Trader1, traderBalance1);
        traderBalances.put(Trader2, traderBalance2);

        FakeBidsSupplier bidsSupplier = new FakeBidsSupplier(
                new Bid(Trader1, ShareA, BidType.BUY, 100, 10),
                new Bid(Trader1, ShareA, BidType.SELL, 100, 10),
                // Должна обработаться именно эта заявка.
                new Bid(Trader2, ShareA, BidType.SELL, 100, 10));

        StockExchangeEngine exchangeEngine = new StockExchangeEngineImpl(traderBalances);

        // endregion Arrange

        // region Act

        exchangeEngine.processBids(bidsSupplier);
        traderBalances = exchangeEngine.getTraderBalancesModifiable();

        // endregion Act

        // region Assert

        assertMoneyBalance(traderBalances, Trader1, 0);
        assertMoneyBalance(traderBalances, Trader2, 3000);
        assertShareBalance(traderBalances, Trader1, ShareA, 110);
        assertShareBalance(traderBalances, Trader2, ShareA, 190);

        // endregion
    }

    /**
     * Этот тест проверяет порядок исполнения заявок.
     * Принцип проверки: из двух одинаковых заявок от разных трейдеров должна быть исполнена первая.
     */
    @Test
    public void bidsOrderTest() {
        TraderBalance traderBalance1 = StockTestingUtils.createTraderBalance(0, Shares, 100, 200, 300, 400);
        TraderBalance traderBalance2 = StockTestingUtils.createTraderBalance(2000, Shares, 0, 0, 0, 0);
        TraderBalance traderBalance3 = StockTestingUtils.createTraderBalance(3000, Shares, 0, 0, 0, 0);
        TraderBalance traderBalance4 = StockTestingUtils.createTraderBalance(4000, Shares, 0, 0, 0, 0);
        TraderBalance traderBalance5 = StockTestingUtils.createTraderBalance(5000, Shares, 0, 0, 0, 0);
        Map<Trader, TraderBalance> traderBalances = new HashMap<>(2, 1.0f);
        traderBalances.put(Trader1, traderBalance1);
        traderBalances.put(Trader2, traderBalance2);
        traderBalances.put(Trader3, traderBalance3);
        traderBalances.put(Trader4, traderBalance4);
        traderBalances.put(Trader5, traderBalance5);

        StockExchangeEngine exchangeEngine = new StockExchangeEngineImpl(traderBalances);

        List<Bid> bids = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            bids.addAll(Arrays.asList(
                    new Bid(Trader1, ShareD, BidType.SELL, 10, 100),
                    new Bid(Trader2, ShareD, BidType.SELL, 10, 100),
                    new Bid(Trader5, ShareD, BidType.SELL, 100, 10),
                    new Bid(Trader4, ShareD, BidType.SELL, 100, 10),
                    new Bid(Trader2, ShareA, BidType.BUY, 85, i + 1)));
        }
        Supplier<Bid> bidsSupplier = new FakeBidsSupplier(bids);

        exchangeEngine.processBids(bidsSupplier);

        int trader1B = traderBalance1.getMoney();
        int trader2B = traderBalance2.getMoney();
        for (int i = 0; i < 10000; i++) {
            if (i % 2 == 0) {
                trader1B += 1000;
            } else {
                trader2B += 1000;
            }
            bidsSupplier = new FakeBidsSupplier(new Bid(Trader3, ShareD, BidType.BUY, 10, 100));
            exchangeEngine.processBids(bidsSupplier);
            traderBalances = exchangeEngine.getTraderBalancesModifiable();
            assertMoneyBalance(traderBalances, Trader1, trader1B);
            assertMoneyBalance(traderBalances, Trader2, trader2B);
        }
    }

    // endregion

    // region Other methods

    private static void assertMoneyBalance(
            Map<Trader, TraderBalance> balances,
            Trader trader,
            int money
    ) {
        TraderBalance traderBalance = balances.get(trader);
        assertNotNull(traderBalance);
        assertEquals(money, traderBalance.getMoney());
    }

    private static Map<Trader, TraderBalance> copy(Map<Trader, TraderBalance> source) {
        Map<Trader, TraderBalance> result = new HashMap<>(source.size());
        for (Map.Entry<Trader, TraderBalance> entry : source.entrySet()) {
            result.put(entry.getKey(), entry.getValue().copy());
        }
        return result;
    }

    private static void assertBalancesEqual(
            @NotNull List<Share> shares,
            @NotNull Map<Trader, TraderBalance> expected,
            @NotNull Map<Trader, TraderBalance> actual
    ) {
        assertEquals(expected.keySet(), actual.keySet());
        for (Map.Entry<Trader, TraderBalance> expectedEntry : expected.entrySet()) {
            TraderBalance expectedTraderBalance = expectedEntry.getValue();
            TraderBalance actualTraderBalance = actual.get(expectedEntry.getKey());
            assertNotNull(actualTraderBalance);
            assertEquals(expectedTraderBalance.getMoney(), actualTraderBalance.getMoney());
            for (Share share : shares) {
                assertEquals(expectedTraderBalance.getShareBalance(share), actualTraderBalance.getShareBalance(share));
            }
        }
    }

    // endregion

    // region Nested types

    /**
     * Поставщик биржевых заявок.
     * Можно было бы сделать мок, но в данном случае так проще.
     */
    private static class FakeBidsSupplier implements Supplier<Bid> {
        Iterator<Bid> iterator;

        FakeBidsSupplier(Iterable<Bid> bids) {
            iterator = bids.iterator();
        }

        FakeBidsSupplier(Bid... bids) {
            this(Arrays.asList(bids));
        }

        @Override
        public Bid get() {
            return iterator.hasNext() ? iterator.next() : null;
        }
    }

    // endregion
}
