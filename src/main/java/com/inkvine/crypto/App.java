package com.inkvine.crypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inkvine.crypto.model.Coin;
import com.inkvine.crypto.rest.CoinMarketCapAPI;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.skb.interfaces.document.TableRowStyle;
import org.apache.commons.lang3.ObjectUtils;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.inkvine.crypto.rest.CoinMarketCapAPI.BASE_URL;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {

        if (args.length < 2 || args[0] == null || args[0].isEmpty()) {
            System.out.println("Please provide a valid wallet json file");
            return;
        }

        if (args.length < 2 || args[1] == null || args[1].isEmpty()) {
            System.out.println("Please provide a valid value for your interval");
            return;
        }


        // read parameters
        File jsonWallet = new File(args[0]);
        int seconds = Integer.parseInt(args[1]);
        boolean verbose = args.length == 3 && args[2] != null && !args[2].isEmpty() && args[2].equals("-v");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        CoinMarketCapAPI coinMarketCapAPI = retrofit.create(CoinMarketCapAPI.class);

        // Read json
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Double> wallet = null;
        try {
            wallet = mapper.readValue(jsonWallet, Map.class);
        } catch (IOException e) {
            System.out.println("Could not read provided json wallet! Check path");
            return;
        }

        Timer timer = new Timer();

        Map<String, Double> finalWallet = wallet;
        Map<String, Double> oldValues = new LinkedHashMap<>();

        TimerTask myTask = new TimerTask() {

            @Override
            public void run() {

                try {

                    AsciiTable at = new AsciiTable();
                    at.getContext().setWidth(130);
                    at.addRule();
                    at.addRow("Coin Name", "Number of", "Value", "Value all", "Change %", "Change Value");

                    Map<String, Double> valueOfWallet = getValueOfWallet(coinMarketCapAPI, finalWallet, verbose);

                    double cumulatedValueOld = oldValues != null && !oldValues.isEmpty() && oldValues.size() > 0 ? oldValues.keySet().stream().mapToDouble(s -> {

                        return oldValues.get(s) * finalWallet.get(s);

                    }).sum() : 0;

                    valueOfWallet.keySet().forEach(s -> {

                        double percentageChange = 0;
                        double difference = 0;

                        if (!oldValues.isEmpty() && oldValues.containsKey(s)) {
                            percentageChange = (valueOfWallet.get(s) - oldValues.get(s)) / oldValues.get(s) * 100;
                            difference = valueOfWallet.get(s) - oldValues.get(s);
                        }

                        at.addRule();
                        at.addRow(s, finalWallet.get(s), valueOfWallet.get(s), valueOfWallet.get(s) * finalWallet.get(s), percentageChange, difference);

                        // copy values for next run
                        oldValues.put(s, valueOfWallet.get(s));

                    });

                    String table = at.render();
                    System.out.println(table);

                    double cumulatedValueNew = 0;
                    try {
                        cumulatedValueNew = (finalWallet.keySet().stream().mapToDouble(s -> {

                            try {
                                return (valueOfWallet.containsKey(s) ? valueOfWallet.get(s) : 0) * finalWallet.get(s);
                            } catch (NullPointerException e) {
                                return 0;
                            }

                        })).sum();

                    } catch (NullPointerException e) {

                    }

                    double walletPercentageChange = ((cumulatedValueNew - cumulatedValueOld) / cumulatedValueOld * 100);
                    double walletDifference = cumulatedValueNew - cumulatedValueOld;

                    System.out.println("\n" + ConsoleColors.BLUE_BOLD_BRIGHT + "New cumulated value of your wallet: " + ((walletPercentageChange >= 0) ? ConsoleColors.GREEN_BOLD_BRIGHT : ConsoleColors.RED_BOLD_BRIGHT) +

                            cumulatedValueNew

                            + ConsoleColors.BLUE_BOLD_BRIGHT + " EUR" + ConsoleColors.BLUE_BOLD_BRIGHT + " --- Old Value of your wallet: " + cumulatedValueOld + ConsoleColors.RESET);
                    System.out.println(ConsoleColors.BLUE_BOLD_BRIGHT
                            + "% Change till last calculation: "
                            + ((walletPercentageChange >= 0) ? ConsoleColors.GREEN_BOLD_BRIGHT : ConsoleColors.RED_BOLD_BRIGHT)
                            + walletPercentageChange + ConsoleColors.BLUE_BOLD_BRIGHT
                            + " --- Value change till last update: " + ((walletPercentageChange >= 0) ? ConsoleColors.GREEN_BOLD_BRIGHT : ConsoleColors.RED_BOLD_BRIGHT)
                            + walletDifference + ConsoleColors.BLUE_BOLD_BRIGHT + " EUR" + ConsoleColors.RESET);


                } catch (Throwable t) {

                    System.out.println("Something went wrong this time :)");

                }
            }
        };

        timer.schedule(myTask, 0, seconds * 1000);

    }

    private static Map<String, Double> getValueOfWallet(CoinMarketCapAPI coinMarketCapAPI, Map<String, Double> wallet, boolean verbose) {

        LinkedHashMap<String, Double> coinValues = new LinkedHashMap<>();


        wallet.keySet().forEach(s -> {

            Call<List<Coin>> eur = coinMarketCapAPI.getCoinById(s, "EUR");
            try {

                Coin coin = eur.execute().body().get(0);
                if (verbose)
                    System.out.println("Retrieved coin from CoinmarktCap API : " + coin);

                coinValues.put(s, coin.getPriceEur());

            } catch (IOException e) {
                System.out.println(String.format("Could not retrieve coin %s. Please check name in your provided JSON wallet!", s));
            }

        });

        return coinValues;
    }
}
