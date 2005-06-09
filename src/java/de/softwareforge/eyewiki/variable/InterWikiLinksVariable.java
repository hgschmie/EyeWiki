package de.softwareforge.eyewiki.variable;

import java.util.Iterator;


import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiEngine;
import de.softwareforge.eyewiki.manager.VariableManager;

public class InterWikiLinksVariable
        extends AbstractVariable
        implements WikiVariable
{
    private final WikiEngine engine;
    private final VariableManager variableManager;

    public InterWikiLinksVariable(final VariableManager variableManager, final WikiEngine engine)
    {
        this.engine = engine;
        this.variableManager = variableManager;
    }

    public synchronized void start()
    {
        variableManager.registerVariable("interwikilinks", this);
    }

    public String getValue(WikiContext context, String variableName)
            throws Exception
    {
        StringBuffer sb = new StringBuffer();
        for (Iterator i = engine.getAllInterWikiLinks().iterator(); i.hasNext();)
        {
            String link = (String) i.next();
            sb.append(link)
                    .append(" --&gt; ")
                    .append(engine.getInterWikiURL(link))
                    .append("<br />\n");
        }

        return sb.toString();
    }
}
