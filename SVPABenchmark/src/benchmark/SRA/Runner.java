package benchmark.SRA;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import java.lang.management.ManagementFactory;
import java.io.IOException;
import java.io.File;

import com.google.common.base.Stopwatch;
import java.util.concurrent.TimeUnit;

public class Runner {

    private static Integer numberOfRuns = 5;

    public static void main(String[] args) {
        Experiments sra = new Experiments();
        Method[] methods = sra.getClass().getMethods();
        ArrayList<String> alreadyRan = new ArrayList<String>();
        CSVReader csvReader = null;
        CSVWriter csvWriter = null;

        try {
            csvReader = new CSVReader(new FileReader("Experiments.csv"),',','"',1);
            while (csvReader.readNext() != null)
                alreadyRan.add(csvReader.readNext()[0]);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                csvReader.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        for (String method : alreadyRan)
            System.out.println("Already ran " +  method + ". Skipping.");

        for (Method method : methods) {
            if (method.getAnnotation(ToRun.class) != null && !alreadyRan.contains(method.getName())) {
                String[] potentialErrorRecord = {method.getName()};
                ArrayList<String> timmings = new ArrayList<String>();

                System.out.println("Now running: " + method.getName());
                try {
                    csvWriter = new CSVWriter(new FileWriter("Experiments.csv"));
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
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                } catch (InvocationTargetException e) {
                    if (e.getCause() instanceof StackOverflowError)
                        System.out.println("Stack overflow while computing. Restarting JVM and skipping.");
                    else if (e.getCause() instanceof OutOfMemoryError)
                        System.out.println("Ran out of memory while computing. Restarting JVM and skipping.");
                    csvWriter.writeNext(potentialErrorRecord);
                    try {
                        restartApplication(null);
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

    // -------------------------
    // CSV methods
    // -------------------------

    private static String readFile(Path path) {
        String response = "";
        try {
            FileReader fr = new FileReader(path.toString());
            BufferedReader br = new BufferedReader(fr);
            String strLine;
            StringBuffer sb = new StringBuffer();
            while ((strLine = br.readLine()) != null) {
                sb.append(strLine);
            }
            response = sb.toString();
            System.out.println(response);
            fr.close();
            br.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return response;
    }

    private static String csvWrite(List<String[]> stringArray, Path path) {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(path.toString()));
            writer.writeAll(stringArray);
            writer.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return readFile(path);
    }

    /**
     * Sun property pointing the main class and its arguments.
     * Might not be defined on non Hotspot VM implementations.
     */
    public static final String SUN_JAVA_COMMAND = "sun.java.command";

    /**
     * Restart the current Java application
     * @param runBeforeRestart some custom code to be run before restarting
     * @throws IOException
     */
    public static void restartApplication(Runnable runBeforeRestart) throws IOException {
        try {
            String java = System.getProperty("java.home") + "/bin/java";
            List<String> vmArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
            StringBuffer vmArgsOneLine = new StringBuffer();
            for (String arg : vmArguments) {
                if (!arg.contains("-agentlib")) {
                    vmArgsOneLine.append(arg);
                    vmArgsOneLine.append(" ");
                }
            }
            final StringBuffer cmd = new StringBuffer("\"" + java + "\" " + vmArgsOneLine);

            String[] mainCommand = System.getProperty(SUN_JAVA_COMMAND).split(" ");
            if (mainCommand[0].endsWith(".jar")) {
                cmd.append("-jar " + new File(mainCommand[0]).getPath());
            } else {
                cmd.append("-cp \"" + System.getProperty("java.class.path") + "\" " + mainCommand[0]);
            }
            for (int i = 1; i < mainCommand.length; i++) {
                cmd.append(" ");
                cmd.append(mainCommand[i]);
            }

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        Runtime.getRuntime().exec(cmd.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            if (runBeforeRestart!= null) {
                runBeforeRestart.run();
            }

            System.exit(0);
        } catch (Exception e) {
            throw new IOException("Error while trying to restart the application", e);
        }
    }
}
