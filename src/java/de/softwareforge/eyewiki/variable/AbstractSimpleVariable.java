package de.softwareforge.eyewiki.variable;

import de.softwareforge.eyewiki.WikiContext;

public abstract class AbstractSimpleVariable
        implements WikiVariable
{
    public abstract String getValue(WikiContext context, String variableName)
            throws Exception;

    public int getPriority()
    {
        return NORMAL_PRIORITY;
    }

    public boolean isVisible()
    {
        return true;
    }

    public String getName()
    {
        return this.getClass().getName();
    }
}
