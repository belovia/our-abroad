package ru.belov.ourabroad.poi.storage.sql;

public final class SpecialistServiceSql {

    private SpecialistServiceSql() {
    }

    public static final String FIND_BY_ID = """
        select id,
               specialist_id,
               title,
               description,
               price,
               currency,
               active
        from specialist_service
        where id = :id
        """;

    public static final String FIND_BY_SPECIALIST_ID = """
        select id,
               specialist_id,
               title,
               description,
               price,
               currency,
               active
        from specialist_service
        where specialist_id = :specialistId
        order by title
        """;

    public static final String FIND_ACTIVE_BY_SPECIALIST_ID = """
        select id,
               specialist_id,
               title,
               description,
               price,
               currency,
               active
        from specialist_service
        where specialist_id = :specialistId
          and active = true
        order by title
        """;

    public static final String INSERT = """
        insert into specialist_service (
            id,
            specialist_id,
            title,
            description,
            price,
            currency,
            active
        ) values (
            :id,
            :specialistId,
            :title,
            :description,
            :price,
            :currency,
            :active
        )
        on conflict (id) do update set
            specialist_id = excluded.specialist_id,
            title = excluded.title,
            description = excluded.description,
            price = excluded.price,
            currency = excluded.currency,
            active = excluded.active
        """;

    public static final String DELETE = """
        delete from specialist_service
        where id = :id
        """;

    public static final String EXISTS_BY_ID = """
        select exists(
            select 1
            from specialist_service
            where id = :id
        )
        """;
}

