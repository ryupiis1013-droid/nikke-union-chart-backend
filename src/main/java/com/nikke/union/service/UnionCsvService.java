package com.nikke.union.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UnionCsvService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UnionCsvService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // level,name,monster,damage,season,unionRank  (프론트 RAW 포맷과 동일)
    private static final String SQL_LATEST =
            """
            SELECT season, player_id, boss, damage, union_rank, level
            FROM nikke.union_damage
            ORDER BY season ASC, player_id ASC
            """;

    public String buildCsvString() {
        StringBuilder sb = new StringBuilder(4096);

        jdbcTemplate.query(SQL_LATEST, rs -> {
            int season     = rs.getInt("season");
            String name    = rs.getString("player_id");
            String monster = normalizeMonster(rs.getString("boss"));
            long damage    = rs.getLong("damage");
            int unionRank  = rs.getInt("union_rank");
            int level      = rs.getInt("level");

            // CSV 한 줄 생성
            sb.append(level).append(',')
                    .append(name).append(',')
                    .append(monster).append(',')
                    .append(damage).append(',')
                    .append(season).append(',')
                    .append(unionRank).append('\n');
        });

        return sb.toString();
    }


    public String getUnionCsvString() {
        List<String> lines = new ArrayList<>();
        //lines.add("level,player_id,boss,damage,season,union_rank");

        jdbcTemplate.query(SQL_LATEST, (rs) -> {
            lines.add(String.format(
                    "%d,%s,%s,%d,%d,%d",
                    rs.getInt("level"),
                    rs.getString("player_id"),
                    rs.getString("boss"),
                    rs.getLong("damage"),
                    rs.getInt("season"),
                    rs.getInt("union_rank")
            ));
        });

        return String.join("\n", lines);
    }

    private String normalizeMonster(String m) {
        if (m == null) return "";
        // '마테리얼h' → '마테리얼H' 로 통일
        return m.replace("마테리얼h", "마테리얼H");
    }
}
