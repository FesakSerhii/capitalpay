const { Pool } = require("pg");
function createDBConnection() {
    const pool = new Pool({
        user: "payserver",
        password: "UuDB2GfTd42RXJjk",
        host: "185.111.106.6",
        database: "merchant_test"
    });
    pool.query(`
        create table if not exists cards (
           id SERIAL primary key not null,
           card_number varchar(50) not null,
           card_token varchar(255) not null,
           card_id int not null
        )
    `);
    return pool;
}
module.exports = { createDBConnection };
