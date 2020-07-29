# sneaker-balloon

Dependencies: 
- hamcrest-core-1.3
- json-simple-1.1.1
- junit-4.13
- sqlite-jdbc-3.30.1

Compile (inside /src) with:

`javac -cp "../dependencies/*" *.java`

Run (inside /src) with:

`java -cp "../dependencies/*:." Cart ../input/products-data.json ../input/sale_rules.json <DATE>`

Date should be formatted as MM/dd/yy, i.e. "10/01/21".
