package dgorbunov.stock.processing;

import dgorbunov.stock.domain.BidType;
import dgorbunov.stock.domain.Share;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Created by dgorbunov on 04.02.2017.
 */
public abstract class AbstractBidKey implements BidKey {
    protected Share share;
    protected BidType bidType;
    protected int price;
    protected int quantity;

    protected AbstractBidKey(Share share, BidType bidType, int price, int quantity) {
        this.share = share;
        this.bidType = bidType;
        this.price = price;
        this.quantity = quantity;
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
