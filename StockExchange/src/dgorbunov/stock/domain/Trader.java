package dgorbunov.stock.domain;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Трейдер (клиент биржи).
 */
public final class Trader {

    /**
     * Уникальный в пределах системы идентификатор клиента.
     * Введен, в первую очередь, для улучшения производительности {@link #equals} и {@link #hashCode}.
     */
    private int identity;

    /**
     * Имя (наименование) клиента.
     */
    @NotNull
    private String name;

    /**
     * Конструктор, создает новый экземпляр клиента биржи.
     *
     * @param identity Уникальный в пределах системы идентификатор клиента.
     * @param name     Имя (наименование клиента).
     */
    public Trader(int identity, @NotNull String name) {
        this.identity = identity;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trader)) return false;
        Trader trader = (Trader) o;
        return identity == trader.identity;
    }

    @Override
    public int hashCode() {
        return identity;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Trader{");
        sb.append("identity=").append(identity);
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public int getIdentity() {
        return identity;
    }

    @NotNull
    public String getName() {
        return name;
    }
}
