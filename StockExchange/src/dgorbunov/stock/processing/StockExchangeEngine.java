package dgorbunov.stock.processing;

import dgorbunov.stock.domain.Bid;
import dgorbunov.stock.domain.Trader;
import dgorbunov.stock.domain.TraderBalance;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Интерфейс движка фондовой биржи.
 * Использование:
 * <ol>
 * <li>Для обработки заявок вызовите {@link #processBids}.</li>
 * <li>Для получения результата обработки заявок вызовите {@link #getTraderBalancesModifiable}.</li>
 * </ol>
 */
public interface StockExchangeEngine {
    void processBids(@NotNull Supplier<Bid> bidSupplier);

    @NotNull Map<Trader, TraderBalance> getTraderBalancesModifiable();
}
