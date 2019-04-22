package benchmark.SRA;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.Reader;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.commons.cli.*;
import java.io.File;
import java.util.List;

import com.google.common.base.Stopwatch;
import java.util.concurrent.TimeUnit;

public class Runner {

    private static Integer numberOfRuns = 5;
    private static Experiments sra = new Experiments();
    private static Method[] methods = sra.getClass().getMethods();
    private static ArrayList<String> alreadyRan = new ArrayList<String>();
    private static CSVReader csvReader = null;
    private static CSVWriter csvWriter = null;
    private static String[] potentialErrorRecord = {"IDENTIFIER"};
    private static File file = new File("./Experiments.csv");
    private static ArrayList<String> testsToRun = new ArrayList<>();

    public static void main(String[] args) {
        while(true) {
            try {

                System.out.println("-------------------------------------------------------------------------------");
                System.out.println("SRA Experiment Runner");
                System.out.println("-------------------------------------------------------------------------------");

                Options options = new Options();

                Option input = new Option("f", "file", true, "CSV file. Default: ./Experiments.csv");
                input.setRequired(false);
                options.addOption(input);

                Option option = new Option("t", "tests", true, "Tests to run. Default: All");
                option.setArgs(Option.UNLIMITED_VALUES);
                input.setRequired(false);
                options.addOption(option);

                CommandLineParser parser = new DefaultParser();
                HelpFormatter formatter = new HelpFormatter();
                CommandLine cmd;

                try {
                    cmd = parser.parse(options, args);
                    String filePath = cmd.getOptionValue("file");
                    String[] tests = cmd.getOptionValues("tests");

                    if (filePath != null)
                        file = new File(filePath);

                    if (tests != null)
                        Collections.addAll(testsToRun, tests);
                    else
                        testsToRun.add("all");

                } catch (ParseException e) {
                    System.out.println(e.getMessage());
                    formatter.printHelp("SRA", options);

                    System.exit(1);
                }

                if (!file.isFile())
                    file.createNewFile();

                Reader reader = Files.newBufferedReader(file.toPath());
                csvReader = new CSVReader(reader);
                String[] buffer;
                while ((buffer = csvReader.readNext()) != null)
                    alreadyRan.add(buffer[0]);
                csvReader.close();

                for (Method method : methods) {
                    if (testsToRun.contains(method.getName()) || testsToRun.contains("all")) {
                        if (method.getAnnotation(ToRun.class) != null && !alreadyRan.contains(method.getName())) {
                            csvWriter = new CSVWriter(new FileWriter(file, true));
                            potentialErrorRecord[0] = method.getName();
                            ArrayList<String> timmings = new ArrayList<String>();

                            System.out.println("-------------------------------------------------------------------------------");
                            System.out.println("Now running: " + method.getName());

                            for (Integer iterator = 0; iterator < numberOfRuns; iterator++) {
                                Stopwatch timer = Stopwatch.createStarted();
                                method.invoke(sra);
                                timer.stop();
                                System.out.println("[" + (iterator + 1) + "] Done in: " + timer.elapsed(TimeUnit.NANOSECONDS) + " ns.");
                                timmings.add("" + timer.elapsed(TimeUnit.NANOSECONDS));
                            }

                            System.out.println("[AVERAGE] Done in: " + average(timmings) + " ns.");
                            timmings.add(average(timmings));
                            timmings.add(0, method.getName());
                            csvWriter.writeNext(timmings.stream().toArray(String[]::new));
                            try {
                                csvWriter.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (alreadyRan.contains(method.getName())) {
                            System.out.println("Already ran " + method.getName() + ". Skipping.");
                        }
                    }
                }

                break;
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof StackOverflowError)
                    System.out.println("Stack overflow while computing. Skipping.");
                else if (e.getCause() instanceof OutOfMemoryError)
                    System.out.println("Ran out of memory while computing. Skipping.");
                csvWriter.writeNext(potentialErrorRecord);
                try {
                    csvWriter.close();
                } catch (Exception ee) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String average(ArrayList<String> results) {
        Integer length = results.size();
        Long total = 0L;
        for (String result : results)
            total += Long.parseLong(result, 10) ;
        return String.valueOf(total / length);
    }
}

