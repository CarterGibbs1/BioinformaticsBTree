package cs321.search;

/**
 * Args for searching database
 */
public class GeneBankSearchDatabaseArguments {
    private final String pathToDatabase;
    private final String queryPathway;
    private final int debugLevel;

    /**
     * Args for searching database.
     *
     * @param pathToDatabase dir pathway to database, usually in same directory, and just db file name
     * @param queryPathway dir pathway to query file
     * @param debugLevel optional arg, only is 0, displays results on console
     */
    public GeneBankSearchDatabaseArguments(String pathToDatabase, String queryPathway, int debugLevel) {
        this.pathToDatabase = pathToDatabase;
        this.queryPathway = queryPathway;
        this.debugLevel = debugLevel;// only 0
    }

    @Override
    public String toString() {
        return "GeneBankCreateBTreeArguments{" +
                "pathToDatabase=" + pathToDatabase +
                ", queryPathway=" + queryPathway +
                ", debugLevel='" + debugLevel +
                '}';
    }

    public String getPathToDatabase() {
        return pathToDatabase;
    }

    public String getQueryPathway() {
        return queryPathway;
    }

    public int debugLevel() {
        return debugLevel;
    }
}
