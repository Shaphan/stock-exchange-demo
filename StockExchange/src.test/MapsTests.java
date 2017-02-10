import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * Тест-обоснование оптимизации с мутабельным ключом для поиска и удаления
 * и иммутабельным ключом для добавления.
 * При {@link #times} = 1 работают примерно одинаково
 * (возможно, с мутабельным ключом даже чуть медленнее, на доли процента),
 * при {@link #times} = 2 мутабельный ключ для поиска уже дает преимущество,
 * и далее оно нарастает с увеличением количества проходов.
 * Матчинг заявок соответствует ситуации {@link #times} >= 2.
 */
@Ignore
public class MapsTests {
    private final int times = 3;
    private int[][] numbers = createNumbers(10000000 / times); // чтобы время теста было примерно одинаковое

    @Test
    public void test1MapWithMutableKey() {
        MutableKey mutableKey = new MutableKey();
        Map<KeyBase, Integer> map = new HashMap<>();
        for (int j = 0; j < times; j++) {
            for (int i = 0; i < numbers.length; i++) {
                int num1 = numbers[i][0];
                int num2 = numbers[i][1];
                mutableKey.update(num1, num2);
                Integer value = map.get(mutableKey);
                if (value == null) {
                    ImmutableKey immutableKey = new ImmutableKey(num1, num2);
                    map.put(immutableKey, i);
                } else {
                    map.remove(mutableKey);
                }
            }
        }
        if (times %2 == 0) {
            Assert.assertEquals(0, map.keySet().size());
        } else {
            Assert.assertEquals(numbers.length, map.keySet().size());
        }
    }

    @Test
    public void test2MapWithImmutableKey() {
        Map<Object, Integer> map = new HashMap<>();
        for (int j = 0; j < times; j++) {
            for (int i = 0; i < numbers.length; i++) {
                ImmutableKey key = new ImmutableKey(numbers[i][0], numbers[i][1]);
                Integer value = map.get(key);
                if (value == null) {
                    map.put(key, i);
                } else {
                    map.remove(key);
                }
            }
        }
        if (times %2 == 0) {
            Assert.assertEquals(0, map.keySet().size());
        } else {
            Assert.assertEquals(numbers.length, map.keySet().size());
        }
    }

    private static int[][] createNumbers(int number) {
        int[][] randoms = new int[number][];
        Random rnd = new Random();
        for (int i = 0; i < randoms.length; i++) {
            randoms[i] = new int[2];
//            numbers[i][0] = i;
//            numbers[i][0] = - i * 2;
            randoms[i][0] = rnd.nextInt();
            randoms[i][1] = rnd.nextInt();
        }
        return randoms;
    }

    private static abstract class KeyBase {
        protected int f1;
        protected int f2;

        protected KeyBase() {
        }

        protected KeyBase(int f1, int f2) {
            this.f1 = f1;
            this.f2 = f2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof KeyBase)) return false;
            KeyBase keyBase = (KeyBase) o;
            return f1 == keyBase.f1 &&
                    f2 == keyBase.f2;
        }

        @Override
        public int hashCode() {
            return Objects.hash(f1, f2);
        }
    }

    private final static class ImmutableKey extends KeyBase {
        ImmutableKey(int f1, int f2) {
            super(f1, f2);
        }
    }

    private static class MutableKey extends KeyBase {
        void update(int f1, int f2) {
            this.f1 = f1;
            this.f2 = f2;
        }
    }
}
