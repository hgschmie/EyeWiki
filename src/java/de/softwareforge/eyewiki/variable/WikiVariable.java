package de.softwareforge.eyewiki.variable;

import de.softwareforge.eyewiki.WikiContext;

public interface WikiVariable
{
    /** This evaluator should run as early as possible */
    int MAX_PRIORITY = 1000;

    /** This evaluator should run on normal priority */
    int NORMAL_PRIORITY = 0;

    /** This evaluator should run as late as possible */
    int MIN_PRIORITY = -1000;

    /**
     * Returns the value for this variable evaluation.
     *
     * Non-evaluating variables don't need to check the variable
     * name, they are only called with their registered name.
     */
    String getValue(WikiContext context, String variableName)
            throws Exception;

    /**
     * Evaluators can return a priority where they want to be evaluated.
     */
    int getPriority();

    /**
     * Should this variable / evaluator be visible in a possible list-out?
     */
    boolean isVisible();

    /**
     * The human readable name of this variable
     */
    String getName();
}
