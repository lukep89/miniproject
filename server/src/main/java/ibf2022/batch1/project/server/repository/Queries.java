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

        public static final String SQL_UPDATE_USER_STATUS_BY_EMAIL = """
                        update user set status = ?
                        where email = ?;
                        """;

        public static final String SQL_GET_USER_BY_ID = """
                        select * from user
                        where id = ?;
                        """;

        public static final String SQL_UPDATE_USER_STATUS_BY_ID = """
                        update user set status = ?
                        where id = ?;
                        """;

        public static final String SQL_GET_ALL_ADMIN = """
                        select * from user
                        where role = 'admin';
                        """;

        public static final String SQL_UPDATE_USER_PASSWORD = """
                        update user set password = ?, date_updated = now()
                        where email = ?;
                        """;

        // For Admin to get category
        public static final String SQL_GET_ALL_CATEGORY = """
                        select * from category
                        """;

        // For User to get category
        public static final String SQL_GET_ALL_CATEGORY_FOR_USER = """
                        select * from category as c
                        where c.id in
                        (select p.category_id
                        from product as p
                        where p.status = 'true');
                        """;

        public static final String SQL_INSERT_TO_CATEGORY_TABLE = """
                        insert into category
                                (name)
                                value
                                (?);
                        """;

        // // TODO: frontend will list all the category when adding new category
        // public static final String SQL_GET_CATEGORY_BY_NAME = """
        // select * from category
        // where name = ?;
        // """;

        public static final String SQL_GET_CATEGORY_BY_ID = """
                        select * from category
                        where id = ?;
                        """;

        public static final String SQL_UPDATE_CATEGORY = """
                        update category set name = ?
                        where id = ?;
                        """;

        public static final String SQL_INSERT_TO_PRODUCT_TABLE = """
                        insert into product
                           (name, description, price, status, category_id)
                           value
                           (?, ?, ?, ?, ?);
                           """;

        public static final String SQL_GET_ALL_PRODUCT = """
                        select p.id id , p.name name, p.description description,
                        p.price price, p.status status, p.category_id categoryId, c.name categoryName
                        from product p
                        inner join category c
                        on p.category_id = c.id;
                        """;

        public static final String SQL_GET_PRODUCT_BY_ID = """
                        select * from product
                        where id = ?;
                        """;

        public static final String SQL_UPDATE_PRODUCT_BY_ID = """
                        update product set name = ?, description = ?, price = ?, status = ?, category_id = ?
                        where id = ?;
                        """;

        public static final String SQL_DELETE_PRODUCT_BY_ID = """
                        delete from product where id = ?;
                        """;

        public static final String SQL_UPDATE_PRODUCT_STATUS_BY_ID = """
                        update product set status = ?
                        where id = ?;
                        """;

        public static final String SQL_GET_ACTIVE_PRODUCT_BY_CATEGORY_ID = """
                        select p.id id , p.name name
                        from product p
                        inner join category c
                        on p.category_id = c.id
                        where p.category_id = ? and status = 'active';
                        """;

        public static final String SQL_INSERT_TO_BILL_TABLE = """
                        insert into bill
                           (uuid, name, email, contact_number, payment_method,
                           total_amount, product_details, created_by)
                           value
                           (?, ?, ?, ?, ?, ?, ?, ?);
                           """;

        // For Admin to get all bills
        public static final String SQL_GET_ALL_BILL = """
                        select * from bill
                        """;

        // For User to get bill relating to user
        public static final String SQL_GET_ALL_BILL_BY_USERNAME = """
                        select * from bill
                         where created_by = ?
                         order by id desc;
                         """;

        public static final String SQL_GET_BILL_BY_ID = """
                        select * from bill
                        where id = ?;
                        """;

        public static final String SQL_DELETE_BILL_BY_ID = """
                        delete from bill where id = ?;
                        """;

        public static final String SQL_COUNT_CATEGORY = """
                        select count(*) from category;
                        """;

        public static final String SQL_COUNT_PRODUCT = """
                        select count(*) from product;
                        """;

        public static final String SQL_COUNT_BILL = """
                        select count(*) from bill;
                        """;
}
