import {createDBConnection} from "./db.init.js";
import { fileURLToPath } from 'url';
import { dirname } from 'path';
import * as crypto from "crypto";
import * as axios from 'axios'

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);
const secret = "VfTUVSQ0hBTlQiLCJST0";
import express from "express";
import bodyParser from "body-parser";
const app = express()
const port = 3000;
const jsonParser = bodyParser.json()
app.set("views", __dirname + "/views");
app.set("view engine", "ejs");

const db = createDBConnection();

function hash256(str) {
    return  crypto.createHash("sha256").update(str).digest('hex');
}

app.get("/", async function (req, res) {
    const cards = (await db.query("select * from cards")).rows;
    const payments = (await db.query("select * from payments p join cards c on c.card_id = p.card_id")).rows;
    res.render("cards", {cards, payments});
});

app.get("/success", function (req, res) {
    res.send(`
        <h1>Success</h1>
        <script>
            setTimeout(() => location.href = '/', 2000);
        </script>
    `);
})

app.get("/fail", function (req, res) {
    res.send(`<h1>Fail</h1>`)
})

app.post("/inter", jsonParser, async function (req, res) {
    console.log("Adding card", req.body);
    const cardData = req.body.data;

    const recalculatedSignature = hash256(cardData.cardId + cardData.token + cardData.cardNumber + secret);
    console.log({recalculatedSignature, signature: cardData.signature});
    if (recalculatedSignature !== cardData.signature) {
        res.status(422).send("Fail");
        return;
    }
    await db.query("insert into cards (card_number, card_id, card_token, param) values ($1, $2, $3, $4)", [
        cardData.cardNumber, cardData.cardId, cardData.token, cardData.params
    ]);
    res.send("OK");
});

app.post("/pay", jsonParser, async function (req, resp) {
    console.log("try to pay");
    const card = (await db.query("select * from cards where card_id=$1", [req.body.cardId])).rows[0];
    let sendMoneyObj = {
        "clientCardToken": card.card_token,
        "merchantId": 663,
        "acceptedSum": req.body.sum,
        "cashBoxId": 13978
    };
    sendMoneyObj.signature = hash256(sendMoneyObj.cashBoxId + sendMoneyObj.merchantId + card.card_token + secret);
    console.log("send money obj", sendMoneyObj);
    const cpResp = await axios.default.post("https://api.capitalpay.kz/api/v1/p2p/send-p2p-to-client", sendMoneyObj);
    await db.query("insert into payments (card_id, sum, result) values ($1, $2, $3)", [
        req.body.cardId,
        req.body.sum,
        JSON.stringify(cpResp.data, null, 2)
    ]);
    console.log(cpResp.data);
    resp.send("OK");
});

app.listen(port, () => {
    console.log(`Example app listening at http://localhost:${port}`);
})
