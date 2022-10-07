package ru.ngs.summerjob.stproject.validator.register;

import ru.ngs.summerjob.stproject.domain.Person;
import ru.ngs.summerjob.stproject.domain.register.CityRegisterResponse;
import ru.ngs.summerjob.stproject.exception.CityRegisterException;

public interface CityRegisterChecker {
    CityRegisterResponse checkPerson(Person person) throws CityRegisterException;
}
