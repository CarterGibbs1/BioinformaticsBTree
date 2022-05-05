
/**
 * Used to hold CreateBTree driver arguments
 *
 * @author  Carter Gibbs
 * @version Spring 2022
 */
public class GeneBankCreateBTreeArguments {
    private final boolean useCache; // bool to if the BTree uses a cache
    private final int degree; // degree of the BTree
    private final String gbkFileName; // gbk file name
    private final int subsequenceLength; // subsequence length of the dna
    private final int cacheSize; // cache size (optional)
    private final int debugLevel; // debug level (0 or 1) (optional)

    /**
     * Constructor to hold args
     * @param useCache
     * @param degree
     * @param gbkFileName
     * @param subsequenceLength
     * @param cacheSize
     * @param debugLevel
     */
    public GeneBankCreateBTreeArguments(boolean useCache, int degree, String gbkFileName, int subsequenceLength, int cacheSize, int debugLevel) {
        this.useCache = useCache;
        this.degree = degree;
        this.gbkFileName = gbkFileName;
        this.subsequenceLength = subsequenceLength;
        this.cacheSize = cacheSize;
        this.debugLevel = debugLevel;
    }

    @Override
    public boolean equals(Object obj)
    {
        //this method was generated using an IDE
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        GeneBankCreateBTreeArguments other = (GeneBankCreateBTreeArguments) obj;
        if (cacheSize != other.cacheSize)
        {
            return false;
        }
        if (debugLevel != other.debugLevel)
        {
            return false;
        }
        if (degree != other.degree)
        {
            return false;
        }
        if (gbkFileName == null)
        {
            if (other.gbkFileName != null)
            {
                return false;
            }
        }
        else
        {
            if (!gbkFileName.equals(other.gbkFileName))
            {
                return false;
            }
        }
        if (subsequenceLength != other.subsequenceLength)
        {
            return false;
        }
        if (useCache != other.useCache)
        {
            return false;
        }
        return true;
    }


    @Override
    public String toString()
    {
        //this method was generated using an IDE
        return "GeneBankCreateBTreeArguments{" +
                "useCache=" + useCache +
                ", degree=" + degree +
                ", gbkFileName='" + gbkFileName + '\'' +
                ", subsequenceLength=" + subsequenceLength +
                ", cacheSize=" + cacheSize +
                ", debugLevel=" + debugLevel +
                '}';
    }

    // GETTERS
    public int getCacheSize() {
        return cacheSize;
    }

    public boolean isUseCache() {
        return useCache;
    }

    public int getDegree() {
        return degree;
    }

    public String getGbkFileName() {
        return gbkFileName;
    }

    public int getSubsequenceLength() {
        return subsequenceLength;
    }

    public int getDebugLevel() {
        return debugLevel;
    }
}
