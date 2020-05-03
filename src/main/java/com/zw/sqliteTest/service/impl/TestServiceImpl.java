package com.zw.sqliteTest.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.zw.sqliteTest.entity.Student;
import com.zw.sqliteTest.mapper.StudentMapper;
import com.zw.sqliteTest.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class TestServiceImpl implements TestService {
    @Autowired
    private StudentMapper studentMapper;
    @Override
    public String insertTest(Integer num)  {
        String result="";
            CountDownLatch cdt=new CountDownLatch(num);
            ExecutorService service= Executors.newFixedThreadPool(5);

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

                            //Student student2= studentMapper.selectById(finalI-3);
                            //System.out.println(Thread.currentThread().getName()+"student2.getId:"+student2.getId());
                            //select(); //应用层多线程读写在使用一个连接的情况下很稳定,不很锁库

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
        try {
            cdt.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //间隔时间
        Double time= Convert.toDouble(DateUtil.spendMs(starttime));
        result="写操作执行完"+num+"个记录耗时:"
                + time+"毫秒"+", 每条记录大概需要"+ NumberUtil.decimalFormat("#.##",time/num)+"毫秒"+",tps大概:"+NumberUtil.decimalFormat("#.##",num/time*1000);
            System.out.println(result);
            return result;

    }

    @Override
    public String selectTest(Integer num)  {
        {
            String result="";
            CountDownLatch cdt=new CountDownLatch(num);
            Integer busThreadNum=Runtime.getRuntime().availableProcessors()*2;
            System.out.println("业务线程数:"+busThreadNum);
            ExecutorService service= Executors.newFixedThreadPool(busThreadNum);
            long starttime=System.currentTimeMillis();
            //获取学生表的总数量,用于随机数限制
            Integer rowcount=studentMapper.selectCount(null);
            for (int i = 0; i <num ; i++) {

                int finalI =i;
                service.execute(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            //随机获取学生数据
                            Student student=studentMapper.selectById(RandomUtil.randomInt(rowcount));
                            // log.info(student.toString());
                        } catch (Exception e) {
                            System.out.println(Thread.currentThread().getName()+"执行任务:"+finalI+"失败");
                            e.printStackTrace();
                        } finally {
                            cdt.countDown();
                        }

                    }
                });

            }
            try {
                cdt.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Double time= Convert.toDouble(DateUtil.spendMs(starttime));
            result="随机读主键操作执行完"+num+"个记录耗时:"
                    + time+"毫秒"+", 每条记录大概需要"+ NumberUtil.decimalFormat("#.##",time/num)+"毫秒"+",qps大概:"+NumberUtil.decimalFormat("#.##",num/time*1000);
            System.out.println(result);
            return result;

        }
    }
}
