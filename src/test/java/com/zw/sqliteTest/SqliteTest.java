package com.zw.sqliteTest;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import com.zw.sqliteTest.entity.Student;
import com.zw.sqliteTest.mapper.StudentMapper;
import com.zw.sqliteTest.service.TestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest(classes = SqliteTestApplication.class)
/**
 * 极限性能能一定是一些多读+配置sqlite的wal模式
 */
class SqliteTest {

	@Autowired
	private StudentMapper studentMapper;

	@Autowired
	private TestService testService;
	@Test
	void contextLoads() {
		System.out.println(1);
	}

	/**多线程并发写入数据时,经常会出现SQLITE_BUSY The database file is locked,不支持并发写入,
	 * 这里为什么要用多线模拟,因为业务层很多时候都会用到多线程,主要是模拟生产环境,其实这些线程在写入的时候都被阻塞了,因为用的就是一个连接
	 * Cause: org.sqlite.SQLiteException: [SQLITE_BUSY]  The database file is locked (database is locked)
	 * @throws InterruptedException
	 *
	 * dell g3 笔记本固态下测试
	 * 执行1000条数据总耗时 7445毫秒 每条记录大概7,8毫秒,tps大概是135
	 * 执行10000条数据总耗时 :80214毫秒 每条记录大概8毫秒,tps大概是125
	 * 执行100000条数据总耗时 :80214毫秒 每条记录大概8毫秒,tps大概是125
	 * 执行100000条数据总耗时 :780002毫秒 每条记录大概7.8毫秒,tps大概是128
	 */
	@Test
	void insert()  {
		testService.insertTest(10000);
	}


	/**
	 * 随机读主键数据性能测试
	 * 多连接随机读主键操作执行完10000个记录耗时:399毫秒, 每条记录大概需要0.0399毫秒,tps大概:25000
	 * 单连接随机读主键操作执行完10000个记录耗时:1156毫秒, 每条记录大概需要0.1156毫秒,tps大概:8000
	 */
	@Test
	void select()  {
		testService.selectTest(10000);
	}

}
