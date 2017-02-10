package dgorbunov.stock.processing;

import dgorbunov.stock.domain.Bid;
import dgorbunov.stock.domain.BidType;
import dgorbunov.stock.domain.Share;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Реализация ключа для матчинга заявок.
 */
public final class BidKeyImpl implements BidKey {
    @NotNull
    private final Share share;
    @NotNull
    private final BidType bidType;
    private final int price;
    private final int quantity;

    public BidKeyImpl(@NotNull Share share, @NotNull BidType bidType, int price, int quantity) {
        this.share = share;
        this.bidType = bidType;
        this.price = price;
        this.quantity = quantity;
    }

    public BidKeyImpl(@NotNull Bid bid) {
        this(bid.getShare(), bid.getBidType(), bid.getPrice(), bid.getQuantity());
    }

    @NotNull
    @Override
    public Share getShare() {
        return share;
    }

    @NotNull
    @Override
    public BidType getBidType() {
        return bidType;
    }

    @Override
    public int getPrice() {
        return price;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BidKey)) return false;
        BidKey that = (BidKey) o;
        return getPrice() == that.getPrice() &&
                getQuantity() == that.getQuantity() &&
                Objects.equals(getShare(), that.getShare()) &&
                getBidType() == that.getBidType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getShare(), getBidType(), getPrice(), getQuantity());
    }
}
