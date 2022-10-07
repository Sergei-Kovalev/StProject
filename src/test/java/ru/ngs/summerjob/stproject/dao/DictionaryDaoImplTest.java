package ru.ngs.summerjob.stproject.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.*;
import ru.ngs.summerjob.stproject.domain.CountryArea;
import ru.ngs.summerjob.stproject.domain.PassportOffice;
import ru.ngs.summerjob.stproject.domain.Street;
import ru.ngs.summerjob.stproject.domain.RegisterOffice;
import ru.ngs.summerjob.stproject.exception.DaoException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

class DictionaryDaoImplTest {

    private static final Logger logger = LoggerFactory.getLogger(DictionaryDaoImplTest.class);

    @BeforeAll
    public static void startUp() throws URISyntaxException, IOException, SQLException {
       DBInit.startUp();
    }

    @Test
    public void testStreet() throws DaoException {
        LocalDateTime ld = LocalDateTime.now();
        logger.info("TEST " + ld);
        List<Street> d = new DictionaryDaoImpl().findStreets("pros");
        Assertions.assertEquals(2, d.size());
    }
    @Test
    public void testPassportOffice() throws DaoException {
        List<PassportOffice> po = new DictionaryDaoImpl().findPassportOffice("010020000000");
        Assertions.assertEquals(2, po.size());
    }
    @Test
    public void testRegisterOffice() throws DaoException {
        List<RegisterOffice> ro = new DictionaryDaoImpl().findRegisterOffice("010010000000");
        Assertions.assertEquals(2, ro.size());
    }
    @Test
    public void testArea() throws DaoException {
        List<CountryArea> ca1 = new DictionaryDaoImpl().findAreas("");
        Assertions.assertEquals(2, ca1.size());
        List<CountryArea> ca2 = new DictionaryDaoImpl().findAreas("020000000000");
        Assertions.assertEquals(2, ca2.size());
        List<CountryArea> ca3 = new DictionaryDaoImpl().findAreas("020010000000");
        Assertions.assertEquals(2, ca3.size());
        List<CountryArea> ca4 = new DictionaryDaoImpl().findAreas("020010010000");
        Assertions.assertEquals(2, ca4.size());
    }

}