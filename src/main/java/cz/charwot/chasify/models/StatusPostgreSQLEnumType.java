package cz.charwot.chasify.models;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.*;

public class StatusPostgreSQLEnumType implements UserType<Status> {

    @Override
    public int getSqlType() {
        return Types.OTHER; // PostgreSQL ENUM
    }

    @Override
    public Class<Status> returnedClass() {
        return Status.class;
    }

    @Override
    public boolean equals(Status x, Status y) {
        return x == y;
    }

    @Override
    public int hashCode(Status x) {
        return x.hashCode();
    }

    @Override
    public Status nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner)
            throws SQLException {
        String name = rs.getString(position);
        return name == null ? null : Status.fromDb(name);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Status value, int index, SharedSessionContractImplementor session)
            throws SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            st.setObject(index, value.toString(), Types.OTHER);
        }
    }

    @Override
    public Status deepCopy(Status value) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Status value) {
        return value.name();
    }

    @Override
    public Status assemble(Serializable cached, Object owner) {
        return Status.valueOf((String) cached);
    }

    @Override
    public Status replace(Status original, Status target, Object owner) {
        return original;
    }
}

