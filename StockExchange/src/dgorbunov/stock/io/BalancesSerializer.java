package dgorbunov.stock.io;

import dgorbunov.stock.domain.Share;
import dgorbunov.stock.domain.Trader;
import dgorbunov.stock.domain.TraderBalance;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Сериализатор (конечных) балансов клиентов биржи.
 */
public class BalancesSerializer {
    @NotNull
    private final List<Share> shares;

    /**
     * Конструктор, создает новый экземпляр сериализатора.
     *
     * @param shares Акции. Должны быть известны заранее.
     */
    public BalancesSerializer(@NotNull List<Share> shares) {
        this.shares = shares;
    }

    public void serialize(@NotNull Map<Trader, TraderBalance> traderBalances, @NotNull Writer writer) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            Iterator<Map.Entry<Trader, TraderBalance>> iterator = traderBalances.entrySet().stream()
                    // Сохраняем изначальный порядок клиентов, заданный в clients.txt.
                    .sorted(Comparator.comparingInt(entry2 -> entry2.getKey().getIdentity()))
                    .iterator();

            while (iterator.hasNext()) {
                Map.Entry<Trader, TraderBalance> entry = iterator.next();
                Trader trader = entry.getKey();
                TraderBalance traderBalance = entry.getValue();
                try {
                    bufferedWriter.write(trader.getName());
                    bufferedWriter.write('\t');
                    bufferedWriter.write(Integer.toString(traderBalance.getMoney()));
                    for (Share share : shares) {
                        bufferedWriter.write('\t');
                        Integer shareBalance = traderBalance.getShareBalance(share);
                        if (shareBalance == null) {
                            throw new RuntimeException(String.format(
                                    "Balance not specified for share %s and trader %s",
                                    share, trader
                            ));
                        }
                        bufferedWriter.write(shareBalance.toString());
                    }
                    // В clients.txt используется юниксовый перевод строки.
                    // Последняя строка в clients.txt перевод строки не содержит.
                    // Скорее всего, это неважно, но на всякий случай - отличаем
                    // последнюю строку и не ставим послее нее символ окончания строки.
                    if (iterator.hasNext()) {
                        bufferedWriter.write('\n');
                    }
                } catch (IOException e) {
                    throw new RuntimeException(String.format(
                            "An exception has occurred while saving balance for trader %s: %s",
                            trader, traderBalance));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("An exception has occurred while saving balances", e);
        }
    }
}
