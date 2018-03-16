package netcracker.study.monopoly.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;
import java.util.Properties;

/**
 * This class is need in order for the hibernate to recognise jsonb type, that Postgres supports.
 * You need to set your own class name in properties and call setParameterValues(...) method.
 * Ex:
 * {@code @TypeDef(name = "jsonb", typeClass = JSONBUserType.class,
 * parameters = @Parameter(name = JSONBUserType.CLASS,
 * value = "netcracker.study.monopoly.db.model.json.Game"))}
 */
public class JSONBUserType implements UserType, ParameterizedType {

    public final static String CLASS = "CLASS";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private Class forClass;

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.JAVA_OBJECT};
    }

    @Override
    public Class returnedClass() {
        return forClass;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return Objects.hashCode(x);
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        try {
            final String json = rs.getString(names[0]);
            return json == null
                    ? null
                    : objectMapper.readValue(json, forClass);
        } catch (IOException ex) {
            throw new HibernateException(ex);
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        try {
            final String json = value == null ? null : objectMapper.writeValueAsString(value);
            st.setObject(index, json, Types.OTHER);
        } catch (JsonProcessingException ex) {
            throw new HibernateException(ex);
        }
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }


    /**
     * @param parameters - properties, that should contain key "CLASS"
     *                   and value - qualified class name of your own type
     */
    @Override
    public void setParameterValues(Properties parameters) {
        try {
            forClass = Class.forName(parameters.getProperty(CLASS));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
