package org.example;

import org.example.service.Service;
import org.example.service.ServiceImpl;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext cxc= new ClassPathXmlApplicationContext("applicationContext.xml");

//      DataSource datasource = (DataSource) cxc.getBean("datasource");
//        System.out.println(datasource);
//        System.out.println("===================================");
//        DataSource c3p0datasource = (DataSource) cxc.getBean("c3p0datasource");
//        System.out.println(c3p0datasource);
        ServiceImpl service = (ServiceImpl) cxc.getBean(Service.class);
        service.save();
    }

}
