package de.softwareforge.eyewiki.variable;

import java.util.NoSuchElementException;

import org.apache.commons.configuration.Configuration;


import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiProperties;
import de.softwareforge.eyewiki.exception.NoSuchVariableException;
import de.softwareforge.eyewiki.manager.VariableManager;

public class ConfigurationVariables
        extends AbstractVariable
        implements WikiVariable
{
    private final Configuration conf;
    private final VariableManager variableManager;

    public ConfigurationVariables(VariableManager variableManager, Configuration conf)
    {
        this.conf = conf;
        this.variableManager = variableManager;
    }

    public synchronized void start()
    {
        // Hardcoded sequence of the evaluators
        variableManager.registerEvaluator(this, MIN_PRIORITY - 1);
    }

    public String getValue(WikiContext context, String varName)
            throws Exception
    {
        // Next-to-final straw: attempt to fetch using property name
        // We don't allow fetching any other properties than those starting
        // with "eyewiki.".  I know my own code, but I can't vouch for bugs
        // in other people's code... :-)
        if (varName.startsWith(WikiProperties.PROP_PREFIX))
        {
            try
            {
                return conf.getString(varName);
            }
            catch (NoSuchElementException nsee)
            {
                // Does nothing, just continue searching...
            }
        }

        throw new NoSuchVariableException("");
    }
}
