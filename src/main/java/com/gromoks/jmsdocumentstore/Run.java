package com.gromoks.jmsdocumentstore;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Run {
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("/spring/root-context.xml");
    }
}
