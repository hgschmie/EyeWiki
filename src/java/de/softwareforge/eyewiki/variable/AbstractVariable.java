package de.softwareforge.eyewiki.variable;

import org.picocontainer.Startable;

import de.softwareforge.eyewiki.WikiContext;

public abstract class AbstractVariable
        extends AbstractSimpleVariable
        implements WikiVariable, Startable
{
    public abstract String getValue(WikiContext context, String variableName)
            throws Exception;

    public abstract void start();

    public synchronized void stop()
    {
        // GNDN
    }
}
