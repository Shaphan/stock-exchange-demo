package dgorbunov.stock.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Юнит-тесты класса {@link Share}.
 */
public class ShareTests {
    @Test
    public void equalsAndHashCodeTest() {
        Share share1A = new Share(1, "A");
        Share share1B = new Share(1, "B");
        Share share2A = new Share(2, "A");

        assertEquals(share1A, share1B);
        assertEquals(share1A.hashCode(), share1B.hashCode());
        assertNotEquals(share1A, share2A);
    }
}
