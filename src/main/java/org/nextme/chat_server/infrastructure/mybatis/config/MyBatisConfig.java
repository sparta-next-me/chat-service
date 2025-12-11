package org.nextme.chat_server.infrastructure.mybatis.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.nextme.chat_server.infrastructure.mybatis.typehandler.MemberRoleHandler;
import org.nextme.chat_server.infrastructure.mybatis.typehandler.MemberStatusHandler;
import org.nextme.chat_server.infrastructure.mybatis.typehandler.RoomTypeHandler;
import org.nextme.chat_server.infrastructure.mybatis.typehandler.UUIDTypeHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
@MapperScan("org.nextme.chat_server.infrastructure.mybatis.mapper")
public class MyBatisConfig {

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);

        // XML 매퍼 위치 설정
        sessionFactory.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources("classpath:mapper/**/*.xml")
        );

        // TypeHandler 등록
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.getTypeHandlerRegistry().register(UUIDTypeHandler.class);
        configuration.getTypeHandlerRegistry().register(RoomTypeHandler.class);
        configuration.getTypeHandlerRegistry().register(MemberRoleHandler.class);
        configuration.getTypeHandlerRegistry().register(MemberStatusHandler.class);
        configuration.setMapUnderscoreToCamelCase(true); // snake_case → camelCase

        sessionFactory.setConfiguration(configuration);

        return sessionFactory.getObject();
    }
}
