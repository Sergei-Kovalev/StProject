package ru.ngs.summerjob.stproject;

import ru.ngs.summerjob.stproject.dao.StudentOrderDaoImpl;
import ru.ngs.summerjob.stproject.domain.children.AnswerChildren;
import ru.ngs.summerjob.stproject.domain.register.AnswerCityRegister;
import ru.ngs.summerjob.stproject.domain.student.AnswerStudent;
import ru.ngs.summerjob.stproject.domain.wedding.AnswerWedding;
import ru.ngs.summerjob.stproject.domain.StudentOrder;
import ru.ngs.summerjob.stproject.exception.DaoException;
import ru.ngs.summerjob.stproject.mail.MailSender;
import ru.ngs.summerjob.stproject.validator.ChildrenValidator;
import ru.ngs.summerjob.stproject.validator.CityRegisterValidator;
import ru.ngs.summerjob.stproject.validator.StudentValidator;
import ru.ngs.summerjob.stproject.validator.WeddingValidator;

import java.util.LinkedList;
import java.util.List;

public class StudentOrderValidator {

    private CityRegisterValidator cityRegisterVal;
    private WeddingValidator weddingVal;
    private ChildrenValidator childrenVal;
    private StudentValidator studentVal;
    private MailSender mailSender;

    public StudentOrderValidator() {
        cityRegisterVal = new CityRegisterValidator();
        weddingVal = new WeddingValidator();
        childrenVal = new ChildrenValidator();
        studentVal = new StudentValidator();
        mailSender = new MailSender();
    }

    public static void main(String[] args) {
        StudentOrderValidator sov = new StudentOrderValidator();
        sov.checkAll();
    }

    public void checkAll() {
        try {
            List<StudentOrder> soList = readStudentOrders();

            for (StudentOrder so : soList) {
                System.out.println();
                checkOneOrder(so);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkOneOrder(StudentOrder so) {
        AnswerCityRegister cityAnswer = checkCityRegister(so);

//        AnswerWedding wedAnswer = checkWedding(so);
//        AnswerChildren childAnswer = checkChildren(so);
//        AnswerStudent studentAnswer = checkStudent(so);

//        sendMail(so);
    }

    public List<StudentOrder> readStudentOrders() throws DaoException {
        return new StudentOrderDaoImpl().getStudentOrders();
    }

    public AnswerCityRegister checkCityRegister(StudentOrder so) {
        return cityRegisterVal.checkCityRegister(so);
    }

    public AnswerWedding checkWedding(StudentOrder so) {
        return weddingVal.checkWedding(so);
    }

    public AnswerChildren checkChildren(StudentOrder so) {
        return childrenVal.checkChildren(so);
    }

    public AnswerStudent checkStudent(StudentOrder so) {
        return studentVal.checkStudent(so);
    }

    public void sendMail(StudentOrder so) {
        mailSender.sendMail(so);
    }
}
