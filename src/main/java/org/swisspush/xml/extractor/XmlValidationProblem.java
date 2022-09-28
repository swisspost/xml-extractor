package org.swisspush.xml.extractor;

import javax.xml.stream.Location;

public class XmlValidationProblem {
    public final static int SEVERITY_WARNING = 1;
    public final static int SEVERITY_ERROR = 2;
    public final static int SEVERITY_FATAL = 3;


    private Location location;
    private String message;
    private int severity;
    private String type;

    public XmlValidationProblem(Location location, String message, int severity, String type) {
        this.location = location;
        this.message = message;
        this.severity = severity;
        this.type = type;
    }

    public Location getLocation() {
        return location;
    }

    public String getMessage() {
        return message;
    }

    public int getSeverity() {
        return severity;
    }

    public String getType() {
        return type;
    }
}
