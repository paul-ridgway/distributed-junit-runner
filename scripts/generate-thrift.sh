#!/bin/bash
cd $( dirname "${BASH_SOURCE[0]}" )
cd ..
echo 'Clean up...'
rm -R src/main/java/io/ridgway/paul/tests/api
mkdir -p src/main/java/io/ridgway/paul/tests/api
echo 'Generate...'
thrift -gen java -out src/main/java src/main/thrift/test.thrift
echo 'Done'
