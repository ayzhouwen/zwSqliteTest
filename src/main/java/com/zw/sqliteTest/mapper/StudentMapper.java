package com.zw.sqliteTest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zw.sqliteTest.entity.Student;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {

}
