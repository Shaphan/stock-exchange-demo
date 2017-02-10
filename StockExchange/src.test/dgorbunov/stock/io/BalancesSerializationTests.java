package dgorbunov.stock.io;

import dgorbunov.stock.common.StockTestingUtils;
import dgorbunov.stock.domain.Share;
import dgorbunov.stock.domain.Trader;
import dgorbunov.stock.domain.TraderBalance;
import org.junit.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

// Использование статик импортов для удобства в тестах (не в промышленном) коде
// ИМХО вполне допустимо.
import static dgorbunov.stock.common.StockTestingPredefines.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Тесты сериализатора и десериализатора балансов клиентов.
 */
public class BalancesSerializationTests {

    private String balancesText = "C1\t1000\t130\t240\t760\t320\n" +
            "C2\t4350\t370\t120\t950\t560\n" +
            "C3\t2760\t0\t0\t0\t0\n" +
            "C4\t560\t450\t540\t480\t950\n" +
            "C5\t1500\t0\t0\t400\t100\n" +
            "C6\t1300\t890\t320\t100\t0\n" +
            "C7\t750\t20\t0\t790\t0\n" +
            "C8\t7000\t90\t190\t0\t0\n" +
            "C9\t7250\t190\t190\t0\t280";

    @Test
    public void deserializationTest() {
        BalancesDeserializer deserializer = new BalancesDeserializer(Shares);
        Map<Trader, TraderBalance> traderBalances =
                deserializer.deserialize(new StringReader(balancesText));

        assertEquals(9, traderBalances.size());

        TraderBalance traderBalance = traderBalances.get(Trader1);
        assertNotNull(traderBalance);
        assertEquals(1000, traderBalance.getMoney());
        assertShareBalance(traderBalance, ShareA, 130);

        traderBalance = traderBalances.get(Trader2);
        assertNotNull(traderBalance);
        assertEquals(4350, traderBalance.getMoney());
        assertShareBalance(traderBalance, ShareB, 120);
    }

    @Test
    public void serializationTest() {
        // region Arrange
        String expectedText = "C1\t1000\t0\t1\t2\t3\n" +
                "C2\t-1000\t0\t-1\t-2\t-3";

        Map<Trader, TraderBalance> traderBalances = new HashMap<>(2, 1.0f);
        traderBalances.put(Trader1, StockTestingUtils.createTraderBalance(1000, Shares, 0, 1, 2, 3));
        traderBalances.put(Trader2, StockTestingUtils.createTraderBalance(-1000, Shares, 0, -1, -2, -3));

        BalancesSerializer serializer = new BalancesSerializer(Shares);
        // StringWriter допускается не оборачивать в try-with-resources / try-finally, равно как и StringReader.
        StringWriter sw = new StringWriter();
        //endregion Arrange

        // region Act
        serializer.serialize(traderBalances, sw);
        // endregion Act

        // region Assert
        assertEquals(expectedText, sw.toString());
        // endregion Assert
    }

    @Test
    public void deserializationAndSerializationTest() {
        // region Arrange
        BalancesDeserializer deserializer = new BalancesDeserializer(Shares);
        BalancesSerializer serializer = new BalancesSerializer(Shares);
        StringWriter sw = new StringWriter();
        // endregion Arrange

        // region Act
        Map<Trader, TraderBalance> traderBalances = deserializer.deserialize(new StringReader(balancesText));
        serializer.serialize(traderBalances, sw);
        // endregion Act

        // region Assert
        assertEquals(balancesText, sw.toString());
        // endregion Assert
    }

    private static void assertShareBalance(TraderBalance traderBalance, Share share, int expectedBalance) {
        Integer actualBalance = traderBalance.getShareBalance(share);
        assertNotNull(actualBalance);
        assertEquals(expectedBalance, actualBalance.intValue());
    }
}
