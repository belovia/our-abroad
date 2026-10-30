# SDD process

Цикл работы:

1. Агент задаёт вопросы по слайсу.
2. Ты отвечаешь.
3. Агент предлагает черновик спеки (`status: draft` / `review`).
4. Ты пишешь `approved` (или правки).
5. Спека фиксируется (`status: approved`) в этом каталоге.
6. При необходимости — agent skills под approved-спеку.
7. Только потом — код.

Правила:

- Один слайс за раз.
- Код только по `approved` спекам.
- Стек: Java 21, Spring Boot, JDBC, Liquibase, PostgreSQL, JWT (access), springdoc, Clean Architecture + use cases.
- Текущий код репозитория не источник истины, пока отдельно не решим migrate/rewrite.
