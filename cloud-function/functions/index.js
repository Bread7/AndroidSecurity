const functions = require("firebase-functions");
const cors = require("cors");
const express = require("express");
const compression = require("compression");
const { StreamChat } = require('stream-chat');

// Express app config
const tasksApp = express();
tasksApp.use(compression());
tasksApp.use(express.json()); // Updated to use built-in method
tasksApp.use(express.urlencoded({ extended: false })); // Updated to use built-in method
tasksApp.use(cors({ origin: true }));

// A simple API to get all tasks
tasksApp.get("/", (request, response) => {
    response.status(200).send("Hello from Firebase!");
});

// Define your API key and secret here (ideally, these should be stored securely, not in plain code)
const api_key = 'fe4bycj2hda3';
const api_secret = '6yudrrguuq9vxrfanaesggvzmmdc8pe733kcc8g5xp48ye9bqfc53sfweaw9ynry';

// Initialize a Server Client
const serverClient = StreamChat.getInstance(api_key, api_secret);

// Endpoint to create and return a token
tasksApp.post("/createToken", async (request, response) => {
    const { user_id } = request.body;

    if (!user_id) {
        return response.status(400).send({ error: "user_id is required" });
    }

    try {
    
        // Create User Token
        const token = serverClient.createToken(user_id);

        response.status(200).send({ token });
    } catch (error) {
        console.error("Error creating token:", error);
        response.status(500).send({ error: "Internal server error" });
    }
});

// Endpoint to upsert a user
tasksApp.post("/upsertUser", async (request, response) => {
    const { user_id, name, email, role } = request.body;

    if (!user_id) {
        return response.status(400).send({ error: "user_id is required" });
    }

    if (!name) {
        return response.status(400).send({ error: "name is required" });
    }

    if (!email) {
        return response.status(400).send({ error: "email is required" });
    }

    if (role !== "admin" && role !== "user") {
        return response.status(400).send({ error: "role must be either 'admin' or 'user'" });
    }

    try {
        // Upsert User
        const user = await serverClient.upsertUser({
            id: user_id,
            name,
            email,
            role
        });

        response.status(200).send({ success: true });
    }
    catch (error) {
        console.error("Error upserting user:", error);
        response.status(500).send({ error: "Internal server error" });
    }
});

// Endpoint to query all channels
tasksApp.get("/channels", async (request, response) => {
    const filter = { type: 'team', members: { $nin: [request.query.user_id] } };
    const sort = { last_message_at: -1 };
    try {
        // Query Channels that the user is not a member of
        const channelList = await serverClient.queryChannels(filter, sort);

        // Transform the channels to a serializable format
        const channels = channelList.map(channel => {
            // Extract and return only the necessary information from each channel
            // Adjust the properties according to your needs
            return {
                id: channel.cid,
                name: channel.data.name,
            };
        });

        response.status(200).send({ channels });
    }
    catch (error) {
        console.error("Error querying channels:", error);
        response.status(500).send({ error: "Internal server error" });
    }
});


// tasks will be the name of the function as well as API
//in which we will pass our express app
exports.tasks = functions.https.onRequest(tasksApp);
