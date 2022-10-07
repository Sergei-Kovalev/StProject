package ru.ngs.summerjob.stproject.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ngs.summerjob.stproject.config.Config;
import ru.ngs.summerjob.stproject.domain.*;
import ru.ngs.summerjob.stproject.domain.RegisterOffice;
import ru.ngs.summerjob.stproject.exception.DaoException;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StudentOrderDaoImpl implements StudentOrderDao {

    private static final Logger logger = LoggerFactory.getLogger(StudentOrderDaoImpl.class);

    private static final String INSERT_ORDER =
            "INSERT INTO jc_student_order(\n" +
                    "\tstudent_order_status, student_order_date, h_sur_name, " +
                    "h_given_name, h_patronymic_name, h_date_of_birth, h_passport_seria, h_passport_number, " +
                    "h_passport_date, h_passport_office_id, h_post_index, h_street_code, h_building, h_extension, " +
                    "h_apartment, h_university_id, h_student_number, w_sur_name, w_given_name, w_patronymic_name, w_date_of_birth, w_passport_seria, " +
                    "w_passport_number, w_passport_date, w_passport_office_id, w_post_index, w_street_code, w_building, " +
                    "w_extension, w_apartment, w_university_id, w_student_number, certificate_id, register_office_id, marriage_date)\n" +
                    "\tVALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private static final String INSERT_CHILD =
            "INSERT INTO jc_student_child(\n" +
                    "\tstudent_order_id, c_sur_name, c_given_name, c_patronymic_name, c_date_of_birth, " +
                    "c_certificate_number, c_certificate_date, c_register_office_id, " +
                    "c_post_index, c_street_code, c_building, c_extension, c_apartment)\n" +
                    "\tVALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private static final String SELECT_ORDERS =
            "SELECT so.*, ro.r_office_area_id, ro.r_office_name, \n" +
                    "po_h.p_office_area_id AS h_p_office_area_id, po_h.p_office_name AS h_p_office_name, \n" +
                    "po_w.p_office_area_id AS w_p_office_area_id, po_w.p_office_name AS w_p_office_name\n" +
                    "FROM jc_student_order so \n" +
                    "INNER JOIN jc_register_office ro ON ro.r_office_id = so.register_office_id \n" +
                    "INNER JOIN jc_passport_office po_h ON po_h.p_office_id = so.h_passport_office_id \n" +
                    "INNER JOIN jc_passport_office po_w ON po_w.p_office_id = so.w_passport_office_id \n" +
                    "WHERE student_order_status = ? ORDER BY student_order_date LIMIT ?";

    private static final String SELECT_CHILD =
            "SELECT soc.*, ro.r_office_area_id, ro.r_office_name\n" +
                    "FROM jc_student_child soc\n" +
                    "INNER JOIN jc_register_office ro ON soc.c_register_office_id = ro.r_office_id\n" +
                    "WHERE student_order_id IN ";

    private static final String SELECT_ORDERS_FULL =
            "SELECT so.*, ro.r_office_area_id, ro.r_office_name, \n" +
                    "po_h.p_office_area_id AS h_p_office_area_id, po_h.p_office_name AS h_p_office_name, \n" +
                    "po_w.p_office_area_id AS w_p_office_area_id, po_w.p_office_name AS w_p_office_name,\n" +
                    "soc.*, ro_c.r_office_area_id, ro_c.r_office_name\n" +
                    "FROM jc_student_order so \n" +
                    "INNER JOIN jc_register_office ro ON ro.r_office_id = so.register_office_id \n" +
                    "INNER JOIN jc_passport_office po_h ON po_h.p_office_id = so.h_passport_office_id \n" +
                    "INNER JOIN jc_passport_office po_w ON po_w.p_office_id = so.w_passport_office_id \n" +
                    "INNER JOIN jc_student_child soc ON soc.student_order_id = so.student_order_id \n" +
                    "INNER JOIN jc_register_office ro_c ON soc.c_register_office_id = ro_c.r_office_id\n" +
                    "WHERE student_order_status = ? ORDER BY so.student_order_id LIMIT ?";

    private Connection getConnection() throws SQLException {
        return ConnectionBuilder.getConnection();
    }

    @Override
    public Long saveStudentOrder(StudentOrder so) throws DaoException {
        logger.trace("saveStudentOrder() start");

        Long result = -1L;

        logger.debug("SO:{}", so);

        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(INSERT_ORDER, new String[]{"student_order_id"})) { //получение id для только что созданной записи

            con.setAutoCommit(false);

            try {
                //Head
                stmt.setInt(1, StudentOrderStatus.START.ordinal());
                stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));

                //Husband
                setParamsForAdult(stmt, 3, so.getHusband());

                //Wife
                setParamsForAdult(stmt, 18, so.getWife());

                //Marriage
                stmt.setString(33, so.getMarriageCertificateId());
                stmt.setLong(34, so.getMarriageOffice().getOfficeId());
                stmt.setDate(35, Date.valueOf(so.getMarriageDate()));

                stmt.executeUpdate();
                ResultSet gkRs = stmt.getGeneratedKeys();
                if (gkRs.next()) {
                    result = gkRs.getLong(1);                       // возвращаем результат в виде генерируемого поля id (см. выше)
                }
                gkRs.close();

                saveChildren(con, so, result);
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DaoException(e);
        }
        return result;
    }

    private void saveChildren(Connection con, StudentOrder so, Long soId) throws SQLException {
        logger.trace("saveChildren() start");
        try (PreparedStatement stmt = con.prepareStatement(INSERT_CHILD)) {
            for (Child child : so.getChildren()) {
                stmt.setLong(1, soId);
                setParamsForChild(stmt, child);
                stmt.addBatch();                                    // для поучиться ^_^ складываем всех в пакет
            }
            stmt.executeBatch();                                    // записываем пакет
        }
    }

    private void setParamsForChild(PreparedStatement stmt, Child child) throws SQLException {
        logger.trace("setParamsForChild() start");
        setParamsForPerson(stmt, 2, child);
        stmt.setString(6, child.getCertificateNumber());
        stmt.setDate(7, Date.valueOf(child.getIssueDate()));
        stmt.setLong(8, child.getIssueDepartment().getOfficeId());
        setParamsForAddress(stmt, 1, child);
    }

    private void setParamsForAdult(PreparedStatement stmt, int start, Adult adult) throws SQLException {
        logger.trace("setParamsForAdult() start");
        setParamsForPerson(stmt, start, adult);
        stmt.setString(start + 4, adult.getPassportSeria());
        stmt.setString(start + 5, adult.getPassportNumber());
        stmt.setDate(start + 6, Date.valueOf(adult.getIssueDate()));
        stmt.setLong(start + 7, adult.getIssueDepartment().getOfficeId());
        setParamsForAddress(stmt, start, adult);
        stmt.setLong(start + 13, adult.getUniversity().getUniversityId());
        stmt.setString(start + 14, adult.getStudentId());
    }

    private void setParamsForAddress(PreparedStatement stmt, int start, Person person) throws SQLException {
        logger.trace("setParamsForAddress() start");
        Address address = person.getAddress();
        stmt.setString(start + 8, address.getPostCode());
        stmt.setLong(start + 9, address.getStreet().getStreetCode());
        stmt.setString(start + 10, address.getBuilding());
        stmt.setString(start + 11, address.getExtension());
        stmt.setString(start + 12, address.getApartment());
    }

    private void setParamsForPerson(PreparedStatement stmt, int start, Person person) throws SQLException {
        logger.trace("setParamsForPerson() start");
        stmt.setString(start, person.getSurName());
        stmt.setString(start + 1, person.getGivenName());
        stmt.setString(start + 2, person.getPatronymic());
        stmt.setDate(start + 3, Date.valueOf(person.getDateOfBirth()));
    }


    @Override
    public List<StudentOrder> getStudentOrders() throws DaoException {
        logger.trace("getStudentOrders() start");
//        return getStudentOrdersTwoSelect();
        return getStudentOrdersOneSelect();
    }

    private List<StudentOrder> getStudentOrdersOneSelect() throws DaoException {
        logger.trace("getStudentOrdersOneSelect() start");
        List<StudentOrder> result = new LinkedList<>();
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(SELECT_ORDERS_FULL)) {

            Map<Long, StudentOrder> maps = new HashMap<>();
            int limit = Integer.parseInt(Config.getProperty(Config.DB_LIMIT));
            stmt.setInt(1, StudentOrderStatus.START.ordinal());
            stmt.setInt(2, limit);

            ResultSet rs = stmt.executeQuery();
            int count = 0;
            while (rs.next()) {
                Long soId = rs.getLong("student_order_id");
                if (!maps.containsKey(soId)) {
                    StudentOrder so = getFullStudentOrder(rs);

                    result.add(so);
                    maps.put(soId, so);
                }
                StudentOrder so = maps.get(soId);               
                so.addChild(fillChild(rs));
                count++;
            }
            if (count >= limit) {
                result.remove(result.size() - 1);
            }

            rs.close();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DaoException(e);
        }
        return result;
    }

    private List<StudentOrder> getStudentOrdersTwoSelect() throws DaoException {
        logger.trace("getStudentOrdersTwoSelect() start");
        List<StudentOrder> result = new LinkedList<>();
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(SELECT_ORDERS)) {

            stmt.setInt(1, StudentOrderStatus.START.ordinal());
            stmt.setInt(2, Integer.parseInt(Config.getProperty(Config.DB_LIMIT)));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                StudentOrder so = getFullStudentOrder(rs);
                result.add(so);
            }
            findChildren(con, result);

            rs.close();

        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DaoException(e);
        }
        return result;
    }

    private StudentOrder getFullStudentOrder(ResultSet rs) throws SQLException {
        logger.trace("getFullStudentOrder() start");
        StudentOrder so = new StudentOrder();
        fillStudentOrder(rs, so);
        fillMarriage(rs, so);

        so.setHusband(fillAdult(rs, "h_"));
        so.setWife(fillAdult(rs, "w_"));
        return so;
    }


    private void findChildren(Connection con, List<StudentOrder> result) throws SQLException {
        logger.trace("findChildren() start");
        String cl = "(" + result.stream().map(so -> String.valueOf(so.getStudentOrderID()))
                .collect(Collectors.joining(",")) + ")";

        Map<Long, StudentOrder> maps = result.stream().collect(Collectors.toMap(so -> so.getStudentOrderID(), so -> so));

        try (PreparedStatement stmt = con.prepareStatement(SELECT_CHILD + cl)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Child ch = fillChild(rs);

                StudentOrder so = maps.get(rs.getLong("student_order_id"));
                so.addChild(ch);
            }
        }
    }

    private Child fillChild(ResultSet rs) throws SQLException {
        logger.trace("findChild() start");
        String surName = rs.getString("c_sur_name");
        String givenName = rs.getString("c_given_name");
        String patronymic = rs.getString("c_patronymic_name");
        LocalDate dateOfBirth = rs.getDate("c_date_of_birth").toLocalDate();

        Child child = new Child(surName, givenName, patronymic, dateOfBirth);
        child.setCertificateNumber(rs.getString("c_certificate_number"));
        child.setIssueDate(rs.getDate("c_certificate_date").toLocalDate());

        Long officeId = rs.getLong("c_register_office_id");
        String officeAreaId = rs.getString("r_office_area_id");
        String officeName = rs.getString("r_office_name");
        RegisterOffice ro = new RegisterOffice(officeId, officeAreaId, officeName);
        child.setIssueDepartment(ro);

        Address adr = new Address();
        adr.setPostCode(rs.getString("c_post_index"));
        Street street = new Street(rs.getLong("c_street_code"), "");
        adr.setStreet(street);
        adr.setBuilding(rs.getString("c_building"));
        adr.setExtension(rs.getString("c_extension"));
        adr.setApartment(rs.getString("c_apartment"));

        child.setAddress(adr);

        return child;
    }

    private Adult fillAdult(ResultSet rs, String pref) throws SQLException {
        logger.trace("fillAdult() start");
        Adult adult = new Adult();
        adult.setSurName(rs.getString(pref + "sur_name"));
        adult.setGivenName(rs.getString(pref + "given_name"));
        adult.setPatronymic(rs.getString(pref + "patronymic_name"));
        adult.setDateOfBirth(rs.getDate(pref + "date_of_birth").toLocalDate());
        adult.setPassportSeria(rs.getString(pref + "passport_seria"));
        adult.setPassportNumber(rs.getString(pref + "passport_number"));
        adult.setIssueDate(rs.getDate(pref + "passport_date").toLocalDate());

        Long officeId = rs.getLong(pref + "passport_office_id");
        String officeAreaId = rs.getString(pref + "p_office_area_id");
        String officeName = rs.getString(pref + "p_office_name");
        PassportOffice po = new PassportOffice(officeId, officeAreaId, officeName);
        adult.setIssueDepartment(po);

        Address adr = new Address();
        adr.setPostCode(rs.getString(pref + "post_index"));
        Street street = new Street(rs.getLong(pref + "street_code"), "");
        adr.setStreet(street);
        adr.setBuilding(rs.getString(pref + "building"));
        adr.setExtension(rs.getString(pref + "extension"));
        adr.setApartment(rs.getString(pref + "apartment"));

        adult.setAddress(adr);

        University un = new University(rs.getLong(pref + "university_id"), "");
        adult.setUniversity(un);
        adult.setStudentId(rs.getString(pref + "student_number"));
        return adult;
    }

    private void fillStudentOrder(ResultSet rs, StudentOrder so) throws SQLException {
        logger.trace("fillStudentOrder() start");
        so.setStudentOrderID(rs.getLong("student_order_id"));
        so.setStudentOrderDate(rs.getTimestamp("student_order_date").toLocalDateTime());
        so.setStudentOrderStatus(StudentOrderStatus.fromValue(rs.getInt("student_order_status")));
    }

    private void fillMarriage(ResultSet rs, StudentOrder so) throws SQLException {
        logger.trace("fillMarriage() start");
        so.setMarriageCertificateId(rs.getString("certificate_id"));
        so.setMarriageDate(rs.getDate("marriage_date").toLocalDate());

        Long roId = rs.getLong("register_office_id");
        String areaId = rs.getString("r_office_area_id");
        String officeName = rs.getString("r_office_name");
        RegisterOffice ro = new RegisterOffice(roId, areaId, officeName);
        so.setMarriageOffice(ro);
    }
}
