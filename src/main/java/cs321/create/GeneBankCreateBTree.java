package cs321.create;

import cs321.btree.BTree;
import cs321.common.ParseArgumentException;

import java.io.*;
import java.util.List;

public class GeneBankCreateBTree {

    public static void main(String[] args) throws Exception {
        System.out.println("Hello world from cs321.create.GeneBankCreateBTree.main");
        GeneBankCreateBTreeArguments geneBankCreateBTreeArguments = parseArgumentsAndHandleExceptions(args);


    }

    private static GeneBankCreateBTreeArguments parseArgumentsAndHandleExceptions(String[] args) {
        GeneBankCreateBTreeArguments geneBankCreateBTreeArguments = null;
        try {
            geneBankCreateBTreeArguments = parseArguments(args);
        } catch (ParseArgumentException e) {
            printUsageAndExit(e.getMessage());
        }
        return geneBankCreateBTreeArguments;
    }

    private static void printUsageAndExit(String errorMessage) {

        System.exit(1);
    }

    public static GeneBankCreateBTreeArguments parseArguments(String[] args) throws ParseArgumentException {
        if (args.length < 4 || args.length > 6) {
            System.out.println
                    ("Usage: java GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> <sequence length> [<cache size>] [<debug level>]\n");
            System.out.println
                    ("<0/1(no/with Cache)>: 1 for run program with a Cache, 0 for without.");
            System.out.println
                    ("<degree>: degree of the BTree.");
            System.out.println
                    ("<gbk file>: file name of the gene bank.");
            System.out.println
                    ("<sequence length>: sequence length of each DNA strand. Integer between 1 and 31 inclusive.");
            System.out.println
                    ("[<cache size>]: if running program with cache, then must include the size of the cache in arguments");
            System.out.println
                    ("[<debug level>]: type of output wanted for program. 1 for a dump file output with console. 0 for just console.");
            throw new ParseArgumentException("Incorrect number of arguments");
        }
        int withOrWithoutCache = Integer.parseInt(args[0]);
        if (withOrWithoutCache != 0 && withOrWithoutCache != 1)
            throw new ParseArgumentException("With or Without Cache can only be 1 or 0.");
        int degree = Integer.parseInt(args[1]);
        if (degree < 0) throw new ParseArgumentException("Degree must be 0 or greater, cannot be negative.");
        String gbkFileName = args[2];
        int sequenceLength = Integer.parseInt(args[3]);
        if (sequenceLength < 1 || sequenceLength > 31) throw new ParseArgumentException("Sequence length must be between 1 and 31 inclusive.");
        int cacheSize = 0;
        int debugLevel = 0;
        if (withOrWithoutCache == 1 && args.length < 5) {
            throw new ParseArgumentException("If With/Without Cache is 1, then must provide Cache size.");
        } else if (args.length >= 5){
            cacheSize = Integer.parseInt(args[4]);
            if (cacheSize <= 0) throw new ParseArgumentException("Cache size must be greater than 0.");
            if (args.length == 6) {
                debugLevel = Integer.parseInt(args[5]);
                if (debugLevel != 1 && debugLevel != 0) throw new ParseArgumentException("Debug level must be either 0 or 1");
            }
        }

        return new GeneBankCreateBTreeArguments(withOrWithoutCache == 1, degree, gbkFileName, sequenceLength, cacheSize, debugLevel);
    }
}
