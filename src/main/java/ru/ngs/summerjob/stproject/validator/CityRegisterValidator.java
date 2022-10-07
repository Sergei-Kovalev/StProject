package ru.ngs.summerjob.stproject.validator;

import ru.ngs.summerjob.stproject.domain.register.AnswerCityRegister;
import ru.ngs.summerjob.stproject.domain.register.AnswerCityRegisterItem;
import ru.ngs.summerjob.stproject.domain.Child;
import ru.ngs.summerjob.stproject.domain.register.CityRegisterCheckerResponse;
import ru.ngs.summerjob.stproject.domain.Person;
import ru.ngs.summerjob.stproject.domain.StudentOrder;
import ru.ngs.summerjob.stproject.exception.CityRegisterException;
import ru.ngs.summerjob.stproject.exception.TransportException;
import ru.ngs.summerjob.stproject.validator.register.CityRegisterChecker;
import ru.ngs.summerjob.stproject.validator.register.FakeCityRegisterChecker;

public class CityRegisterValidator {

    public static final String IN_CODE = "NO_GRN";

    private CityRegisterChecker personChecker;

    public CityRegisterValidator() {
        personChecker = new FakeCityRegisterChecker();
    }

    public AnswerCityRegister checkCityRegister(StudentOrder so) {
        AnswerCityRegister ans = new AnswerCityRegister();

        ans.addItem(checkPerson(so.getHusband()));
        ans.addItem(checkPerson(so.getWife()));
        for (Child ch : so.getChildren()) {
            ans.addItem(checkPerson(ch));
        }

        return ans;
    }

    private AnswerCityRegisterItem checkPerson(Person person) {
        AnswerCityRegisterItem.CityStatus status;
        AnswerCityRegisterItem.CityError error = null;
        try {
            CityRegisterCheckerResponse tmp = personChecker.checkPerson(person);
            status = tmp.isExisting() ? AnswerCityRegisterItem.CityStatus.YES : AnswerCityRegisterItem.CityStatus.NO;
        } catch (CityRegisterException e) {
            e.printStackTrace(System.out);
            status = AnswerCityRegisterItem.CityStatus.ERROR;
            error = new AnswerCityRegisterItem.CityError(e.getCode(), e.getMessage());
        } catch (TransportException e) {
            e.printStackTrace(System.out);
            status = AnswerCityRegisterItem.CityStatus.ERROR;
            error = new AnswerCityRegisterItem.CityError(IN_CODE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(System.out);
            status = AnswerCityRegisterItem.CityStatus.ERROR;
            error = new AnswerCityRegisterItem.CityError(IN_CODE, e.getMessage());
        }

        AnswerCityRegisterItem ans = new AnswerCityRegisterItem(person, status, error);
        return ans;
    }
}
