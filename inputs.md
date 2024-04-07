# Inputs

## Client Inputs

### Hamlet with folder
upload /s/chopin/l/grad/asterix/CS555/csx55.dfs/inputFilesForTesting/hamlet.txt test.txt
download test.txt /s/chopin/l/grad/asterix/CS555/csx55.dfs/downloaded/test.txt

upload /s/chopin/l/grad/asterix/CS555/csx55.dfs/inputFilesForTesting/hamlet.txt /test1.txt
download test1.txt /s/chopin/l/grad/asterix/CS555/csx55.dfs/downloaded/test1.txt

upload /s/chopin/l/grad/asterix/CS555/csx55.dfs/inputFilesForTesting/hamlet.txt test2.txt
download /test2.txt /s/chopin/l/grad/asterix/CS555/csx55.dfs/downloaded/test2.txt

upload /s/chopin/l/grad/asterix/CS555/csx55.dfs/inputFilesForTesting/hamlet.txt /test3.txt
download /test3.txt /s/chopin/l/grad/asterix/CS555/csx55.dfs/downloaded/test3.txt

### Hamlet without folder
upload /s/chopin/l/grad/asterix/CS555/csx55.dfs/inputFilesForTesting/hamlet.txt dl/testFromDir.txt
download dl/testFromDir.txt /s/chopin/l/grad/asterix/CS555/csx55.dfs/downloaded/testFromDir.txt

upload /s/chopin/l/grad/asterix/CS555/csx55.dfs/inputFilesForTesting/hamlet.txt /dl/testFromDir1.txt
download dl/testFromDir1.txt /s/chopin/l/grad/asterix/CS555/csx55.dfs/downloaded/testFromDir1.txt

upload /s/chopin/l/grad/asterix/CS555/csx55.dfs/inputFilesForTesting/hamlet.txt dl/testFromDir2.txt
download /dl/testFromDir2.txt /s/chopin/l/grad/asterix/CS555/csx55.dfs/downloaded/testFromDir2.txt

upload /s/chopin/l/grad/asterix/CS555/csx55.dfs/inputFilesForTesting/hamlet.txt /dl/testFromDir3.txt
download /dl/testFromDir3.txt /s/chopin/l/grad/asterix/CS555/csx55.dfs/downloaded/testFromDir3.txt

### Disrupted
upload /s/chopin/l/grad/asterix/CS555/csx55.dfs/inputFilesForTesting/hamlet.txt DISRUPT4/testFromDir.txt
download DISRUPT4/testFromDir.txt /s/chopin/l/grad/asterix/CS555/csx55.dfs/downloaded/disrupted.txt

### Photo
upload /s/chopin/l/grad/asterix/CS555/csx55.dfs/inputFilesForTesting/bigPhoto.jpg /bigPhoto.jpg
download bigPhoto.jpg /s/chopin/l/grad/asterix/CS555/csx55.dfs/downloaded/bigPhoto.jpg

## Controller Inputs
chunk-servers
chunk-servers --metadata
chunk-servers --chunks

## Misc
cmp downloaded/hamlet.txt inputFilesForTesting/hamlet.txt
cmp downloaded/bigPhoto.jpg inputFilesForTesting/bigPhoto.jpg
