package dgorbunov.stock.domain;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Сведения о балансах трейдера:
 * сумма на денежном счету и количество каждой ценной бумаги на счетах ЦБ.
 */
public final class TraderBalance {
    private int money;
    @NotNull
    private final Map<Share, Integer> shareBalances;

    /**
     * Конструктор.
     *
     * @param money Сумма денег на счету трейдера. По условиям задачи может быть отрицательной (задолженность).
     * @param shareBalances Количество каждой из акций у трейдера. По условиям задачи могут быть отрицательными.
     */
    public TraderBalance(int money, @NotNull Map<Share, Integer> shareBalances) {
        this.money = money;
        this.shareBalances = shareBalances;
    }

    /**
     * Исполняет заявку данного трейдера.
     *
     * @param bid Заявка.
     */
    public void applyBid(@NotNull Bid bid) {
        Integer shareBalance = shareBalances.get(bid.getShare());
        if (shareBalance == null) {
            // Баланс должен быть по всем известным акциям.
            throw new RuntimeException(String.format("Unknown share %s in bid %s", bid.getShare(), bid));
        }

        switch (bid.getBidType()) {
            case BUY:
                money = Math.subtractExact(money, bid.getSum());
                shareBalance = Math.addExact(shareBalance, bid.getQuantity());
                break;
            case SELL:
                money = Math.addExact(money, bid.getSum());
                shareBalance = Math.subtractExact(shareBalance, bid.getQuantity());
                break;
            default:
                throw new IllegalStateException(String.format("Unsupported bid type: %s", bid.getBidType()));
        }

        shareBalances.put(bid.getShare(), shareBalance);
    }

    /**
     * Создает копию данного баланса.
     */
    public TraderBalance copy() {
        Map<Share, Integer> shareBalancesCopy = new HashMap<>(shareBalances.size(), 1.0f);
        for(Map.Entry<Share, Integer> entry : shareBalances.entrySet()) {
            shareBalancesCopy.put(entry.getKey(), entry.getValue());
        }
        return  new TraderBalance(money, shareBalancesCopy);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TraderBalance{");
        sb.append("money=").append(money);
        sb.append(", shareBalances=").append(shareBalances);
        sb.append('}');
        return sb.toString();
    }

    public int getMoney() {
        return money;
    }

    @Nullable
    public Integer getShareBalance(@NotNull Share share) {
        return shareBalances.get(share);
    }
}
