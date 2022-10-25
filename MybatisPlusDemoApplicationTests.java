package com.example.mybatis_plus_demo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mybatis_plus_demo.Service.UserService;
import com.example.mybatis_plus_demo.entity.Product;
import com.example.mybatis_plus_demo.entity.User;
import com.example.mybatis_plus_demo.mapper.ProductMapper;
import com.example.mybatis_plus_demo.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest
class MybatisPlusDemoApplicationTests {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductMapper productMapper;

    @Test
    void contextLoads() {
    }

    @Test
    public void testSelectList() {
        userMapper.selectList(null).forEach(System.out::println);
    }

    @Test
    public void testInsert() {
        //mybatis-plus在执行的时候，在插入数据的时候，会默认基于雪花算法的策略生成id
        User user = new User(null, "张三", 23, "zhangsan@qq.com");
        int result = userMapper.insert(user);
        System.out.println("受影响的行数:" + result);
        //1582178227948834818
        System.out.println("id自动获取:" + user.getId());
    }

    @Test
    public void testDeleteById() {
        int result = userMapper.deleteById(1582178227948834818L);
        System.out.println("受影响的行数:" + result);
    }

    @Test
    public void testDeleteBatchIds() {
        List<Long> IdList = Arrays.asList(1L, 2L, 3L);
        int result = userMapper.deleteBatchIds(IdList);
        System.out.println("受影响的行数:" + result);

    }

