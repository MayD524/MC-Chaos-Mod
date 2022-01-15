
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

    // check if the message is a command
    if (message.startsWith("!")) {
        // get the command
        let command = message.substring(1);
        switch (command) {
            case "help":
                client.say(channel, "Just put a number 1-4 in the chat to vote for an option :)\n!author - shows the authors :>\n!help - shows this message :>");
                break;
            case "author":
                client.say(channel, "Created by the Devs at Char(69) Dev Team, May & Sweden");
                break;
            default:
                client.say(channel, "Invalid command, type !help for a list of commands");
                break;
        }
    }


    if (config_data["meta"]["enabled_voting"]) {
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