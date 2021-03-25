package ru.ravel.downloadServer.Configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FileDaoImpl {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public String getFileNameBySecretKey(String secretKey) {
        return jdbcTemplate.queryForObject(
                "select concat(path, name) as name \n" +
                        "from files\n" +
                        "where secret_key like ?;",
                new Object[]{secretKey}, String.class
        );
    }

}
