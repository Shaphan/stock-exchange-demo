package dgorbunov.stock.domain;

import org.jetbrains.annotations.NotNull;

/**
 * Заявка на фондовой бирже.
 */
public final class Bid {
    /**
     * Трейдер (клиент биржи).
     */
    @NotNull
    private final Trader trader;

    /**
     * Акция.
     */
    @NotNull
    private final Share share;

    /**
     * Тип сделки (покупка, продажа).
     */
    @NotNull
    private final BidType bidType;

    /**
     * Цена за единицу ценной бумаги.
     */
    private final int price;

    /**
     * Количество.
     */
    private final int quantity;

    /**
     * Сумма заявки.
     */
    private final int sum;

    public Bid(@NotNull Trader trader, @NotNull Share share, @NotNull BidType bidType, int price, int quantity) {
        // Цена и количество должны быть больше нуля.
        if (price <= 0) {
            throw new IllegalArgumentException("price");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity");
        }

        this.trader = trader;
        this.share = share;
        this.bidType = bidType;
        this.price = price;
        this.quantity = quantity;
        this.sum = Math.multiplyExact(price, quantity);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Bid{");
        sb.append("trader=").append(trader);
        sb.append(", share=").append(share);
        sb.append(", bidType=").append(bidType);
        sb.append(", price=").append(price);
        sb.append(", quantity=").append(quantity);
        sb.append(", sum=").append(sum);
        sb.append('}');
        return sb.toString();
    }

    @NotNull
    public Trader getTrader() {
        return trader;
    }

    @NotNull
    public Share getShare() {
        return share;
    }

    @NotNull
    public BidType getBidType() {
        return bidType;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getSum() {
        return sum;
    }
}
