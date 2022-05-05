

/**
 * Used to hold SearchBTree driver arguments
 *
 * @author  Carter Gibbs
 * @version Spring 2022
 */
public class GeneBankSearchBTreeArguments
{

    private final boolean useCache; // bool of if BTree uses cache
    private final String BTreeFileName; // BTree filename
    private final String queryFileName; // query filename
    private final int cacheSize; // cache size (optional)
    private final int debugLevel; // debug level (1 or 0) (optional)

    /**
     * Constructor to hold args
     * @param useCache
     * @param BTreeFileName
     * @param queryFileName
     * @param cacheSize
     * @param debugLevel
     */
    public GeneBankSearchBTreeArguments(boolean useCache, String BTreeFileName, String queryFileName, int cacheSize, int debugLevel)
    {
        this.useCache = useCache;
        this.BTreeFileName = BTreeFileName;
        this.queryFileName = queryFileName;
        this.cacheSize = cacheSize;
        this.debugLevel = debugLevel;
    }

    // UTILITY
    @Override
    public String toString()
    {
        return "GeneBankSearchBTreeArguments{" +
                "useCache=" + useCache +
                ", BTreeFileName=" + BTreeFileName +
                ", queryFileName='" + queryFileName +
                ", cacheSize=" + cacheSize +
                ", debugLevel=" + debugLevel +
                '}';
    }

    // GETTERS
    public boolean isUseCache() {
        return useCache;
    }

    public String getBTreeFileName() {
        return BTreeFileName;
    }

    public String getQueryFileName() {
        return queryFileName;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public int getDebugLevel() {
        return debugLevel;
    }
}
