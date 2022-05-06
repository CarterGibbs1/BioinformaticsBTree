

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class GeneBankCreateBTreeTest
{
    private String[] args;
    private GeneBankCreateBTreeArguments expectedConfiguration;
    private GeneBankCreateBTreeArguments actualConfiguration;

    @Test
    public void parse4CorrectArgumentsTest() throws ParseArgumentException
    {
        args = new String[4];
        args[0] = "0";
        args[1] = "20";
        args[2] = "fileNameGbk.gbk";
        args[3] = "13";

        expectedConfiguration = new GeneBankCreateBTreeArguments(false, 20, "fileNameGbk.gbk", 13, 0, 0);
        actualConfiguration = GeneBankCreateBTree.parseArguments(args);
        assertEquals(expectedConfiguration, actualConfiguration);
    }

    @Test
    public void readInputFileTest() throws FileNotFoundException, IOException, ParseArgumentException {
        GeneBankCreateBTreeArguments args = new GeneBankCreateBTreeArguments(false, 20, "src/test/java/cs321/create/test.txt", 13, 0, 0);
        String expectedOutput = "AGCTAAGCTAGCTATGCTATGCATGTAGTCAACACGTGCATTTTCGCAGACATCGTAGAGCTCTGTGTTAGCATCGTGATGCTACAGTGATG" +
                "AGTCGTAGTTAGCTAGTCATGTCAGTGATCGTAGTGCTAGTAGTAGTC";

        //GeneBankCreateBTree.testMain(args);
        //assert(true);
    }

    @Test
    public void testNInFormat() throws FileNotFoundException, IOException, ParseArgumentException {
        GeneBankCreateBTreeArguments args = new GeneBankCreateBTreeArguments(false, 20, "src/test/java/cs321/create/testNFormat", 13, 0, 0);
        BReadWrite.setRAF("./results.txt", true);
        //GeneBankCreateBTree.testMain(args);
    }

    @Test
    public void testgbk1File() throws Exception {
        String args[] = {"1", "3", "data/files_gbk/test1.gbk", "3", "100", "1"};
        GeneBankCreateBTree.main(args);
    }

    @Test
    public void testgbk0File() throws Exception {
        String args[] = {"1", "3", "data/files_gbk/test0.gbk", "4", "100", "1"};
        GeneBankCreateBTree.main(args);
    }

    @Test
    public void numberInFrontOfLinesTest() throws FileNotFoundException, IOException, ParseArgumentException {
        GeneBankCreateBTreeArguments args = new GeneBankCreateBTreeArguments(false, 20, "src/test/java/cs321/create/testNumberFormat.txt", 13, 0, 0);
        String expectedOutput = "AGTCGTCAGTCAGTAATATTGCGTTGTGCCGATCGTAGTTTATGCGATGGCTAGAGGTAGTCGTATAATATTTTTCGGCTTAGATGAGACCCCAGCTGAATTGTCTGGCGGTGTGTTGGTGTGCACCAGCTGAGACCAGCTACATCGGAGTTCAGGGTCTGGCGATCGTAGA";

        //String actualOutput = GeneBankCreateBTree.readGBKFile(args);

        //assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void testGBKFile1() throws FileNotFoundException, IOException, ParseArgumentException {
        GeneBankCreateBTreeArguments args = new GeneBankCreateBTreeArguments(false, 20, "src/test/java/cs321/create/gbk1.txt", 3, 0, 0);
        String expectedOutput = "gatcctccatatacaacggtatctccacctcaggtttagatctcaacaacggaaccattg" +
                "ccgacatgagacagttaggtatcgtcgagagttacaagctaaaacgagcagtagtcagct" +
                "ctgcatctgaagccgctgaagttctactaagggtggataacatcatccgtgcaagaccaa" +
                "gaaccgccaatagacaacatatgtaacatatttaggatatacctcgaaaataataaaccg" +
                "ccacactgtcattattataattagaaacagaacgcaaaaattatccactatataattcaa" +
                "agacgcgaaaaaaaaagaacaacgcgtcatagaacttttggcaattcgcgtcacaaataa" +
                "attttggcaacttatgtttcctcttcgagcagtactcgagccctgtctcaagaatgtaat" +
                "aatacccatcgtaggtatggttaaagatagcatctccacaacctcaaagctccttgccga" +
                "gagtcgccctcctttgtcgagtaattttcacttttcatatgagaacttattttcttattc" +
                "tttactctcacatcctgtagtgattgacactgcaacagccaccatcactagaagaacaga" +
                "acaattacttaatagaaaaattatatcttcctcgaaacgatttcctgcttccaacatcta" +
                "cgtatatcaagaagcattcacttaccatgacacagcttcagatttcattattgctgacag" +
                "ctactatatcactactccatctagtagtggccacgccctatgaggcatatcctatcggaa" +
                "aacaataccccccagtggcaagagtcaatgaatcgtttacatttcaaatttccaatgata" +
                "cctataaatcgtctgtagacaagacagctcaaataacatacaattgcttcgacttaccga" +
                "gctggctttcgtttgactctagttctagaacgttctcaggtgaaccttcttctgacttac" +
                "tatctgatgcgaacaccacgttgtatttcaatgtaatactcgagggtacggactctgccg" +
                "acagcacgtctttgaacaatacataccaatttgttgttacaaaccgtccatccatctcgc" +
                "tatcgtcagatttcaatctattggcgttgttaaaaaactatggttatactaacggcaaaa" +
                "acgctctgaaactagatcctaatgaagtcttcaacgtgacttttgaccgttcaatgttca" +
                "ctaacgaagaatccattgtgtcgtattacggacgttctcagttgtataatgcgccgttac" +
                "ccaattggctgttcttcgattctggcgagttgaagtttactgggacggcaccggtgataa" +
                "actcggcgattgctccagaaacaagctacagttttgtcatcatcgctacagacattgaag" +
                "gattttctgccgttgaggtagaattcgaattagtcatcggggctcaccagttaactacct" +
                "ctattcaaaatagtttgataatcaacgttactgacacaggtaacgtttcatatgacttac" +
                "ctctaaactatgtttatctcgatgacgatcctatttcttctgataaattgggttctataa" +
                "acttattggatgctccagactgggtggcattagataatgctaccatttccgggtctgtcc" +
                "cagatgaattactcggtaagaactccaatcctgccaatttttctgtgtccatttatgata" +
                "cttatggtgatgtgatttatttcaacttcgaagttgtctccacaacggatttgtttgcca" +
                "ttagttctcttcccaatattaacgctacaaggggtgaatggttctcctactattttttgc" +
                "cttctcagtttacagactacgtgaatacaaacgtttcattagagtttactaattcaagcc" +
                "aagaccatgactgggtgaaattccaatcatctaatttaacattagctggagaagtgccca" +
                "agaatttcgacaagctttcattaggtttgaaagcgaaccaaggttcacaatctcaagagc" +
                "tatattttaacatcattggcatggattcaaagataactcactcaaaccacagtgcgaatg" +
                "caacgtccacaagaagttctcaccactccacctcaacaagttcttacacatcttctactt" +
                "acactgcaaaaatttcttctacctccgctgctgctacttcttctgctccagcagcgctgc" +
                "cagcagccaataaaacttcatctcacaataaaaaagcagtagcaattgcgtgcggtgttg" +
                "ctatcccattaggcgttatcctagtagctctcatttgcttcctaatattctggagacgca" +
                "gaagggaaaatccagacgatgaaaacttaccgcatgctattagtggacctgatttgaata" +
                "atcctgcaaataaaccaaatcaagaaaacgctacacctttgaacaacccctttgatgatg" +
                "atgcttcctcgtacgatgatacttcaatagcaagaagattggctgctttgaacactttga" +
                "aattggataaccactctgccactgaatctgatatttccagcgtggatgaaaagagagatt" +
                "ctctatcaggtatgaatacatacaatgatcagttccaatcccaaagtaaagaagaattat" +
                "tagcaaaacccccagtacagcctccagagagcccgttctttgacccacagaataggtctt" +
                "cttctgtgtatatggatagtgaaccagcagtaaataaatcctggcgatatactggcaacc" +
                "tgtcaccagtctctgatattgtcagagacagttacggatcacaaaaaactgttgatacag" +
                "aaaaacttttcgatttagaagcaccagagaaggaaaaacgtacgtcaagggatgtcacta" +
                "tgtcttcactggacccttggaacagcaatattagcccttctcccgtaagaaaatcagtaa" +
                "caccatcaccatataacgtaacgaagcatcgtaaccgccacttacaaaatattcaagact" +
                "ctcaaagcggtaaaaacggaatcactcccacaacaatgtcaacttcatcttctgacgatt" +
                "ttgttccggttaaagatggtgaaaatttttgctgggtccatagcatggaaccagacagaa" +
                "gaccaagtaagaaaaggttagtagatttttcaaataagagtaatgtcaatgttggtcaag" +
                "ttaaggacattcacggacgcatcccagaaatgctgtgattatacgcaacgatattttgct" +
                "taattttattttcctgttttattttttattagtggtttacagataccctatattttattt" +
                "agtttttatacttagagacatttaattttaattccattcttcaaatttcatttttgcact" +
                "taaaacaaagatccaaaaatgctctcgccctcttcatattgagaatacactccattcaaa" +
                "attttgtcgtcaccgctgattaatttttcactaaactgatgaataatcaaaggccccacg" +
                "tcagaaccgactaaagaagtgagttttattttaggaggttgaaaaccattattgtctggt" +
                "aaattttcatcttcttgacatttaacccagtttgaatccctttcaatttctgctttttcc" +
                "tccaaactatcgaccctcctgtttctgtccaacttatgtcctagttccaattcgatcgca" +
                "ttaataactgcttcaaatgttattgtgtcatcgttgactttaggtaatttctccaaatgc" +
                "ataatcaaactatttaaggaagatcggaattcgtcgaacacttcagtttccgtaatgatc" +
                "tgatcgtctttatccacatgttgtaattcactaaaatctaaaacgtatttttcaatgcat" +
                "aaatcgttctttttattaataatgcagatggaaaatctgtaaacgtgcgttaatttagaa" +
                "agaacatccagtataagttcttctatatagtcaattaaagcaggatgcctattaatggga" +
                "acgaactgcggcaagttgaatgactggtaagtagtgtagtcgaatgactgaggtgggtat" +
                "acatttctataaaataaaatcaaattaatgtagcattttaagtataccctcagccacttc" +
                "tctacccatctattcataaagctgacgcaacgattactattttttttttcttcttggatc" +
                "tcagtcgtcgcaaaaacgtataccttctttttccgaccttttttttagctttctggaaaa" +
                "gtttatattagttaaacagggtctagtcttagtgtgaaagctagtggtttcgattgactg" +
                "atattaagaaagtggaaattaaattagtagtgtagacgtatatgcatatgtatttctcgc" +
                "ctgtttatgtttctacgtacttttgatttatagcaaggggaaaagaaatacatactattt" +
                "tttggtaaaggtgaaagcataatgtaaaagctagaataaaatggacgaaataaagagagg" +
                "cttagttcatcttttttccaaaaagcacccaatgataataactaaaatgaaaaggatttg" +
                "ccatctgtcagcaacatcagttgtgtgagcaataataaaatcatcacctccgttgccttt" +
                "agcgcgtttgtcgtttgtatcttccgtaattttagtcttatcaatgggaatcataaattt" +
                "tccaatgaattagcaatttcgtccaattctttttgagcttcttcatatttgctttggaat" +
                "tcttcgcacttcttttcccattcatctctttcttcttccaaagcaacgatccttctaccc" +
                "atttgctcagagttcaaatcggcctctttcagtttatccattgcttccttcagtttggct" +
                "tcactgtcttctagctgttgttctagatcctggtttttcttggtgtagttctcattatta" +
                "gatctcaagttattggagtcttcagccaattgctttgtatcagacaattgactctctaac" +
                "ttctccacttcactgtcgagttgctcgtttttagcggacaaagatttaatctcgttttct" +
                "ttttcagtgttagattgctctaattctttgagctgttctctcagctcctcatatttttct" +
                "tgccatgactcagattctaattttaagctattcaatttctctttgatc";

        //String actualOutput = GeneBankCreateBTree.readGBKFile(args);

        //assertEquals(expectedOutput, actualOutput);
    }

}
