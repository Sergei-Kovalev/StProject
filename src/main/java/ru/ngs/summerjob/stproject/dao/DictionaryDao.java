package ru.ngs.summerjob.stproject.dao;

import ru.ngs.summerjob.stproject.domain.CountryArea;
import ru.ngs.summerjob.stproject.domain.PassportOffice;
import ru.ngs.summerjob.stproject.domain.Street;
import ru.ngs.summerjob.stproject.domain.RegisterOffice;
import ru.ngs.summerjob.stproject.exception.DaoException;

import java.util.List;

public interface DictionaryDao {
    List<Street> findStreets(String pattern) throws DaoException;
    List<PassportOffice> findPassportOffice(String ariaId) throws DaoException;
    List<RegisterOffice> findRegisterOffice(String ariaId) throws DaoException;
    List<CountryArea> findAreas(String ariaId) throws DaoException;
}
