package de.softwareforge.eyewiki.variable;


import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.WikiPage;
import de.softwareforge.eyewiki.exception.NoSuchVariableException;
import de.softwareforge.eyewiki.manager.VariableManager;

public class MetadataVariables
        extends AbstractVariable
        implements WikiVariable
{
    private final VariableManager variableManager;

    public MetadataVariables(VariableManager variableManager)
    {
        this.variableManager = variableManager;
    }

    public synchronized void start()
    {
        // Hardcoded sequence of the evaluators
        variableManager.registerEvaluator(this, MIN_PRIORITY - 2);
    }

    public String getValue(WikiContext context, String varName)
            throws Exception
    {
        // And the final straw: see if the current page has named metadata.
        //
        WikiPage pg = context.getPage();

        if (pg != null)
        {
            Object metadata = pg.getAttribute(varName);

            if (metadata != null)
            {
                return (metadata.toString());
            }
        }

        throw new NoSuchVariableException("");
    }
}
