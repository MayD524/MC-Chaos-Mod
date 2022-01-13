
// make twitch client
const client = new tmi.client({
    options: {
        debug: true,
        messageLogLevel : "info"
    },
    connection: {
        reconnect: true,
        secure:true
    },
    channels: [config_data["meta"]['twitch_token']]
});

// connect to the chat
try {
    client.connect()
    $("#total_votes").text("Connected to Twitch");
} catch(err) {
    $("#total_votes").text("Error connecting to Twitch: " + err);
}

// do something when we recv a message
client.on('message', (channel, userstate, message, self) => {
    if (self) return;
    console.log(`[${channel}] ${userstate['display-name']}: ${message}`);

    if (config_data["meta"]["enabled_voting"] && active_round) {
        // check if the user has voted
        if (!has_voted.includes(userstate['display-name'])) {
            switch(message.toLowerCase()) {
                case '1': 
                    total_votes++;
                    scores[0]++;
                    break;
                case '2': 
                    total_votes++;
                    scores[1]++;
                    break;
                case '3': 
                    total_votes++;
                    scores[2]++;
                    break;
                case '4': 
                    total_votes++;
                    scores[3]++;
                    break;

                default:
                    break;
            }
            // add the user to the list of users who have voted
            has_voted.push(userstate['display-name']);
        }
        $("#total_votes").text("Total Votes: " + total_votes);
    }
});