# sneaker-balloon

Dependencies: 
- hamcrest-core-1.3
- json-simple-1.1.1
- junit-4.13
- sqlite-jdbc-3.30.1

Compile with:

`javac -cp "<INSERT CLASSPATH HERE>/*" *.java`

Run (inside /src) with:
java -cp "<INSERT CLASSPATH HERE>/*:.” Cart ../input/products-data.json ../input/sale_rules.json
