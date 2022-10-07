package ru.ngs.summerjob.stproject.validator.register;

import ru.ngs.summerjob.stproject.domain.register.CityRegisterCheckerResponse;
import ru.ngs.summerjob.stproject.domain.Person;
import ru.ngs.summerjob.stproject.exception.CityRegisterException;
import ru.ngs.summerjob.stproject.exception.TransportException;

public interface CityRegisterChecker {
    CityRegisterCheckerResponse checkPerson(Person person) throws CityRegisterException, TransportException;
}
