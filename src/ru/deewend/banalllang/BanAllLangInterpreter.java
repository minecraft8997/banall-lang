package ru.deewend.banalllang;

import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import ru.deewend.banalllang.funcresult.BanAllEmptyResult;
import ru.deewend.banalllang.funcresult.BanAllException;
import ru.deewend.banalllang.funcresult.BanAllFunctionResult;
import ru.deewend.banalllang.funcresult.BanAllResultTempban;
import ru.deewend.banalllang.funcresult.BanAllReturnStatement;

public class BanAllLangInterpreter {
    public static final String VERSION = "0.1.0a";

    private final List<String> lines;
    private int currentLineNumber;
    private List<BanAllFunction> functionList;
    private final List<Thread> threadList;
    private static File root;
    private boolean importedBanall;

    public BanAllLangInterpreter(List<String> lines) {
        this.lines = Collections.unmodifiableList(new ArrayList<>(lines));
        this.threadList = new ArrayList<>();
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java -jar banall-interpreter_" + VERSION + ".jar <filename>");

            return;
        }
        if (checkBAN()) return;

        List<String> lines = new ArrayList<>();

        root = new File(args[0]);
        
        try (BufferedReader reader = new BufferedReader(new FileReader(root))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Failed to read " + root + " file");
            return;
        }

