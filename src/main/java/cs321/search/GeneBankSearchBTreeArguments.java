package cs321.search;

public class GeneBankSearchBTreeArguments
{

    private final boolean useCache;
    private final String BTreeFileName;
    private final String queryFileName;
    private final int cacheSize;
    private final int debugLevel;

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
