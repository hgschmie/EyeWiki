package de.softwareforge.eyewiki.variable;

import org.picocontainer.Startable;


import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.manager.VariableManager;

public class ConstantVariables
        implements Startable
{
    private final VariableManager variableManager;

    public ConstantVariables(final VariableManager variableManager)
    {
        this.variableManager = variableManager;
    }

    public synchronized void start()
    {
        variableManager.registerVariable(VariableManager.VAR_ERROR, new ConstantVariable(""));
        variableManager.registerVariable(VariableManager.VAR_MSG, new ConstantVariable(""));
    }

    public synchronized void stop()
    {
        // GNDN
    }

    private static class ConstantVariable
            extends AbstractSimpleVariable
            implements WikiVariable
    {
        private final String value;

        private ConstantVariable(final String value)
        {
            this.value = value;
        }

        public String getValue(WikiContext context, String variableName)
        {
            return value;
        }
    }
}

