package de.softwareforge.eyewiki;

/**
 * Contains release and version information.
 *
 * @author Janne Jalkanen
 */
public final class Release
{
    /**
     * Hidden C'tor
     */
    private Release()
    {
    }

    /** This is the default application name. */
    public static final String APPNAME = "eyeWiki";

    /**
     * This should be empty when doing a release - otherwise keep it as "cvs" so that whenever
     * someone checks out the code, they know it is a bleeding-edge version.  Other possible
     * values are "-alpha" and "-beta" for alpha and beta versions, respectively.
     */
    private static final String POSTFIX = "";

    /** This should be increased every time you do a release. */
    public static final String RELEASE = "R1";

    /** DOCUMENT ME! */
    public static final int VERSION = 1;

    /** DOCUMENT ME! */
    public static final int REVISION = 0;

    /** DOCUMENT ME! */
    public static final int MINORREVISION = 0;

    /**
     * This is the generic version string you should use when printing out the version.  It is of
     * the form "x.y.z-ttt".
     */
    public static final String VERSTR = VERSION + "." + REVISION + "." + MINORREVISION + POSTFIX;

    /**
     * This method is useful for templates, because hopefully it will not be inlined, and thus any
     * change to version number does not need recompiling the pages.
     *
     * @return DOCUMENT ME!
     *
     * @since 2.1.26.
     */
    public static String getVersionString()
    {
        return VERSTR;
    }

    /**
     * Executing this class directly from command line prints out the current version.  It is very
     * useful for things like different command line tools.
     *
     * <P>
     * Example:
     * <PRE>
     *  % java de.softwareforge.eyewiki.Release
     *  1.9.26-cvs
     *  </PRE>
     * </p>
     *
     * @param argv DOCUMENT ME!
     */
    public static void main(String [] argv)
    {
        System.out.println(VERSTR);
    }
}
