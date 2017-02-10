package dgorbunov.stock.processing;

import dgorbunov.stock.domain.BidType;
import org.junit.Test;

import static dgorbunov.stock.common.StockTestingPredefines.ShareA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Юнит-тесты ключа {@link BidKeyImpl}.
 */
public class BidKeyTests {
    @Test
    public void equalsAndHashCodeTests() {
        BidKey bidKey1 = new BidKeyImpl(ShareA, BidType.BUY, 1, 2);
        BidKey bidKey2 = new BidKeyImpl(ShareA, BidType.BUY, 1, 2);
        BidKey bidKey3 = new BidKeyImpl(ShareA, BidType.SELL, 1, 2);

        assertEquals(bidKey1, bidKey2);
        assertEquals(bidKey1.hashCode(), bidKey2.hashCode());
        assertNotEquals(bidKey1, bidKey3);
    }
}
