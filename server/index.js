require('dotenv/config');
const express = require('express');
const bodyParser = require('body-parser')
const usersRouter = require("./api/user");
const {initFirebase} = require("./config/config");

initFirebase();
const app = express();
app.use(bodyParser.json());

app.use("/users", usersRouter);

app.listen(process.env.PORT, () =>
    console.log(`Listening on Port ${process.env.PORT}`)
);