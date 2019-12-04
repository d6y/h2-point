#  Extending H2 to add Point support

```
$ sbt
sbt:h2point> run
[info] running Example
DEBUG slick.jdbc.JdbcBackend.statement - Preparing statement: create table "TELEMETRY" ("ID" BIGINT NOT NULL PRIMARY KEY,"POS" GEOMETRY(POINT, 0) NOT NULL,"OTHER" GEOMETRY(POINT, 0))
DEBUG slick.jdbc.JdbcBackend.statement - Preparing statement: insert into "TELEMETRY" ("ID","POS","OTHER")  values (?,?,?)
DEBUG slick.jdbc.JdbcBackend.statement - Preparing statement: select "ID", "POS", "OTHER" from "TELEMETRY"
DEBUG slick.jdbc.StatementInvoker.result - /----+---------------+-------\
DEBUG slick.jdbc.StatementInvoker.result - | 1  | 2             | 3     |
DEBUG slick.jdbc.StatementInvoker.result - | ID | POS           | OTHER |
DEBUG slick.jdbc.StatementInvoker.result - |----+---------------+-------|
DEBUG slick.jdbc.StatementInvoker.result - | 1  | POINT (42 90) | NULL  |
DEBUG slick.jdbc.StatementInvoker.result - \----+---------------+-------/
Vector((1,POINT (42 90),None))
[success] Total time: 2 s, completed 4 Dec 2019, 17:01:44
```



