package org.nextme.chat_server.infrastructure.mybatis.typehandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.nextme.chat_server.domain.chatRoomMember.MemberStatus;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(MemberStatus.class)
public class MemberStatusHandler extends BaseTypeHandler<MemberStatus> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, MemberStatus parameter, JdbcType jdbcType) throws SQLException {

    }

    @Override
    public MemberStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return null;
    }

    @Override
    public MemberStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public MemberStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return null;
    }
}
