package dgorbunov.stock.io;

import dgorbunov.stock.domain.Bid;
import dgorbunov.stock.domain.BidType;
import dgorbunov.stock.domain.Share;
import dgorbunov.stock.domain.Trader;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Поставщик заявок.
 * Заявок может быть много, обарабатываются они последовательно, поэтому поставщик читает и возвращает их по одной.
 */
public class BidsSupplier implements Supplier<Bid>, AutoCloseable {

    private static final Pattern LINE_REGEX = Pattern.compile(
            "(\\p{Alnum}+)\t([bs]{1})\t(\\p{Alnum}+)\t([0-9]+)\t([0-9]+).*",
            Pattern.DOTALL);

    @NotNull
    private final BufferedReader reader;

    private final Map<String, Trader> traders;

    private final Map<String, Share> shares;

    public BidsSupplier(
            @NotNull Reader reader,
            @NotNull Collection<Trader> traders,
            @NotNull Collection<Share> shares
    ) {
        this.reader = new BufferedReader(reader);
        this.traders = traders.stream()
                .collect(Collectors.toMap(Trader::getName, t -> t));
        this.shares = shares.stream()
                .collect(Collectors.toMap(Share::getName, s -> s));
    }

    @Override
    public Bid get() {

        String line;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException("An exception has occurred while reading incoming bids", e);
        }

        if (line == null) {
            return null;
        }

        try {
            Matcher matcher = LINE_REGEX.matcher(line);
            if (!matcher.matches()) {
                throw new RuntimeException(String.format("Line does not match pattern %s", LINE_REGEX));
            }
            String traderName = matcher.group(1);
            Trader trader = traders.get(traderName);

            String bidTypeText = matcher.group(2);
            BidType bidType;
            if ("b".equalsIgnoreCase(bidTypeText)) {
                bidType = BidType.BUY;
            } else if ("s".equalsIgnoreCase(bidTypeText)) {
                bidType = BidType.SELL;
            } else {
                throw new IllegalStateException(String.format("Unknown bid type %s", bidTypeText));
            }

            String shareName = matcher.group(3);
            Share share = shares.get(shareName);

            return new Bid(trader, share, bidType,
                    Integer.parseUnsignedInt(matcher.group(4)),
                    Integer.parseUnsignedInt(matcher.group(5)));
        } catch (Exception e) {
            // В реальной жизни мы бы, конечно, не останавливали обработку заявок полностью.
            // Нераспарсенные заявки логировались бы и куда-нибудь сохранялись,
            // а о наличии ошибок уведомляли бы пользователя.
            throw new RuntimeException(String.format(
                    "An exception has occurred while parsing a bid from line %s", line),
                    e);
        }
    }

    @Override
    public void close() throws Exception {
        reader.close();
    }
}
