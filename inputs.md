# Inputs

## Client Inputs
upload /s/chopin/l/grad/asterix/CS555/csx55.dfs/inputFilesForTesting/hamlet.txt hamlet.txt
download /hamlet.txt /s/chopin/l/grad/asterix/CS555/csx55.dfs/downloaded/hamlet.txt

upload /s/chopin/l/grad/asterix/CS555/csx55.dfs/inputFilesForTesting/bigPhoto.jpg /bigPhoto.jpg
download /bigPhoto.jpg /s/chopin/l/grad/asterix/CS555/csx55.dfs/downloaded/bigPhoto.jpg

## Controller Inputs
chunk-servers
chunk-servers --metadata
chunk-servers --chunks

## Misc
cmp downloaded/hamlet.txt inputFilesForTesting/hamlet.txt
cmp downloaded/bigPhoto.jpg inputFilesForTesting/bigPhoto.jpg
