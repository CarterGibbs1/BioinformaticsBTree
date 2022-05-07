# Team Members

Last Name       | First Name      | GitHub User Name
--------------- | --------------- | --------------------
Greear          | Mesa            | MesaGreear
Gibbs           | Carter          | CarterGibbs1
Goin            | Aaron           | aarongoinboise

# Cache Performance Results
Inserting a number of random sequences 10 times into our BTree of random degree 5 through 29 results in average ms times of:

Cache Size | 10k  | 20k  | 50k  | 100k | 200k / differing results due to randomness
-----------|------|------|------|------|--------
Nil        | 63   | 129  | 317  | 653  | 1319 / 2123
100        | 26   | 74   | 190  | 484  | 982  / 2103
500        | 8    | 35   | 119  | 348  | 640  / 1627



# BTree Binary File Format and Layout
## BTree Metadata 
The BTree takes up the first 20 bytes of data and is formated like this:

root address (8) | degree (4) | k (2) | Number of BNodes (4) | Height (2) |

## BNodes
From 20 bytes onwards, BNodes are written as:

parent address (8) | n (4) | child0 (8) | key0 (8) | key0 frequency (4) | child1 (8) | key1 (8) ...

# File Formatting
Using this repository as is, produced dumps, RAFs, databases, and query results are put in corresponding folders which keeps things organized. On Onyx where
there are no subfolders, produced files will just be placed in the main directory.

# SQLite
Run the following command (in the same directory as the class) before you use GeneBankCreateBTree and GeneBankSearchDatabase:
"jar xf sqlite-jdbc-3.36.0.3.jar"

This command will produce several new folders and files that are needed for SQLite functionality.
