package dgorbunov.stock.common;

import dgorbunov.stock.domain.Share;
import dgorbunov.stock.domain.Trader;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Класс, содержащий основные используемые при тестировании сущности,
 * чтобы не объявлять их многократно: акции и трейдеры.
 */
public class StockTestingPredefines {
    public static final Share ShareA = new Share(1, "A");
    public static final Share ShareB = new Share(2, "B");
    public static final Share ShareC = new Share(3, "C");
    public static final Share ShareD = new Share(4, "D");

    public static final List<Share> Shares = Collections.unmodifiableList(Arrays.asList(ShareA,
            ShareB, ShareC, ShareD));

    public static final Trader Trader1 = new Trader(1, "C1");
    public static final Trader Trader2 = new Trader(2, "C2");
    public static final Trader Trader3 = new Trader(3, "C3");
    public static final Trader Trader4 = new Trader(4, "C4");
    public static final Trader Trader5 = new Trader(5, "C5");
    public static final Trader Trader6 = new Trader(2, "C6");
    public static final Trader Trader7 = new Trader(3, "C7");
    public static final Trader Trader8 = new Trader(4, "C8");
    public static final Trader Trader9 = new Trader(5, "C9");

    public static final List<Trader> Traders = Collections.unmodifiableList(Arrays.asList(Trader1,
            Trader2, Trader3, Trader4, Trader5, Trader6, Trader7, Trader8, Trader9));
}
