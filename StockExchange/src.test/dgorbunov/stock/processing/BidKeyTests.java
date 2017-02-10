package dgorbunov.stock.processing;

import dgorbunov.stock.domain.BidType;
import org.junit.Test;

import static dgorbunov.stock.common.StockTestingPredefines.ShareA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Юнит-тесты классов {@link MutableBidKey} и {@link ImmutableBidKey}.
 */
public class BidKeyTests {
    @Test
    public void equalsAndHashCodeTests() {
        ImmutableBidKey immutableBidKey1 = new ImmutableBidKey(ShareA, BidType.BUY, 1, 2);
        ImmutableBidKey immutableBidKey2 = new ImmutableBidKey(ShareA, BidType.BUY, 1, 2);
        ImmutableBidKey immutableBidKey3 = new ImmutableBidKey(ShareA, BidType.SELL, 1, 2);
        MutableBidKey mutableBidKey1 = new MutableBidKey(ShareA, BidType.BUY, 1, 2);

        assertEquals(immutableBidKey1, immutableBidKey2);
        assertEquals(immutableBidKey1.hashCode(), immutableBidKey2.hashCode());
        assertEquals(immutableBidKey1, mutableBidKey1);
        assertEquals(immutableBidKey1.hashCode(), mutableBidKey1.hashCode());
        assertNotEquals(immutableBidKey1, immutableBidKey3);

        mutableBidKey1.invertBidType();
        assertNotEquals(immutableBidKey1, mutableBidKey1);
    }
}
