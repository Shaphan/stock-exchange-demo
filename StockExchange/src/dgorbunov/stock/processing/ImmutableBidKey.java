package dgorbunov.stock.processing;

import dgorbunov.stock.domain.Bid;
import dgorbunov.stock.domain.BidType;
import dgorbunov.stock.domain.Share;
import org.jetbrains.annotations.NotNull;

/**
 * Реализация неизменяемого ключа заявки {@link BidKey}.
 */
public final class ImmutableBidKey extends AbstractBidKey implements BidKey {
    public ImmutableBidKey(@NotNull Share share, @NotNull BidType bidType, int price, int quantity) {
        super(share, bidType, price, quantity);
    }

    public ImmutableBidKey(@NotNull Bid bid) {
        this(bid.getShare(), bid.getBidType(), bid.getPrice(), bid.getQuantity());
    }
}
