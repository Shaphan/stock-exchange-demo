package dgorbunov.stock.app;

import dgorbunov.stock.domain.Share;
import dgorbunov.stock.domain.Trader;
import dgorbunov.stock.domain.TraderBalance;
import dgorbunov.stock.io.BalancesDeserializer;
import dgorbunov.stock.io.BalancesSerializer;
import dgorbunov.stock.io.BidsSupplier;
import dgorbunov.stock.processing.StockExchangeEngine;
import dgorbunov.stock.processing.StockExchangeEngineImpl;
import org.apache.commons.lang3.time.StopWatch;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {

    private static final String USAGE_TEXT = String.format("Demo Stock Exchange App for Sbertech interview.%n" +
            "(C) 2017, Dmitry Gorbunov.%n" +
            "d.gorbunov@outlook.com%n" +
            "%n" +
            "Usage:%n" +
            "java -jar StockExchangeApp.jar clients.txt orders.txt result.txt");

    private static final List<Share> SHARES = Arrays.asList(new Share(1, "A"),
            new Share(2, "B"), new Share(3, "C"), new Share(4, "D"));

    public static void main(String[] args) {
        try {
            System.out.println(USAGE_TEXT);

            if (args == null || args.length != 3) {
                System.err.println("Incorrect arguments. See USAGE!");
                return;
            }

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            Map<Trader, TraderBalance> traderBalances;
            try (FileInputStream inputStream = new FileInputStream(args[0])) {
                try (InputStreamReader reader = new InputStreamReader(inputStream, Charset.forName("UTF-8"))
                ) {
                    BalancesDeserializer deserializer = new BalancesDeserializer(SHARES);
                    traderBalances = deserializer.deserialize(reader);
                }
            }
            StockExchangeEngine engine = new StockExchangeEngineImpl(traderBalances);
            try (FileInputStream inputStream = new FileInputStream(args[1])) {
                try (InputStreamReader reader = new InputStreamReader(inputStream, Charset.forName("UTF-8"))) {
                    try (BidsSupplier bidsSupplier = new BidsSupplier(reader, traderBalances.keySet(), SHARES)) {
                        engine.processBids(bidsSupplier);
                    }
                }
            }
            traderBalances = engine.getTraderBalancesModifiable();
            try (FileOutputStream outputStream = new FileOutputStream(args[2])) {
                try (OutputStreamWriter writer = new OutputStreamWriter(outputStream, Charset.forName("UTF-8"))) {
                    BalancesSerializer serializer = new BalancesSerializer(SHARES);
                    serializer.serialize(traderBalances, writer);
                }
            }

            stopWatch.stop();
            System.out.println(String.format("%nCompleted in %d ms.", stopWatch.getTime()));
        } catch (Exception e) {
            System.err.println("An error has occurred while running the program.");
            e.printStackTrace();
        }
    }
}
