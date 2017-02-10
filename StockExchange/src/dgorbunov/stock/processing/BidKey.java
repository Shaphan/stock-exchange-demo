package dgorbunov.stock.processing;

import dgorbunov.stock.domain.BidType;
import dgorbunov.stock.domain.Share;
import org.jetbrains.annotations.NotNull;

/**
 * Интерфейс ключа для матчинга заявок.
 */
public interface BidKey {
    /**
     * Возвращает акцию заявки.
     */
    @NotNull
    Share getShare();

    /**
     * Возвращает тип заявки (покупка/продажа).
     */
    @NotNull
    BidType getBidType();

    /**
     * Возвращает цену за 1 бумагу.
     */
    int getPrice();

    /**
     * Возвращает количество покупаемых или продаваемых ценных бумаг.
     */
    int getQuantity();
}
