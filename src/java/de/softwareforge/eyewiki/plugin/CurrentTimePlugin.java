package de.softwareforge.eyewiki.plugin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import de.softwareforge.eyewiki.WikiContext;


/**
 * Just displays the current date and time. The time format is exactly like in the
 * java.util.SimpleDateFormat class.
 *
 * @author Janne Jalkanen
 *
 * @see java.util.SimpleDateFormat
 * @since 1.7.8
 */
public class CurrentTimePlugin
        implements WikiPlugin
{
    /** DOCUMENT ME! */
    private static Logger log = Logger.getLogger(CurrentTimePlugin.class);

    /** DOCUMENT ME! */
    public static final String DEFAULT_FORMAT = "HH:mm:ss dd-MMM-yyyy zzzz";

    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     * @param params DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws PluginException DOCUMENT ME!
     */
    public String execute(WikiContext context, Map params)
            throws PluginException
    {
        String formatString = (String) params.get("format");

        if (formatString == null)
        {
            formatString = DEFAULT_FORMAT;
        }

        if (log.isDebugEnabled())
        {
            log.debug("Date format string is: " + formatString);
        }

        try
        {
            SimpleDateFormat fmt = new SimpleDateFormat(formatString);

            Date d = new Date(); // Now.

            return fmt.format(d);
        }
        catch (IllegalArgumentException e)
        {
            throw new PluginException("You specified bad format: " + e.getMessage());
        }
    }
}
