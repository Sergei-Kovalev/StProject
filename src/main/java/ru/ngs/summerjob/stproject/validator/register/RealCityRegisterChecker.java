package ru.ngs.summerjob.stproject.validator.register;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import ru.ngs.summerjob.stproject.config.Config;
import ru.ngs.summerjob.stproject.domain.Person;
import ru.ngs.summerjob.stproject.domain.register.CityRegisterRequest;
import ru.ngs.summerjob.stproject.domain.register.CityRegisterResponse;
import ru.ngs.summerjob.stproject.exception.CityRegisterException;

public class RealCityRegisterChecker implements CityRegisterChecker {
    public CityRegisterResponse checkPerson(Person person) throws CityRegisterException {

        try {
            CityRegisterRequest request = new CityRegisterRequest(person);
            Client client = ClientBuilder.newClient();
            CityRegisterResponse response = client.target(Config.getProperty(Config.CR_URL))
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(request, MediaType.APPLICATION_JSON))
                    .readEntity(CityRegisterResponse.class);

        return response;
        } catch (Exception e) {
            throw new CityRegisterException("1", e.getMessage(), e);
        }
    }
}