        int status;
        try {
            status = new BanAllLangInterpreter(lines).evaluate(false);
        } catch (Throwable t) {
            System.err.println("banall.external.SomethingWentWrongError: " +
                    "something went wrong :(");

            BAN("1w", "just because something went wrong");
            return;
        }
        if (status == -1) BAN("1w", "parse error");
    }

    private int evaluate(Boolean dontStart) {
        functionList = new ArrayList<>();
        BanAllFunction currentFunction = null;

        for (int i = 0; i < lines.size(); i++) {
            currentLineNumber = i + 1;
            StringTokenizer tokenizer = new QuotedStringTokenizer(lines.get(i), "(, );");

            if (!tokenizer.hasMoreTokens()) continue;

            String token = tokenizer.nextToken();
            if (token.startsWith("//")) continue;

            if (token.equals("#include")) {
                if (!tokenizer.hasMoreTokens()) {
                    System.err.println(ln() +
                            "banall.lang.ParseError: missing name of package to include");

                    return -1;
                }

                String nextToken = tokenizer.nextToken();

                if (tokenizer.hasMoreTokens()) {
                    System.err.println(ln() + "banall.lang.ParseError: invalid statement");
                    return -1;
                }
                
                String name = nextToken
                    .replace("<", "")
                    .replace(">", "")
                    .replaceAll("\\.(?!ban)", "/");

                // TODO: Please help (1/4)
                if (nextToken.equals("<banall.ban>")) {
                    if (importedBanall) {
                        System.err.println(ln() +
                                "banall.lang.ParseError: this package is already imported!");
                        return -1;
                    }

                    importedBanall = true;
                    continue;
                }

                if(!name.endsWith(".ban")) name += ".ban";

                Path path = Paths.get(root.getParentFile().getAbsolutePath(), name);

                List<String> fileLines = new ArrayList<>();
                                if (tokenizer.hasMoreTokens()) {
                    System.err.println(ln() + "banall.lang.ParseError: invalid statement");

                    return -1;
                }
                
                try (BufferedReader reader = new BufferedReader(new FileReader(path.normalize().toString()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        fileLines.add(line);
                    }
                } catch (IOException e) {
                    return -1;
                }

                try {
                    BanAllLangInterpreter interperter = new BanAllLangInterpreter(fileLines);

                    interperter.evaluate(true);
                    
                    this.functionList.addAll(interperter.functionList);
                    this.threadList.addAll(interperter.threadList);
                } catch (Throwable t) {
                    return -1;
                }
                
                continue;
            }

            if (token.equals("void")) {
                if (!tokenizer.hasMoreTokens()) {
                    System.err.println(ln() + "banall.lang.ParseError: invalid statement");

                    return -1;
                }

                String name = tokenizer.nextToken();
                if (!tokenizer.hasMoreTokens()) {
                    System.err.println(ln() + "banall.lang.ParseError: invalid statement");

                    return -1;
                }

                String next = tokenizer.nextToken();
                if (!next.equals("{")) {
                    System.err.println(ln() + "banall.lang.ParseError: invalid statement");

                    return -1;
                }

                if (currentFunction != null) {
                    System.err.println(ln() + "banall.lang.ParseError: " +
                            "inner functions are not supported");

                    return -1;
                }
                // TODO: Please help (2/4)

                if(StandardBanAllFunctions.functions.contains(name) && importedBanall) {
                    System.err.println(ln() + "banall.lang.ParseError: " +
                    "function \"" + name + "\" is already defined " +
                    "(it comes from the standard library)");

                    return -1;
                }
            
                for (BanAllFunction function : functionList) {
                    if (function.getName().equals(name)) {
                        System.err.println(ln() + "banall.lang.ParseError: " +
                                "function \"" + name + "\" is already defined");

                        return -1;
                    }
                }

                if (tokenizer.hasMoreTokens()) {
                    System.err.println(ln() + "banall.lang.ParseError: invalid statement");

                    return -1;
                }

                currentFunction = new BanAllFunction(name);

                continue;
            }

            if (currentFunction == null) {
                System.err.println(ln() + "banall.lang.ParseError: " +
                        "found a statement which doesn't belong to any function");

                return -1;
            }
            if (token.equals("}")) {
                if (tokenizer.hasMoreTokens()) {
                    System.err.println(ln() + "banall.lang.ParseError: invalid statement");

                    return -1;
                }

                currentFunction.finishConstructing();
                functionList.add(currentFunction);
                currentFunction = null;

                continue;
            }

            switch (token) {
                case "banall": {
                    currentFunction.addLine(new BanAllLine(currentLineNumber) {
                        @Override
                        public BanAllFunctionResult executeCode() {
                            return StandardBanAllFunctions.banall();
                        }
                    });

                    break;
                }
                case "banall_thread": {
                    if (!tokenizer.hasMoreTokens()) {
                        System.err.println(ln() + "banall.lang.ParseError: too few arguments");

                        return -1;
                    }
                    String functionName = tokenizer.nextToken();

                    if (!tokenizer.hasMoreTokens()) {
                        System.err.println(ln() + "banall.lang.ParseError: too few arguments");

                        return -1;
                    }
                    String threadName = tokenizer.nextToken();

                    currentFunction.addLine(new BanAllLine(currentLineNumber) {
                        @Override
                        public BanAllFunctionResult executeCode() {
                            return invokeFunction(functionName, true, threadName);
                        }
                    });

                    break;
                }
                case "banall_sleep": {
                    if (!tokenizer.hasMoreTokens()) {
                        System.err.println(ln() + "banall.lang.ParseError: too few arguments");

                        return -1;
                    }
                    String time = tokenizer.nextToken();
                    long milliseconds;
                    try {
                        milliseconds = Long.parseLong(time);
                    } catch (NumberFormatException e) {
                        System.err.println(ln() + "banall.lang.ParseError: not an integer");

                        return -1;
                    }

                    currentFunction.addLine(new BanAllLine(currentLineNumber) {
                        @Override
                        public BanAllFunctionResult executeCode() {
                            return StandardBanAllFunctions.sleep(milliseconds);
                        }
                    });

                    break;
                }
                case "banall_println": {
                    if (!tokenizer.hasMoreTokens()) {
                        System.err.println(ln() + "banall.lang.ParseError: too few arguments");

                        return -1;
                    }
                    String message = tokenizer.nextToken();

                    currentFunction.addLine(new BanAllLine(currentLineNumber) {
                        @Override
                        public BanAllFunctionResult executeCode() {
                            return StandardBanAllFunctions.println(message);
                        }
                    });

                    break;
                }
                case "banall_printls": {
                    currentFunction.addLine(new BanAllLine(currentLineNumber) {
                        @Override
                        public BanAllFunctionResult executeCode() {
                            return StandardBanAllFunctions.printls();
                        }
                    });

                    break;
                }
                case "banall_print": {
                    if (!tokenizer.hasMoreTokens()) {
                        System.err.println(ln() + "banall.lang.ParseError: too few arguments");

                        return -1;
                    }
                    String message = tokenizer.nextToken();

                    currentFunction.addLine(new BanAllLine(currentLineNumber) {
                        @Override
                        public BanAllFunctionResult executeCode() {
                            return StandardBanAllFunctions.print(message);
                        }
                    });

                    break;
                }
                case "throw_banall": {
                    if (!tokenizer.hasMoreTokens()) {
                        System.err.println(ln() + "banall.lang.ParseError: too few arguments");

                        return -1;
                    }
                    String clazz = tokenizer.nextToken();

                    if (!tokenizer.hasMoreTokens()) {
                        System.err.println(ln() + "banall.lang.ParseError: too few arguments");

                        return -1;
                    }
                    String message = tokenizer.nextToken();

                    currentFunction.addLine(new BanAllLine(currentLineNumber) {
                        @Override
                        public BanAllFunctionResult executeCode() {
                            return new BanAllException(clazz, message);
                        }
                    });

                    break;
                }
                case "return": {
                    if (!tokenizer.hasMoreTokens()) {
                        currentFunction.addLine(new BanAllLine(currentLineNumber) {
                            @Override
                            public BanAllFunctionResult executeCode() {
                                return new BanAllReturnStatement(new BanAllEmptyResult());
                            }
                        });

                        break;
                    }
                    String what = tokenizer.nextToken();
                    if (!what.equals("tempban")) {
                        System.err.println(ln() + "banall.lang.ParseError: " +
                                "you can either return nothing or a tempban");

                        return -1;
                    }
                    if (!tokenizer.hasMoreTokens()) {
                        System.err.println(ln() + "banall.lang.ParseError: " +
                                "tempban duration is not specified");

                        return -1;
                    }
                    String duration = tokenizer.nextToken();

                    currentFunction.addLine(new BanAllLine(currentLineNumber) {
                        @Override
                        public BanAllFunctionResult executeCode() {
                            return new BanAllReturnStatement(new BanAllResultTempban(duration));
                        }
                    });

                    break;
                }
                case "switch_character": {
                    if (!tokenizer.hasMoreTokens()) {
                        System.err.println(ln() + "banall.lang.ParseError: too few arguments");

                        return -1;
                    }
                    String character = tokenizer.nextToken();

                    currentFunction.addLine(new BanAllLine(currentLineNumber) {
                        @Override
                        public BanAllFunctionResult executeCode() {
                            return StandardBanAllFunctions.switchCharacter(character);
                        }
                    });

                    break;
                }
                case "ping_pandito_in_bryce_request_channel": {
                    currentFunction.addLine(new BanAllLine(currentLineNumber) {
                        @Override
                        public BanAllFunctionResult executeCode() {
                            return StandardBanAllFunctions.pingPanditoInBryceRequestChannel();
                        }
                    });

                    break;
                }
                case "steal_ned_emoji": {
                    currentFunction.addLine(new BanAllLine(currentLineNumber) {
                        @Override
                        public BanAllFunctionResult executeCode() {
                            return StandardBanAllFunctions.stealNEDEmoji();
                        }
                    });

                    break;
                }
                default: {
                    currentFunction.addLine(new BanAllLine(currentLineNumber) {
                        @Override
                        public BanAllFunctionResult executeCode() {
                            return invokeFunction(token, false, null);
                        }
                    });
                }
            }

            if (tokenizer.hasMoreTokens()) {
                System.err.println(ln() + "banall.lang.ParseError: invalid statement");

                return -1;
            }
        }

        // TODO: Please help (3/4)
        if (!importedBanall) {
            System.err.println(ln() + "");

            return -1;
        }


        if(!dontStart) {
            BanAllFunctionResult result = invokeFunction(
                    "main", false, null
            );

            waitUntilAllThreadsFinish();

            if (!(result instanceof BanAllResultTempban)) {
                BAN("1w", "did NOT return a tempban in main() method");
            } else {
                BAN(
                        ((BanAllResultTempban) result).getDuration(),
                        "this duration was specified in return statement"
                );
            }
        }
        return 0;
    }

    private static boolean checkBAN() {
        File file = new File("banall.dat");
        if (!file.exists()) return false;

        long end;
        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
            if (dis.readLong() != 0L) throw new IOException();
            end = dis.readLong() ^ 824L;
            if (dis.readInt() != Long.hashCode(end)) throw new IOException();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("banall.lang.FailedToCheckInterpreterBanishmentError");

            return true;
        }

        if (System.currentTimeMillis() < end) {
            System.out.println("[You are banned from using the interpreter until " + new Date(end) + "]");

            return true;
        }

        return false;
    }

    /* package-private */ static void BAN(String duration, String reason) {
        File file = new File("banall.dat");
        try {
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();
        } catch (IOException e) {
            return;
        }

        long parsed = parseDuration(duration);
        if (parsed == -1L) {
            parsed = parseDuration("1w");
            reason = "invalid duration specified";
        }
        long end = System.currentTimeMillis() + parsed;
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(file))) {
            dos.writeLong(0L);
            dos.writeLong(end ^ 824L);
            dos.writeInt(Long.hashCode(end));
        } catch (IOException ignored) {
        }

        System.out.println("[You are banned from using the interpreter " +
                "for another " + duration + " (until " + new Date(end) + "). " +
                "Reason: " + reason + ". See you later!]");
    }

    private static long parseDuration(String duration) {
        if (duration.length() != 2) {
            return -1L;
        }
        char digit = duration.charAt(0);
        char unit = duration.charAt(1);
        if (
                (digit < '1' || digit > '9') ||
                (unit != 'm' && unit != 's' && unit != 'h' && unit != 'd' && unit != 'w')
        ) {
            return -1L;
        }

        long time = digit - '0';
        switch (unit) {
            case 's':
                time *= 1000;
                break;
            case 'm':
                time *= 60_000;
                break;
            case 'h':
                time *= 3600_000;
                break;
            case 'd':
                time *= 86400_000;
                break;
            case 'w':
                time *= 604800_000;
        }

        return time;
    }
    
    private BanAllFunctionResult invokeFunction(String name, boolean inNewThread, String threadName) {
        // TODO: Please help (4/4)

        if (!importedBanall) {
            return new BanAllException(
                    "banall.runtime.BanAllPackageIsNotIncludedError",
                    "missing \"#include <banall.ban>\" directive"
            );
        }

        BanAllFunction function = lookupFunction(name);
        if (function == null) {
            return new BanAllException(
                    "banall.runtime.NoSuchMethodError", "function \"" + name + "\" is not defined"
            );
        }
        
        if (inNewThread) {
            Thread thread = new Thread(function::invoke);
            synchronized (threadList) {
                threadList.add(thread);
            }
            thread.setName(threadName);
            thread.start();
            
            return new BanAllEmptyResult();
        }

        return function.invoke();
    }

    private void waitUntilAllThreadsFinish() {
        int i = 0;
        try {
            while (i < threadListSize()) getThread(i++).join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private int threadListSize() {
        synchronized (threadList) {
            return threadList.size();
        }
    }

    private Thread getThread(int i) {
        synchronized (threadList) {
            return threadList.get(i);
        }
    }

    private synchronized BanAllFunction lookupFunction(String name) {
        for (BanAllFunction function : functionList) {
            if (function.getName().equals(name))
                return function;
        }

        return null;
    }

    private String ln() {
        return "[Line " + currentLineNumber + "] ";
    }
}
