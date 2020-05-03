# zwSqliteTest
基于springboot +Sqlite

虽然项目中实现了读写分离,但是读写分离并不适合sqlite数据库,读写分离只会造成锁库报错问题,生产单连接+wal就可以了,现在已经将读写分离的注解给注释了,不再是多数据源了
