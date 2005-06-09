package de.softwareforge.eyewiki.variable;

import java.util.Date;


import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.manager.VariableManager;

public class UptimeVariable
        extends AbstractVariable
        implements WikiVariable
{
    private final WikiEngine engine;
    private final VariableManager variableManager;

    public UptimeVariable(final VariableManager variableManager, final WikiEngine engine)
    {
        this.engine = engine;
        this.variableManager = variableManager;
    }

    public synchronized void start()
    {
        variableManager.registerVariable("uptime", this);
    }

    public String getValue(WikiContext context, String variableName)
            throws Exception
    {
        Date now = new Date();
        long secondsRunning =
                (now.getTime() - engine.getStartTime().getTime()) / 1000L;

        long seconds = secondsRunning % 60;
        long minutes = (secondsRunning /= 60) % 60;
        long hours = (secondsRunning /= 60) % 24;
        long days = secondsRunning /= 24;

        return new StringBuffer()
        	    .append(days)
                .append("d, ")
                .append(hours)
                .append("h ")
                .append(minutes)
                .append("m ")
                .append(seconds)
                .append("s")
                .toString();
    }
}
