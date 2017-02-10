package dgorbunov.stock.io;

import dgorbunov.stock.domain.Share;
import dgorbunov.stock.domain.Trader;
import dgorbunov.stock.domain.TraderBalance;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Десериализатор начальных балансов клиентов.
 */
public class BalancesDeserializer {
    @NotNull
    private final List<Share> shares;

    @NotNull
    private final Pattern lineRegex;

    /**
     * Конструктор, создает экземпляр десериализатора начальных балансов.
     *
     * @param shares Акции, должны быть известны заранее.
     */
    public BalancesDeserializer(@NotNull List<Share> shares) {
        this.shares = shares;

        StringBuilder sb = new StringBuilder();
        sb.append("(\\p{Alnum}+)\t([0-9]+)");
        for (Share share : shares) {
            sb.append("\t([0-9]+)");
        }
        sb.append(".*");
        lineRegex = Pattern.compile(sb.toString(), Pattern.DOTALL);
    }

    @NotNull
    public Map<Trader, TraderBalance> deserialize(@NotNull Reader reader) {
        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            Stream<String> stream = bufferedReader.lines();
            Object[] lines = stream.toArray();

            // Т.к. нумерацию трейдеров для удобства ведем с единицы, делаем предположение,
            // что количество трейдеров меньше чем Integer.MAX_VALUE.
            // В реальности максимальный размер Java-массива Integer.MAX_VALUE - 5.
            if (lines.length == Integer.MAX_VALUE) {
                throw new RuntimeException(String.format("Too many traders: %d", lines.length));
            }

            // С уникальной хэш-функцией, исключающей коллизии,
            // можем задать начальную емкость = количеству клиентов и коэффициент заполнения, равный единице.
            Map<Trader, TraderBalance> result = new HashMap<>(lines.length, 1.0f);
            int i = 1;
            for (Object line : lines) {
                try {
                    Matcher matcher = lineRegex.matcher((String) line);
                    if (!matcher.matches()) {
                        throw new RuntimeException(String.format("Line does not match pattern %s", lineRegex));
                    }
                    String traderName = matcher.group(1);
                    Trader trader = new Trader(i, traderName);
                    int money = Integer.parseInt(matcher.group(2));

                    Map<Share, Integer> shareBalances = new HashMap<>(shares.size());
                    int groupNum = 3;
                    for (Share share : shares) {
                        shareBalances.put(share, Integer.parseInt(matcher.group(groupNum++)));
                    }

                    TraderBalance traderBalance = new TraderBalance(money, shareBalances);
                    result.put(trader, traderBalance);
                } catch (Exception e) {
                    // В реальной жизни мы бы, скорее всего, не останавливали парсинг целиком,
                    // а собирали бы нераспарсенные строки для последующего уведомления пользователя.
                    // Но в демо-задаче нам хватит и просто внятного исключения,
                    // позволяющего понять, где произошла проблема.
                    throw new RuntimeException(String.format("Unable to parse line %d: %s", i, line), e);
                }
                i++;
            }

            return result;
        } catch (IOException e) {
            throw new RuntimeException("An exception has occurred while reading incoming balances", e);
        }
    }
}
