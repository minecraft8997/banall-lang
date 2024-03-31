package ru.deewend.banalllang;

import ru.deewend.banalllang.funcresult.BanAllEmptyResult;
import ru.deewend.banalllang.funcresult.BanAllException;
import ru.deewend.banalllang.funcresult.BanAllFunctionResult;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class StandardBanAllFunctions {
    private static final Object CHAT_SIMULATION_LOCK = new Object();
    private static final Set<String> BANNED = new HashSet<>();
    private static volatile String currentCharacter;
    private static volatile long lastCalledBanAll;

    // TODO: Eventually entirely remove this and autogenerate it via reflections
    private static final String[] STANDARD_FUNCTIONS_ARRAY = {
            "banall",
            "banall_thread",
            "banall_sleep",
            "banall_println",
            "banall_printls",
            "banall_print",
            "throw_banall",
            "return",
            "switch_character",
            "ping_pandito_in_bryce_request_channel",
            "steal_ned_emoji",
            "tempban"
    };

    public static final List<String> FUNCTIONS = Collections.unmodifiableList(Arrays.asList(STANDARD_FUNCTIONS_ARRAY));

    private StandardBanAllFunctions() {}

    public static BanAllFunctionResult banall() {
        synchronized (StandardBanAllFunctions.class) {
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis < lastCalledBanAll + 60_000L) {
                return println(
                        "You must wait " + TimeUnit.MILLISECONDS.toSeconds(
                                (lastCalledBanAll + 60_000L) - currentTimeMillis) +
                        " seconds before using this command."
                );
            }

            double value = Math.random() * 10001.0D;
            DecimalFormatSymbols dfs = new DecimalFormatSymbols();
            dfs.setDecimalSeparator('.');
            DecimalFormat df = new DecimalFormat("0.00", dfs);
            String formatted = df.format(value);
            if (formatted.equals("420.69")) {
                println((currentCharacter == null ? "YOU" : (currentCharacter + " HAS")) +
                        " ACTIVATED THE REAL BANALL (0.01% CHANCE)." + System.lineSeparator() +
                        "THE INTERPRETER WILL BE TERMINATED & TEMPBANNED IN 3 SECONDS.");

                sleep(3000L);

                BanAllLangInterpreter.BAN("1d", "1d BANALL" +
                        (currentCharacter == null ? "" : (" by " + currentCharacter)));
                System.exit(0);

                return new BanAllEmptyResult();
            } else {
                String message = "Real BanAll chance calculated: " + formatted + ". Needed: 420.69";
                if (currentCharacter != null) message = currentCharacter + ": " + message;

                lastCalledBanAll = System.currentTimeMillis();

                return println(message);
            }
        }
    }

    public static BanAllFunctionResult sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            return new BanAllException("banall.external.InterruptedException", "oops");
        }

        return new BanAllEmptyResult();
    }

    public static BanAllFunctionResult println(String message) {
        return print(message + System.lineSeparator());
    }

    public static BanAllFunctionResult printls() {
        return print(System.lineSeparator());
    }

    public static BanAllFunctionResult print(String message) {
        System.out.print(message);

        return new BanAllEmptyResult();
    }

    public static BanAllFunctionResult switchCharacter(String character) {
        synchronized (StandardBanAllFunctions.class) {
            currentCharacter = character;
        }
        println("[Switched to \"" + character + "\"]");

        return new BanAllEmptyResult();
    }

    public static BanAllFunctionResult pingPanditoInBryceRequestChannel() {
        String currentCharacter;
        synchronized (StandardBanAllFunctions.class) {
            currentCharacter = StandardBanAllFunctions.currentCharacter;
        }
        if (currentCharacter == null) {
            return new BanAllException(
                    "banall.runtime.CharacterNotSetError", "currentCharacter == null"
            );
        }

        if (currentCharacter.equals("Panda")) {
            return new BanAllEmptyResult();
        }
        if (currentCharacter.equals("toby")) {
            synchronized (CHAT_SIMULATION_LOCK) {
                print("\rPandito is typing.");
                sleep(500L);
                print("\rPandito is typing..");
                sleep(500L);
                print("\rPandito is typing...");
                sleep(500L);
                print("\rPandito is typing.");
                sleep(500L);
                print("\rPandito is typing..");
                sleep(500L);
                print("\rPandito is typing...");
                sleep(500L);

                return println("\rPandito: please stop breaking my channel rules.");
            }
        }

        synchronized (StandardBanAllFunctions.class) {
            if (BANNED.contains(currentCharacter)) {
                return new BanAllException(
                        "banall.runtime.BAN",
                        "character \"" + currentCharacter + "\" is banned from #bryce-request channel"
                );
            }

            BANNED.add(currentCharacter);
            return println("Character \"" +
                    currentCharacter + "\" is now banned from #bryce-request channel :(");
        }
    }

    public static BanAllFunctionResult stealNEDEmoji() {
        String currentCharacter;
        synchronized (StandardBanAllFunctions.class) {
            currentCharacter = StandardBanAllFunctions.currentCharacter;
        }
        if (currentCharacter == null) {
            return new BanAllException(
                    "banall.runtime.CharacterNotSetError", "currentCharacter == null"
            );
        }
        if (currentCharacter.equals("Panda")) {
            synchronized (CHAT_SIMULATION_LOCK) {
                sleep(500L);
                print("\rPlayerZ is typing.");
                sleep(500L);
                print("\rPlayerZ is typing..");
                sleep(500L);
                print("\rPlayerZ is typing...");
                sleep(500L);
                print("\rPlayerZ is typing.");
                sleep(500L);
                print("\rPlayerZ is typing..");
                sleep(500L);
                print("\rPlayerZ is typing...");

                println("\rPlayerZ: STOP THIEFTING PANDA");
                sleep(1000L);

                print("\r123DMWM is typing.");
                sleep(500L);
                print("\r123DMWM is typing..");
                sleep(500L);
                print("\r123DMWM is typing...");
                sleep(500L);
                print("\r123DMWM is typing.");
                sleep(500L);
                print("\r123DMWM is typing..");
                sleep(500L);
                print("\r123DMWM is typing...");
                sleep(500L);
                print("\r123DMWM is typing.");
                sleep(500L);

                println("\r123DMWM: I spent like 5 minutes " +
                        "making that in paint.net, my minutely rate is $500");
                sleep(2000L);

                print("\rPanda is typing.");
                sleep(500L);
                print("\rPanda is typing..");
                sleep(500L);
                print("\rPanda is typing...");
                sleep(500L);
                print("\rPanda is typing.");
                sleep(500L);
                print("\rPanda is typing..");
                sleep(500L);
                print("\rPanda is typing...");
                sleep(500L);
                print("\rPanda is typing.");
                sleep(500L);
                print("\rPanda is typing.");
                sleep(500L);
                print("\rPanda is typing..");
                sleep(500L);
                print("\rPanda is typing...");
                sleep(500L);
                print("\rPanda is typing.");
                sleep(500L);
                print("\rPanda is typing..");
                sleep(500L);
                print("\rPanda is typing...");
                sleep(500L);
                print("\rPanda is typing.");
                sleep(500L);

                return println("\rPanda: I would like to purchase your assets at a rate of $0.001/year");
            }
        }

        return new BanAllException(
                "banall.runtime.CharacterNotPandaError",
                "you cannot call this function if you are not Panda"
        );
    }
}
