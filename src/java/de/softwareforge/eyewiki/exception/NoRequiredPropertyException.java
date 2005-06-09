package de.softwareforge.eyewiki.exception;

import de.softwareforge.eyewiki.WikiException;

/**
 * Marks an erroneus eyewiki.properties file.  Certain properties have been marked as "required",
 * and if you do not provide a good value for a property, you'll see this exception.
 *
 * <P>
 * Check <TT>eyewiki.properties</TT> for the required properties.
 * </p>
 *
 * @author Janne Jalkanen
 */
public class NoRequiredPropertyException
        extends WikiException
{
    /**
     * Constructs an exception.
     *
     * @param msg Message to show
     * @param key The key of the property in question.
     */
    public NoRequiredPropertyException(String msg, String key)
    {
        super(msg + ": key=" + key);
    }
}
