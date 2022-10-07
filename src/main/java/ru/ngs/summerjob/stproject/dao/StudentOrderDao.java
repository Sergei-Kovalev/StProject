package ru.ngs.summerjob.stproject.dao;

import ru.ngs.summerjob.stproject.domain.StudentOrder;
import ru.ngs.summerjob.stproject.exception.DaoException;

import java.util.List;

public interface StudentOrderDao {
    Long saveStudentOrder(StudentOrder so) throws DaoException;

    List<StudentOrder> getStudentOrders() throws DaoException;
}
