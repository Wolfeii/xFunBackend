package se.xfunserver.xfunbackend.spigot.botsam.object;

import org.bukkit.ChatColor;
import se.xfunserver.xfunbackend.assets.PluginMessageBuilder;
import se.xfunserver.xfunbackend.assets.PluginMessageType;
import se.xfunserver.xfunbackend.spigot.Core;
import se.xfunserver.xfunbackend.spigot.Module;
import se.xfunserver.xfunbackend.spigot.assets.C;
import se.xfunserver.xfunbackend.spigot.botsam.Sam;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.stream.Collectors;

public class ErrorHandler extends Handler {

    private Sam sam;

    public ErrorHandler(Sam sam) {
        this.sam = sam;
    }

    @Override
    public void publish(LogRecord record) {
        if (record.getLevel() == Level.WARNING) {
            try {
                error(
                        null,
                        record.getThrown() != null
                                ? record.getThrown().toString()
                                : "",
                        "Titta loggen för mer detaljer",
                        record.getThrown().getStackTrace(),
                        record.getMessage(),
                        record.getMessage()
                                .split("]")
                                [0]
                                .replace("[", ""));
                record.setThrown(null);
                record.setMessage(null);
            } catch (Exception ignored){
            }
        }
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }

    private void save(Module module, String cause, String solution, StackTraceElement[] stackTraceElements,
                      String message, String creator, String fileName) {
        try {
            Calendar calender = Calendar.getInstance();

            File errorLog = new File(Core.getPlugin().getDataFolder() +
                    File.separator + "logs" + File.separator + "errors" + File.separator + fileName);
            if (!errorLog.exists()) {
                errorLog.getParentFile().mkdirs();
                errorLog.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(errorLog, true);
            PrintWriter writer = new PrintWriter(fileWriter);

            SimpleDateFormat dateAndTimeFormat = new SimpleDateFormat("dd/MM/yyyy - h:mm a");

            writer.println("Fel Datum: "+dateAndTimeFormat.format(calender.getTime()));
            writer.println("Skapad av: "+(creator==null ? "xFunBackend" : creator));
            writer.println("Orsak: " + cause);
            writer.println("Beskrivning: " + solution);
            if (module != null){
                writer.println("System: " + module.getName());
            }
            writer.println("Exception Meddelande: " + message);
            writer.println("  ");

            if (stackTraceElements != null){
                for(StackTraceElement st : stackTraceElements){
                    writer.println("    " + st.toString());
                }
            }
            writer.println("  ");
            writer.println("  ");
            writer.flush();
            writer.close();
        } catch (Exception eb) {
            eb.printStackTrace();
        }
    }

    public void error(Module module, String cause, String solution, StackTraceElement[] stackTraceElements, String message, String creator) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateAndTimeFormat = new SimpleDateFormat("dd, MMM, yyyy | hh:mm a");
        SimpleDateFormat fileNameDate = new SimpleDateFormat("ddMMyyyyhhmm");
        String fileName = (fileNameDate.format(calendar.getTime())) + ".txt";

        String[] consoleMessage = new String[] {
                C.getLineWithNameWithoutSymbols(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "xFun Backend " + ChatColor.GRAY + "</>"),
                ChatColor.AQUA + "Vi har letat och fångat ett fel i plugin åt dig!",
                ChatColor.AQUA + (stackTraceElements == null ? "Det gör ont när det är något som inte fungerar!" : "Jag har sparat detta error för dig i 'plugins/xFunBackend/logs/errors/" + fileName),
                ChatColor.AQUA + "Datum Felet Uppstod " + ChatColor.DARK_GRAY + "» " + dateAndTimeFormat.format(calendar.getTime()),
                ChatColor.AQUA + "Anledning " + ChatColor.DARK_GRAY + "» " + cause,
                ChatColor.AQUA + "Potentiell Fix " + ChatColor.DARK_GRAY + "» " + solution,
                stackTraceElements == null ? ChatColor.DARK_AQUA + "Jag skapade tyvärr ingen fil åt dig ;(" : ChatColor.DARK_AQUA + "Läs mig fel logg för en mer detaljerad version!",
                ChatColor.DARK_AQUA + C.getLineWithoutSymbols() + ChatColor.RESET
        };

        if (stackTraceElements != null) {
            save(module, cause, solution, stackTraceElements, message, creator, fileName);
            reportError(fileName, message, cause, stackTraceElements);
        }

        C.sendConsoleColors(consoleMessage);
    }

    private void reportError(String fileName, String exceptionMessage, String exception, StackTraceElement[] stackTraceElements) {
        PluginMessageBuilder builder = new PluginMessageBuilder(PluginMessageType.AUTOMATED_ERROR);

        builder.prefix(false);
        builder.message(fileName, exceptionMessage, exception);
        builder.message(Arrays.stream(stackTraceElements)
                .map(StackTraceElement::toString)
                .collect(Collectors.toList()));
        builder.send();
    }

    public void error(Module module, String cause, String solution, Exception e) {
        error(module, cause, solution, e.getStackTrace(), e.getMessage(), "xFun Backend Sam Robot");
    }

    public void error(Module module, Exception e) {
        error(module, e.getCause() != null ? e.getCause().toString() : "Okänt", "", e.getStackTrace(), e.getMessage(), "xFunBackend");
    }
}
