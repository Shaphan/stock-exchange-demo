package dgorbunov.stock.processing;

import dgorbunov.stock.domain.Bid;
import dgorbunov.stock.domain.BidType;
import dgorbunov.stock.domain.Share;
import org.jetbrains.annotations.NotNull;

/**
 * Релизация изменяемого ключа заявки {@link BidKey}.
 */
class MutableBidKey extends AbstractBidKey implements BidKey {
    MutableBidKey(Share share, BidType bidType, int price, int quantity) {
        super(share, bidType, price, quantity);
    }

    void updateToMatchBid(@NotNull Bid bid) {
        share = bid.getShare();
        bidType = bid.getBidType().invert();
        price = bid.getPrice();
        quantity = bid.getQuantity();
    }

    static MutableBidKey createToMatchBid(@NotNull Bid bid) {
        return new MutableBidKey(bid.getShare(),
                bid.getBidType().invert(),
                bid.getPrice(),
                bid.getQuantity());
    }

    public void invertBidType() {
        bidType = bidType.invert();
    }
}
