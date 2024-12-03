package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Service
public class DataSourceTestService {

    @Autowired
    @Qualifier("primaryDataSource")
    private DataSource primaryDataSource;

    @Autowired
    @Qualifier("secondaryDataSource")
    private DataSource secondaryDataSource;

    @PostConstruct
    public void init() {
        if (primaryDataSource == null) {
            System.err.println("Primary DataSource가 null입니다.");
        } else {
            testDataSourceConnections();
        }
    }

    public void testDataSourceConnections() {
        // Primary DataSource 테스트
        try (Connection connection = primaryDataSource.getConnection()) {
            System.out.println("Primary DataSource 연결 성공!");
        } catch (SQLException e) {
            System.err.println("Primary DataSource 연결 실패: " + e.getMessage());
        }

        // Secondary DataSource 테스트
        try (Connection connection = secondaryDataSource.getConnection()) {
            System.out.println("Secondary DataSource 연결 성공!");
        } catch (SQLException e) {
            System.err.println("Secondary DataSource 연결 실패: " + e.getMessage());
        }
    }
}