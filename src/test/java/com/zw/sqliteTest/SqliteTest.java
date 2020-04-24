package com.zw.sqliteTest;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import com.zw.sqliteTest.entity.Student;
import com.zw.sqliteTest.mapper.StudentMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest(classes = SqliteTestApplication.class)
class SqliteTest {

	@Autowired
	private StudentMapper studentMapper;
	@Test
	void contextLoads() {
		System.out.println(1);
	}



	/**多线程并发写入数据时,经常会出现SQLITE_BUSY The database file is locked,不支持并发写入,
	 * Cause: org.sqlite.SQLiteException: [SQLITE_BUSY]  The database file is locked (database is locked)
	 * @throws InterruptedException
	 * 执行完50000个记录耗时:365496秒
	 */
	@Test
	void insert() throws InterruptedException {
		System.setProperty("hikaricp.configurationFile","hikaricp.configurationFile");
		int num=10000;
		CountDownLatch cdt=new CountDownLatch(num);
		ExecutorService service=Executors.newFixedThreadPool(5);

		long starttime=System.currentTimeMillis();

		studentMapper.delete(null);
		for (int i = 0; i <num ; i++) {

			int finalI = i;
			service.execute(new Runnable() {
				@Override
				public void run() {

					try {
						Student student=new Student();
						student.setId(finalI);
						student.setName("zw"+ finalI);
						student.setAge(finalI *2);


							studentMapper.insert(student);

						Student student2= studentMapper.selectById(finalI-3);
						System.out.println(Thread.currentThread().getName()+"student2.getId:"+student2.getId());


						//System.out.println(Thread.currentThread().getName()+"执行任务:"+finalI+"完毕");
					} catch (Exception e) {
						System.out.println(Thread.currentThread().getName()+"执行任务:"+finalI+"失败");
						e.printStackTrace();
					} finally {
						cdt.countDown();
					}

				}
			});

		}
		cdt.await();

		System.out.println("执行完"+num+"个记录耗时:"+DateUtil.spendMs(starttime)+"秒");

	}

	@Test
	void select(){
		List list=studentMapper.selectList(null);
		System.out.println(list);
	}

}
