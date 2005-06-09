package de.softwareforge.eyewiki.variable;


import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.exception.NoSuchVariableException;
import de.softwareforge.eyewiki.manager.VariableManager;

public class HttpParameterVariables
        extends AbstractVariable
        implements WikiVariable
{
    private final VariableManager variableManager;

    public HttpParameterVariables(VariableManager variableManager)
    {
        this.variableManager = variableManager;
    }

    public synchronized void start()
    {
        // Hardcoded sequence of the evaluators
        variableManager.registerEvaluator(this, MIN_PRIORITY - 4);
    }

    public String getValue(WikiContext context, String varName)
            throws Exception
    {
        //  Well, I guess it wasn't a final straw.  We also allow
        //  variables from the request.
        //
        String res = context.getHttpParameter(varName);
        if (res != null)
        {
            return res;
        }

        throw new NoSuchVariableException("");
    }
}
