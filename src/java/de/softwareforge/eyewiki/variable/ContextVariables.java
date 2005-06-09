package de.softwareforge.eyewiki.variable;


import de.softwareforge.eyewiki.WikiContext;
import de.softwareforge.eyewiki.auth.UserProfile;
import de.softwareforge.eyewiki.exception.NoSuchVariableException;
import de.softwareforge.eyewiki.manager.VariableManager;

public class ContextVariables
        extends AbstractVariable
        implements WikiVariable
{
    private final VariableManager variableManager;

    public ContextVariables(VariableManager variableManager)
    {
        this.variableManager = variableManager;
    }

    public synchronized void start()
    {
        // Hardcoded sequence of the evaluators
        variableManager.registerEvaluator(this, MIN_PRIORITY - 0);

        variableManager.registerVariable("loginstatus", new LoginStatus());
        variableManager.registerVariable("username", new UserName());
        variableManager.registerVariable("requestcontext", new RequestContext());
    }

    public String getValue(WikiContext context, String varName)
            throws Exception
    {
        //
        // Check if such a context variable exists,
        // returning its string representation.
        //
        if ((context.getVariable(varName)) != null)
        {
            return context.getVariable(varName).toString();
        }
        throw new NoSuchVariableException("");
    }

    private static class LoginStatus
            extends AbstractSimpleVariable
            implements WikiVariable
    {
        public String getValue(WikiContext context, String varName)
                throws Exception
        {
            UserProfile wup = context.getCurrentUser();

            int status = (wup != null)
                    ? wup.getLoginStatus()
                    : UserProfile.NONE;

            switch (status)
            {
            case UserProfile.NONE:
                return "unknown (not logged in)";

            case UserProfile.COOKIE:
                return "named (cookie)";

            case UserProfile.CONTAINER:
                return "validated (container)";

            case UserProfile.PASSWORD:
                return "validated (password)";

            default:
                return "ILLEGAL STATUS!";
            }
        }
    }

    private static class UserName
            extends AbstractSimpleVariable
            implements WikiVariable
    {
        public String getValue(WikiContext context, String varName)
                throws Exception
        {
            UserProfile wup = context.getCurrentUser();

            return (wup != null)
                    ? wup.getName()
                    : "not logged in";
        }
    }

    private static class RequestContext
            extends AbstractSimpleVariable
            implements WikiVariable
    {
        public String getValue(WikiContext context, String varName)
                throws Exception
        {
            return context.getRequestContext();
        }
    }
}
