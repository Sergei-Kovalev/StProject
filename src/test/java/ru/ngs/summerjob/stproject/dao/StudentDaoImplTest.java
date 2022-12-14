package ru.ngs.summerjob.stproject.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.ngs.summerjob.stproject.domain.*;
import ru.ngs.summerjob.stproject.domain.RegisterOffice;
import ru.ngs.summerjob.stproject.exception.DaoException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

class StudentDaoImplTest {

    @BeforeAll
    public static void startUp() throws URISyntaxException, IOException, SQLException {
        DBInit.startUp();
    }

    @Test
    void saveStudentOrder() throws DaoException {
        StudentOrder so = buildStudentOrder(10);
        Long id = new StudentOrderDaoImpl().saveStudentOrder(so);
    }

    @Test
     public void saveStudentOrderError() {
        Assertions.assertThrows(DaoException.class, () -> {
            StudentOrder so = buildStudentOrder(10);
            so.getHusband().setSurName(null);
            Long id = new StudentOrderDaoImpl().saveStudentOrder(so);
        });
    }

    @Test
    void getStudentOrders() throws DaoException {
        List<StudentOrder> list = new StudentOrderDaoImpl().getStudentOrders();
    }

    public StudentOrder buildStudentOrder(long id) {
        StudentOrder so = new StudentOrder();
        so.setStudentOrderID(id);
        so.setMarriageCertificateId("" + (123456000 + id));
        so.setMarriageDate(LocalDate.of(2016, 7, 4));

        RegisterOffice ro = new RegisterOffice(1L, "", "");
        so.setMarriageOffice(ro);

        Street street = new Street(1L, "Peramogi street");
        Address address = new Address("195000", street, "10", "2", "121");

        //husband
        Adult husband = new Adult("Vasiliev", "Pavel", "Nikolaevich", LocalDate.of(1995, 3, 18));
        husband.setPassportSeria("" + (1000 + id));
        husband.setPassportNumber("" + (100000 + id));
        husband.setIssueDate(LocalDate.of(2017, 9 , 15));

        PassportOffice po1 = new PassportOffice(1L, "", "");
        husband.setIssueDepartment(po1);
//        husband.setStudentId("" + (100000 + id));
        husband.setAddress(address);
        husband.setUniversity(new University(2L, ""));
        husband.setStudentId("HH12345");

        //wife
        Adult wife = new Adult("Vasilieva", "Irina", "Petrovna", LocalDate.of(1997, 8, 21));
        wife.setPassportSeria("" + (2000 + id));
        wife.setPassportNumber("" + (200000 + id));
        wife.setIssueDate(LocalDate.of(2018, 4 , 5));

        PassportOffice po2 = new PassportOffice(2L, "", "");
        wife.setIssueDepartment(po2);
//        wife.setStudentId("" + (200000 + id));
        wife.setAddress(address);
        wife.setUniversity(new University(1L, ""));
        wife.setStudentId("WW12345");

        //child
        Child child1 = new Child("Vasilieva", "Evgenia", "Pavlovna", LocalDate.of(2016, 1, 1));
        child1.setCertificateNumber("" + (300000 + id));
        child1.setIssueDate(LocalDate.of(2018, 7,  19));

        RegisterOffice ro2 = new RegisterOffice(2L, "", "");
        child1.setIssueDepartment(ro2);
        child1.setAddress(address);

        //child2
        Child child2 = new Child("Vasiliev", "Aleksandr", "Pavlovich", LocalDate.of(2018, 10, 24));
        child2.setCertificateNumber("" + (400000 + id));
        child2.setIssueDate(LocalDate.of(2018, 6,  11));

        RegisterOffice ro3 = new RegisterOffice(3L, "", "");
        child2.setIssueDepartment(ro3);
        child2.setAddress(address);

        so.setHusband(husband);
        so.setWife(wife);
        so.addChild(child1);
        so.addChild(child2);

        return so;
    }
}