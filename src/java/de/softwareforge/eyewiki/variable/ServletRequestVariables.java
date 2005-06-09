package de.softwareforge.eyewiki.variable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.exception.NoSuchVariableException;
import de.softwareforge.eyewiki.manager.VariableManager;

public class ServletRequestVariables
        extends AbstractVariable
        implements WikiVariable
{
    private final VariableManager variableManager;

    public ServletRequestVariables(VariableManager variableManager)
    {
        this.variableManager = variableManager;
    }

    public synchronized void start()
    {
        // Hardcoded sequence of the evaluators
        variableManager.registerEvaluator(this, MIN_PRIORITY - 3);
    }

    public String getValue(WikiContext context, String varName)
            throws Exception
    {
        //  Well, I guess it wasn't a final straw.  We also allow
        //  variables from the session.
        //

        HttpServletRequest req = context.getHttpRequest();

        if (req != null)
        {
            HttpSession session = req.getSession();

            Object attribute = session.getAttribute(varName);

            if (attribute != null)
            {
                return String.valueOf(attribute);
            }
        }

        throw new NoSuchVariableException("");
    }
}
