package dgorbunov.stock.io;

import dgorbunov.stock.domain.Bid;
import dgorbunov.stock.domain.BidType;
import dgorbunov.stock.domain.Share;
import dgorbunov.stock.domain.Trader;
import org.junit.Test;

import java.io.StringReader;
import java.util.Arrays;

import static dgorbunov.stock.common.StockTestingPredefines.*;
import static org.junit.Assert.*;

/**
 * Тест десериализатора (поставщика) заявок.
 */
public class BidSupplierTests {
    @Test
    public void supplierStringTest() throws Exception {
        String str = "C1\tb\tD\t6\t2\n" +
                "C3\tb\tD\t3\t3\n" +
                "C2\ts\tC\t6\t1\n" +
                "C3\tb\tD\t4\t3\n";

        StringReader reader = new StringReader(str);
        try (BidsSupplier bidsSupplier = new BidsSupplier(reader, Traders, Shares)) {

            Bid bid = bidsSupplier.get();
            assertEquals(Trader1, bid.getTrader());
            assertEquals(BidType.BUY, bid.getBidType());
            assertEquals(ShareD, bid.getShare());
            assertEquals(6, bid.getPrice());
            assertEquals(2, bid.getQuantity());

            bid = bidsSupplier.get();
            assertEquals(Trader3, bid.getTrader());
            assertEquals(BidType.BUY, bid.getBidType());
            assertEquals(ShareD, bid.getShare());
            assertEquals(3, bid.getPrice());
            assertEquals(3, bid.getQuantity());

            bid = bidsSupplier.get();
            assertEquals(Trader2, bid.getTrader());
            assertEquals(BidType.SELL, bid.getBidType());
            assertEquals(ShareC, bid.getShare());
            assertEquals(6, bid.getPrice());
            assertEquals(1, bid.getQuantity());

            bid = bidsSupplier.get();
            assertEquals(Trader3, bid.getTrader());
            assertEquals(BidType.BUY, bid.getBidType());
            assertEquals(ShareD, bid.getShare());
            assertEquals(4, bid.getPrice());
            assertEquals(3, bid.getQuantity());

            assertNull(bidsSupplier.get());
        }
    }
}