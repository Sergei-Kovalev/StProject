package ru.ngs.summerjob.stproject.dao;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class SimpleRunner {
    public static void main(String[] args) {
        SimpleRunner sr = new SimpleRunner();
        sr.runTests();
    }

    private void runTests() {
        try {
            Class<?> cl = Class.forName("ru.ngs.summerjob.stproject.dao.DictionaryDaoImplTest");
            Constructor<?> cst = cl.getDeclaredConstructor();
            Object entity = cst.newInstance();
            Method[] methods = cl.getMethods();
            for (Method m : methods) {
                Test ann = m.getAnnotation(Test.class);
                if (ann != null) {
                    m.invoke(entity);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
