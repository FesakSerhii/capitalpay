import {createDBConnection} from "./db.init.js";
import { fileURLToPath } from 'url';
import { dirname } from 'path';
import * as crypto from "crypto";

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);
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
    res.render("cards", {cards});
});

app.get("/success", function (req, res) {
    res.send(`<h1>Success</h1>`)
})

app.post("/inter", jsonParser, async function (req, res) {
    console.log("Adding card", req.body);
    const cardData = req.body.data;
    const recalculatedSignature = hash256(cardData.cardId + cardData.token + cardData.cardNumber + "VfTUVSQ0hBTlQiLCJST0");
    console.log({recalculatedSignature, signature: cardData.signature});
    if (recalculatedSignature !== cardData.signature) {
        res.status(422).send("Fail");
        return;
    }
    await db.query("insert into cards (card_number, card_id, card_token) values ($1, $2, $3)", [
        cardData.cardNumber, cardData.cardId, cardData.token
    ]);
    res.send("OK");
})

app.listen(port, () => {
    console.log(`Example app listening at http://localhost:${port}`)
})
