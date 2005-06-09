package de.softwareforge.eyewiki.variable;

import java.util.Iterator;


import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.manager.VariableManager;

public class InlinedImagesVariable
        extends AbstractVariable
        implements WikiVariable
{
    private final WikiEngine engine;
    private final VariableManager variableManager;

    public InlinedImagesVariable(final VariableManager variableManager, final WikiEngine engine)
    {
        this.engine = engine;
        this.variableManager = variableManager;
    }

    public synchronized void start()
    {
        variableManager.registerVariable("inlinedimages", this);
    }

    public String getValue(WikiContext context, String variableName)
            throws Exception
    {
            StringBuffer sb = new StringBuffer();
            for (
                Iterator i = engine.getAllInlinedImagePatterns().iterator();
                            i.hasNext();)
            {
                String ptrn = (String) i.next();
                sb.append(ptrn)
                        .append("<br />\n");
            }

            return sb.toString();
    }
}
