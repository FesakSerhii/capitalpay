const { Pool } = require("pg");
function createDBConnection() {
    const pool = new Pool({
        user: "payserver",
        password: "UuDB2GfTd42RXJjk",
        host: "185.111.106.6",
        database: "merchant_test",
        connectionTimeoutMillis: 10000
    });
    createTables(pool);
    return pool;
}

async function createTables(pool) {
    await pool.query(`
        create table if not exists cards (
           id SERIAL primary key not null,
           card_number varchar(50) not null,
           card_token varchar(255) not null,
           card_id int not null,
           param varchar(255) null
        )
    `);
    await pool.query(`
        create table if not exists payments (
            id serial primary key not null,
            card_id integer not null,
            sum decimal(10,2) not null,
            result text
        )
    `);
    await pool.query(`
        create table if not exists client_payments (
            id serial primary key not null,
            card_id integer not null,
            sum decimal(10,2) not null,
            result text
        )
    `)
}
module.exports = { createDBConnection };
