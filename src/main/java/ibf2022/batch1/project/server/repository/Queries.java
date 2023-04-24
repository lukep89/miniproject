package ibf2022.batch1.project.server.repository;

public class Queries {

        public static final String SQL_GET_USER_BY_EMAIL = """
                        select * from user
                        where email = ?;
                        """;

        public static final String SQL_INSERT_TO_USER_TABLE = """
                        insert into user
                                (name, contact_number, email, password, status, role)
                                value
                                (?,?, ?,?,?,?);
                        """;

        public static final String SQL_GET_ALL_WHERE_ROLE_USER = """
                        select * from user
                        where role = 'user';
                        """;
}
