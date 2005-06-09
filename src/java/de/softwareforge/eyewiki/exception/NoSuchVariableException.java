package de.softwareforge.eyewiki.exception;

import de.softwareforge.eyewiki.WikiException;

/**
 * Marks that no such variable was located.
 *
 * @author Janne Jalkanen
 */
public class NoSuchVariableException
        extends WikiException
{
    /**
     * Constructs an exception.
     *
     * @param msg Message to show
     */
    public NoSuchVariableException(String msg)
    {
        super(msg);
    }
}
