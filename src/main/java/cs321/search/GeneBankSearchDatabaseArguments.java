package cs321.search;

public class GeneBankSearchDatabaseArguments {
    private final String pathToDatabase;
    private final String queryPathway;
    private final int debugLevel;

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