    @Test
    public void testDeleteByMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("age", 23);
        map.put("name", "张三");
        int result = userMapper.deleteByMap(map);
        System.out.println("受影响的行数:" + result);
    }

    @Test
    public void testUpdateById() {
        User user = new User(4L, "admin", 22, null);
        //byId 参数是entity
        int result = userMapper.updateById(user);
        System.out.println("受影响的行数:" + result);
    }

    @Test
    public void testSelectById() {
        User user = userMapper.selectById(4L);
        System.out.println(user);
        User user1 = userMapper.selectById(8L);
        System.out.println(user1);
    }

    @Test
    public void testSelectBatchIds() {
        List<Long> idList = Arrays.asList(4L, 5L);
        List<User> list = userMapper.selectBatchIds(idList);
        list.forEach(System.out::println);
    }

    @Test
    public void testSelectByMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("age", 22);
        map.put("name", "admin");
        List<User> list = userMapper.selectByMap(map);
        list.forEach(System.out::println);
    }

    @Test
    public void testSelectListAll() {
        List<User> list = userMapper.selectList(null);
        list.forEach(System.out::println);
    }

    @Test
    public void testGetCount() {
        long count = userService.count();
        System.out.println("总记录数:" + count);
    }

    @Test
    public void testSaveBatch() {
        ArrayList<User> users = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            User user = new User();
            user.setName("abc" + i);
            user.setAge(20 + i);
            users.add(user);
        }
        userService.saveBatch(users);
    }

    @Test
    public void test01() {
        //查询用户名包含a,年龄在20-30,邮箱不为null
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", "a")
                .between("age", 20, 30)
                .isNotNull("email");
        List<User> list = userMapper.selectList(queryWrapper);
        list.forEach(System.out::println);

    }

    @Test
    public void test02() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("age")
                .orderByAsc("id");
        List<User> list = userMapper.selectList(queryWrapper);
        list.forEach(System.out::println);
    }

    @Test
    public void test03() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("email");
        int result = userMapper.delete(queryWrapper);
        System.out.println("受影响的行数:" + result);
    }

    @Test
    public void test04() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", "a").gt("age", 20).or()
                .isNull("email");
        User user = new User();
        user.setAge(18);
        user.setEmail("jcahsy1314@qq.com");
        int reuslt = userMapper.update(user, queryWrapper);
        System.out.println("受影响的行数:" + reuslt);
    }

    @Test
    public void test05() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", "a")
                .and(user -> user.gt("age", 20).or()
                        .isNull("email"));
        User user = new User();
        user.setAge(18);
        user.setEmail("jcashy1314@qq.com");
        int result = userMapper.update(user, queryWrapper);
        System.out.println("受影响的行数:" + result);
    }


    @Test
    public void test06() {
        List<User> list = userMapper.selectList(new QueryWrapper<User>().select("name", "age"));
        list.forEach(System.out::println);

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("name", "age");
        List<Map<String, Object>> maps = userMapper.selectMaps(queryWrapper);
        maps.forEach(System.out::println);
    }

    @Test
    public void test07() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.inSql("id", "select id from user where id <= 3");
        List<User> list = userMapper.selectList(queryWrapper);
        list.forEach(System.out::println);
    }

    @Test
    public void test08() {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("age", 18)
                .set("email", "user@qq.com")
                .like("name", "a")
                .and(i -> i.gt("age", 20).or().isNull("email"));
        int result = userMapper.update(null, updateWrapper);
        System.out.println(result);
    }

    @Test
    public void test9() {
        String name = null;
        Integer ageBegin = 10;
        Integer ageEnd = 24;
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            queryWrapper.like("name", "a");
        }
        if (ageBegin != null) {
            queryWrapper.ge("age", ageBegin);
        }
        if (ageEnd != null) {
            queryWrapper.le("age", ageEnd);
        }
        List<User> users = userMapper.selectList(queryWrapper);
        users.forEach(System.out::println);
    }

    @Test
    public void test10() {
        String name = null;
        Integer ageBegin = 10;
        Integer ageEnd = 24;
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(name), "name", "a")
                .ge(ageBegin != null, "age", ageBegin)
                .le(ageEnd != null, "age", ageEnd);
        List<User> users = userMapper.selectList(queryWrapper);
        users.forEach(System.out::println);
    }

    @Test
    public void test11() {
        String name = "a";
        Integer ageBegin = 10;
        Integer ageEnd = 24;
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(name), User::getName, name)
                .ge(ageBegin != null, User::getAge, ageBegin)
                .le(ageEnd != null, User::getAge, ageEnd);
        List<User> list = userMapper.selectList(queryWrapper);
        list.forEach(System.out::println);
    }

    @Test
    public void test12() {
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(User::getAge, 18)
                .set(User::getEmail, "927901442@qq.com")
                .like(User::getName, "a")
                .and(i -> i.lt(User::getAge, 24).or().isNull(User::getEmail));
        User user = new User();
        int result = userMapper.update(user, updateWrapper);
        System.out.println("受影响的行数:" + result);

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        Map<String, Object> map = new HashMap<>();
        map.put("email", "927901442@qq.com");
        List<User> list = userMapper.selectByMap(map);
        list.forEach(System.out::println);

        System.out.println("第一次update");

        updateWrapper.set(User::getAge, 18)
                .set(User::getEmail, "927901442@qq.com")
                .like(User::getName, "a")
                .and(i -> i.lt(User::getAge, 24).or().isNull(User::getEmail));
        result = userMapper.update(null, updateWrapper);
        System.out.println("第二次修改受影响的行数:" + result);

        list = userMapper.selectByMap(map);
        list.forEach(System.out::println);
        System.out.println("第二次update");
    }

    @Test
    public void testPage() {
        Page<User> page = new Page<>(1, 5);
        userMapper.selectPage(page, null);
        List<User> list = page.getRecords();
        list.forEach(System.out::println);
        System.out.println("当前页:" + page.getCurrent());
        System.out.println("每页显示的条数:" + page.getSize());
        System.out.println("总记录数:" + page.getTotal());
        System.out.println("总页数:" + page.getPages());
        System.out.println("是否有上一页:" + page.hasPrevious());
        System.out.println("是否有下一页:" + page.hasNext());
    }

    @Test
    public void testSelectPageVo(){
        Page<User> page = new Page<>(1, 5);
        userMapper.selectPageVo(page,20);
        List<User> list = page.getRecords();
        list.forEach(System.out::println);
        System.out.println("当前页:" + page.getCurrent());
        System.out.println("每页显示的条数:" + page.getSize());
        System.out.println("总记录数:" + page.getTotal());
        System.out.println("总页数:" + page.getPages());
        System.out.println("是否有上一页:" + page.hasPrevious());
        System.out.println("是否有下一页:" + page.hasNext());
    }

    @Test
    public void testConcurrentUpdate(){
        Product p1 = productMapper.selectById(1L);
        System.out.println("小李取出的价格:"+p1.getPrice());

        Product p2 = productMapper.selectById(1L);
        System.out.println("小王取出的价格:"+p2.getPrice());

        p1.setPrice(p1.getPrice()+50);
        int result = productMapper.updateById(p1);
        System.out.println("小李修改结果:"+result);

        p2.setPrice(p2.getPrice()-30);
        int result2 = productMapper.updateById(p2);
        if (result2 == 0){
            p2 = productMapper.selectById(1L);
            p2.setPrice(p2.getPrice()-30);
            result2 = productMapper.updateById(p2);
        }
        System.out.println("小王修改重试的结果:"+result2);
        Product p3 = productMapper.selectById(1L);
        System.out.println("最后的结果"+p3.getPrice());
    }


}
