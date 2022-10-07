package ru.ngs.summerjob.stproject.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ngs.summerjob.stproject.domain.CountryArea;
import ru.ngs.summerjob.stproject.domain.PassportOffice;
import ru.ngs.summerjob.stproject.domain.Street;
import ru.ngs.summerjob.stproject.domain.RegisterOffice;
import ru.ngs.summerjob.stproject.exception.DaoException;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class DictionaryDaoImpl implements DictionaryDao {
    public static final Logger logger = LoggerFactory.getLogger(DictionaryDaoImpl.class);

    private static final String GET_STREET = "SELECT street_code, street_name " +
            "FROM jc_street WHERE UPPER(street_name) LIKE UPPER(?)";
    private static final String GET_PASSPORT = "SELECT * " +
            "FROM jc_passport_office WHERE p_office_area_id = ?";
    private static final String GET_REGISTER = "SELECT * " +
            "FROM jc_register_office WHERE r_office_area_id = ?";
    private static final String GET_AREA = "SELECT * " +
            "FROM jc_country_struct WHERE area_id LIKE ? AND area_id <> ?";

    private Connection getConnection() throws SQLException {
        return ConnectionBuilder.getConnection();
    }

    public List<Street> findStreets(String pattern) throws DaoException {
        List<Street> result = new LinkedList<>();

        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(GET_STREET)) {

            stmt.setString(1, "%" + pattern + "%");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Street street = new Street(rs.getLong("street_code"), rs.getString("street_name"));
                result.add(street);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DaoException(e);
        }

        return result;
    }

    @Override
    public List<PassportOffice> findPassportOffice(String areaId) throws DaoException {
        List<PassportOffice> result = new LinkedList<>();

        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(GET_PASSPORT)) {

            stmt.setString(1, areaId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                PassportOffice street = new PassportOffice(
                        rs.getLong("p_office_id"),
                        rs.getString("p_office_area_id"),
                        rs.getString("p_office_name"));
                result.add(street);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DaoException(e);
        }
        return result;
    }

    @Override
    public List<RegisterOffice> findRegisterOffice(String areaId) throws DaoException {
        List<RegisterOffice> result = new LinkedList<>();

        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(GET_REGISTER)) {

            stmt.setString(1, areaId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                RegisterOffice street = new RegisterOffice(
                        rs.getLong("r_office_id"),
                        rs.getString("r_office_area_id"),
                        rs.getString("r_office_name"));
                result.add(street);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DaoException(e);
        }
        return result;
    }

    @Override
    public List<CountryArea> findAreas(String ariaId) throws DaoException {
        List<CountryArea> result = new LinkedList<>();

        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(GET_AREA)) {

            String param1 = buildParam(ariaId);
            String param2 = ariaId;

            stmt.setString(1, param1); // подставляем в первый знак "?" param1
            stmt.setString(2, param2); // подставляем во второй знак "?" param2

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                CountryArea area = new CountryArea(
                        rs.getString("area_id"),
                        rs.getString("area_name"));
                result.add(area);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DaoException(e);
        }
        return result;
    }

    private String buildParam(String ariaId) throws SQLException {
        if (ariaId == null || ariaId.trim().isEmpty()) {
            return "__" + "0000000000";
        } else if (ariaId.endsWith("0000000000")) {
            return ariaId.substring(0, 2) + "___" + "0000000";
        } else if (ariaId.endsWith("0000000")) {
            return ariaId.substring(0, 5) + "___0000";
        } else if (ariaId.endsWith("0000")) {
            return ariaId.substring(0, 8) + "____";
        }
        throw new SQLException("invalid parameter area_id: " + ariaId);
    }
}
