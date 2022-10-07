package ru.ngs.summerjob.stproject.validator.register;

import ru.ngs.summerjob.stproject.domain.Adult;
import ru.ngs.summerjob.stproject.domain.Child;
import ru.ngs.summerjob.stproject.domain.register.CityRegisterCheckerResponse;
import ru.ngs.summerjob.stproject.domain.Person;
import ru.ngs.summerjob.stproject.exception.CityRegisterException;
import ru.ngs.summerjob.stproject.exception.TransportException;

public class FakeCityRegisterChecker implements CityRegisterChecker {
    public static final String GOOD_1 = "1000";
    public static final String GOOD_2 = "2000";
    public static final String BAD_1 = "1001";
    public static final String BAD_2 = "2001";
    public static final String ERROR_1 = "1002";
    public static final String ERROR_2 = "2002";
    public static final String ERROR_T_1 = "1003";
    public static final String ERROR_T_2 = "2003";

    public CityRegisterCheckerResponse checkPerson(Person person) throws CityRegisterException, TransportException {
        CityRegisterCheckerResponse res = new CityRegisterCheckerResponse();
        if (person instanceof Adult) {
            Adult t = (Adult) person;
            if (t.getPassportSeria().equals(GOOD_1) || t.getPassportSeria().equals(GOOD_2)) {
                res.setExisting(true);
                res.setTemporal(false);
            }
            if (t.getPassportSeria().equals(BAD_1) || t.getPassportSeria().equals(BAD_2)) {
                res.setExisting(false);
            }
            if (t.getPassportSeria().equals(ERROR_1) || t.getPassportSeria().equals(ERROR_2)) {
                CityRegisterException ex = new CityRegisterException("1", "GRN Error " + t.getPassportSeria());
                throw ex;
            }
            if (t.getPassportSeria().equals(ERROR_T_1) || t.getPassportSeria().equals(ERROR_T_2)) {
                TransportException ex = new TransportException("Transport Error " + t.getPassportSeria());
                throw ex;
            }
        }
        if (person instanceof Child) {
            res.setExisting(true);
            res.setTemporal(true);
        }
        System.out.println(res);
        return res;
    }
}
