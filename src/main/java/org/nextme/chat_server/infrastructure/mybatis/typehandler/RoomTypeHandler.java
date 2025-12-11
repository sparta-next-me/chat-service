package org.nextme.chat_server.infrastructure.mybatis.typehandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.nextme.chat_server.domain.chatRoom.RoomType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(RoomType.class)
public class RoomTypeHandler extends BaseTypeHandler<RoomType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, RoomType parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.name());
    }

    @Override
    public RoomType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : RoomType.valueOf(value);
    }

    @Override
    public RoomType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : RoomType.valueOf(value);
    }

    @Override
    public RoomType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : RoomType.valueOf(value);
    }
}
