package de.softwareforge.eyewiki.variable;

import java.util.Iterator;
import java.util.List;


import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.filters.FilterManager;
import de.softwareforge.eyewiki.manager.VariableManager;

public class PageFiltersVariable
        extends AbstractVariable
        implements WikiVariable
{
    private final FilterManager filterManager;
    private final VariableManager variableManager;

    public PageFiltersVariable(final VariableManager variableManager, final FilterManager filterManager)
    {
        this.filterManager = filterManager;
        this.variableManager = variableManager;
    }

    public synchronized void start()
    {
        variableManager.registerVariable("pagefilters", this);
    }

    public String getValue(WikiContext context, String variableName)
            throws Exception
    {
        List filters = filterManager.getVisibleFilterList();
        StringBuffer sb = new StringBuffer();

        for (Iterator i = filters.iterator(); i.hasNext();)
        {
            String f = i.next().getClass().getName();

            if (sb.length() > 0)
            {
                sb.append(", ");
            }

            sb.append(f);
        }

        return sb.toString();
    }
}
