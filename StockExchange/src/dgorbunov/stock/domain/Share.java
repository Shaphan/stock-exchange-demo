package dgorbunov.stock.domain;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Акция.
 */
public final class Share {

    /**
     * Уникальный в пределах системы идентификатор акции.
     * Введен для улучшения производительности {@link #hashCode} и {@link #equals}.
     */
    private int identity;

    /**
     * Наименование акции.
     */
    @NotNull
    private String name;

    /**
     * Конструктор.
     *
     * @param name Наименование акции.
     */
    public Share(int identity, @NotNull String name) {
        this.identity = identity;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Share)) return false;
        Share share = (Share) o;
        return identity == share.identity;
    }

    @Override
    public int hashCode() {
        return identity;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Share{");
        sb.append("identity=").append(identity);
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
    @NotNull
    public String getName() {
        return name;
    }

}
