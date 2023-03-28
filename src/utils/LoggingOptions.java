package utils;

import java.util.HashSet;

/**
 *
 * @author K Neretin
 */
public class LoggingOptions {

    private HashSet<String> allowedLogTypes;
    private boolean rewriteOptionSeted = false;
    private boolean rewriteOption = false;
    private boolean addDateTime = true;

    public LoggingOptions() {
        allowedLogTypes = new HashSet<>();
        allowedLogTypes.add("nofilter");
        allowedLogTypes.add("error");
        allowedLogTypes.add("default");
        allowedLogTypes.add("debug");
        allowedLogTypes.add("process");
        rewriteOption = true;
        rewriteOptionSeted = true;
    }

    public boolean allowLogType(String type) {
        return allowedLogTypes.contains(type);
    }

    public boolean isRewriteOptionSeted() {
        return rewriteOptionSeted;
    }

    public boolean isRewriteOption() {
        return rewriteOption;
    }

    public boolean isAddDateTime() {
        return addDateTime;
    }

    public void setAddDateTime(boolean addDateTime) {
        this.addDateTime = addDateTime;
    }

}
