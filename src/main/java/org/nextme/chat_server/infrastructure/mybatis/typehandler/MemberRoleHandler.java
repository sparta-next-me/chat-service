package org.nextme.chat_server.infrastructure.mybatis.typehandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.nextme.chat_server.domain.chatRoom.RoomType;
import org.nextme.chat_server.domain.chatRoomMember.MemberRole;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(MemberRole.class)
public class MemberRoleHandler extends BaseTypeHandler<MemberRole> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, MemberRole parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.name());
    }

    @Override
    public MemberRole getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : MemberRole.valueOf(value);
    }

    @Override
    public MemberRole getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : MemberRole.valueOf(value);
    }

    @Override
    public MemberRole getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : MemberRole.valueOf(value);
    }
}
