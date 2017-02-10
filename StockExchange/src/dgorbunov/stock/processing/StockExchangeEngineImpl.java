package dgorbunov.stock.processing;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import dgorbunov.stock.domain.Bid;
import dgorbunov.stock.domain.Trader;
import dgorbunov.stock.domain.TraderBalance;
import net.jcip.annotations.NotThreadSafe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

/**
 * Движок биржи.
 */
@NotThreadSafe
public class StockExchangeEngineImpl implements StockExchangeEngine {

    @NotNull
    private final Map<Trader, TraderBalance> traderBalances;

    /**
     * Словарь текущих заявок.
     * <p>
     * Ключом является акция + тип заявки + цена за 1 акцию + количество.
     * Матчинг, соответственно, осуществляем по полному совпадению
     * акции, типа заявки (b + s), количества и цены за 1 акцию.
     * Таким образом, в ситуации
     * C1  b   A   7   12
     * C2  s   A   7   6
     * C2  s   A   7   6
     * - НЕТ сматченных заявок.
     * <p>
     * В реальной жизни обработка заявок может идти как по FIFO (более вероятно),
     * так и по LIFO (менее вероятно), но точно не в произвольном порядке.
     * В условии задачи это явно не указано: сказано лишь,
     * что файл orders.txt содержит заявки в хронологическом порядке.
     * Будем считать, что это означает указание осуществлять обработку по FIFO.
     * <p>
     * Думаю, для демо-задачи, при условии, что мы не обладаем конкретными сведениями о распределении
     * значений (заявок) по ключам (т.е. не знаем, например, сколько трейдер может подать одинаковых заявок,
     * как часто происходит успешный матчинг заявок и т.п.), вполне адекватно задействовать
     * стандартную реализацию {@link ArrayListMultimap} с настройками по умолчанию.
     */
    @NotNull
    private final ListMultimap<BidKey, Bid> bids = ArrayListMultimap.create();

    /**
     * Конструктор.
     *
     * @param traderBalances Исходные балансы по клиентам биржи.
     *                       Для получения результирующих балансов используйте {@link #getTraderBalancesModifiable}.
     */
    public StockExchangeEngineImpl(@NotNull Map<Trader, TraderBalance> traderBalances) {
        // Копируем себе переданный map.
        this.traderBalances = new HashMap<>(traderBalances.size(), 1.0f);
        for (Map.Entry<Trader, TraderBalance> entry : traderBalances.entrySet()) {
            this.traderBalances.put(entry.getKey(), entry.getValue().copy());
        }
    }

    @Override
    public void processBids(@NotNull Supplier<Bid> bidSupplier) {
        Bid bid = bidSupplier.get();

        while (bid != null) {
            BidKey currentSearchBidKey = new BidKeyImpl(bid.getShare(), bid.getBidType().invert(),
                    bid.getPrice(), bid.getQuantity());

            Trader trader = bid.getTrader();
            Optional<Bid> matchingBidOptional = bids.get(currentSearchBidKey).stream()
                    .filter(b -> !b.getTrader().equals(trader))
                    .findFirst();

            if (matchingBidOptional.isPresent()) {
                Bid matchingBid = matchingBidOptional.get();

                TraderBalance traderBalance = traderBalances.get(trader);
                if (traderBalance == null) {
                    throw new RuntimeException(String.format("Unknown trader %s in bid %s", trader, bid));
                }
                TraderBalance matchingTraderBalance = traderBalances.get(matchingBid.getTrader());
                if (matchingTraderBalance == null) {
                    throw new RuntimeException(String.format("Unknown trader %s in bid %s", trader, matchingBid));
                }

                traderBalance.applyBid(bid);
                matchingTraderBalance.applyBid(matchingBid);

                bids.remove(currentSearchBidKey, matchingBid);
            } else {
                BidKey newBidKey = new BidKeyImpl(bid);
                bids.put(newBidKey, bid);
            }
            bid = bidSupplier.get();
        }
    }

    @Override
    @NotNull
    public Map<Trader, TraderBalance> getTraderBalancesModifiable() {
        return traderBalances;
    }
}
