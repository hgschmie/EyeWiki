package de.softwareforge.eyewiki.variable;


import de.softwareforge.eyewiki.Release;
import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.manager.VariableManager;

public class VersionVariable
        extends AbstractVariable
        implements WikiVariable
{
    private final VariableManager variableManager;

    public VersionVariable(final VariableManager variableManager)
    {
        this.variableManager = variableManager;
    }

    public synchronized void start()
    {
        variableManager.registerVariable("eyewikiversion", this);
    }

    public String getValue(WikiContext context, String variableName)
            throws Exception
    {
        return Release.getVersionString();
    }
}
