package ru.belov.ourabroad.poi.storage.healthcheck;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DbHealthCheck {


    public DbHealthCheck(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.execute("select 1");
    }
}
