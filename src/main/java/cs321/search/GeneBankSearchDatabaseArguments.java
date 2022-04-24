package cs321.search;

public class GeneBankSearchDatabaseArguments {
    private final String pathToDatabase;
    private final String queryPathway;
    private final int debugLevel;

    public GeneBankSearchDatabaseArguments(String pathToDatabase, String queryPathway, int debugLevel) {
        this.pathToDatabase = pathToDatabase;
        this.queryPathway = queryPathway;
        this.debugLevel = debugLevel;
    }

    @Override
    public String toString() {
        //this method was generated using an IDE
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
