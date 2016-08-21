#!/bin/bash
cd $( dirname "${BASH_SOURCE[0]}" )
cd ..
java -cp "../java-test-set/target/java-test-set-1.0-SNAPSHOT.jar:../java-test-set/target/java-test-set-1.0-SNAPSHOT-tests.jar:target/tests-1.0-SNAPSHOT.jar" io.ridgway.paul.tests.app.TestApp worker -h localhost
