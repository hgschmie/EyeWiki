package de.softwareforge.eyewiki;

/**
 * This simple class just fulfils the role of a container for searches.  It tells the word and
 * whether it is requested or not.
 *
 * @author Janne Jalkanen
 */
public class QueryItem
{
    /** The word is required to be in the pages */
    public static final int REQUIRED = 1;

    /** The word may NOT be in the pages */
    public static final int FORBIDDEN = -1;

    /** The word should be in the pages, but the search engine may use its own discretion. */
    public static final int REQUESTED = 0;

    /** The word that is being searched */
    private String word;

    /** The type of the word.  See above for types.  The default is REQUESTED. */
    private int type;

    public QueryItem()
    {
        type = REQUESTED;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public String getWord()
    {
        return word;
    }

    public void setWord(String word)
    {
        this.word = word;
    }
}
