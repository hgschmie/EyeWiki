package de.softwareforge.eyewiki.plugin;

import java.util.Map;

import de.softwareforge.eyewiki.WikiContext;


/**
 * Provides a page-specific counter.
 *
 * <P>
 * Parameters
 *
 * <UL>
 * <li>
 * name - Name of the counter.  Optional.
 * </li>
 * </ul>
 *
 * Stores a variable in the WikiContext called "counter", with the name of the optionally attached.
 * For example:<BR> If name is "thispage", then the variable name is called "counter-thispage".
 * </p>
 *
 * @author Janne Jalkanen
 *
 * @since 1.9.30
 */
public class Counter
        implements WikiPlugin
{
    /** DOCUMENT ME! */
    static final String VARIABLE_NAME = "counter";

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
        //
        //  First, determine which kind of name we use to store in
        //  the WikiContext.
        //
        String countername = (String) params.get("name");

        if (countername == null)
        {
            countername = VARIABLE_NAME;
        }
        else
        {
            countername = VARIABLE_NAME + "-" + countername;
        }

        //
        //  Fetch, increment, and store back.
        //
        Integer val = (Integer) context.getVariable(countername);

        if (val == null)
        {
            val = new Integer(0);
        }

        val = new Integer(val.intValue() + 1);

        context.setVariable(countername, val);

        return val.toString();
    }
}
