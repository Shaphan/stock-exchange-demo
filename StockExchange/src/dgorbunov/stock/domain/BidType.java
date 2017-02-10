package dgorbunov.stock.domain;

/**
 * Типы заявок.
 */
public enum BidType {
    /**
     * Покупка.
     */
    BUY,
    /**
     * Продажа.
     */
    SELL,
    //
    ;

    public BidType invert() {
        BidType result;
        switch (this) {
            case BUY:
                result = BidType.SELL;
                break;
            case SELL:
                result = BidType.BUY;
                break;
            default:
                throw new IllegalStateException(String.format("Unknown bid type: %s", this));
        }
        return result;
    }
}
