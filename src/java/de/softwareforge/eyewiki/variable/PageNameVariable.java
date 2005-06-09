package de.softwareforge.eyewiki.variable;


import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.manager.VariableManager;

public class PageNameVariable
        extends AbstractVariable
        implements WikiVariable
{
    private final VariableManager variableManager;

    public PageNameVariable(final VariableManager variableManager)
    {
        this.variableManager = variableManager;
    }

    public synchronized void start()
    {
        variableManager.registerVariable("pagename", this);
    }

    public String getValue(WikiContext context, String variableName)
            throws Exception
    {
        return context.getPage().getName();
    }
}
