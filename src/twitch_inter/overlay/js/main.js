
var roundsOptions = function(obj) {
    // get a list of all the keys
    let keys    = Object.keys(obj);
    let options = [];

    for (var i = 0; i < 4; i++) {
        let opt = randomProperty(keys);
        // check if the option is already in the array
        if (options.indexOf(opt) == -1 && prev_round.indexOf(opt) == -1) {
            // get a random number between 0 and 1 and check if its less than the chance
            if (Math.random() < obj[opt]) {
                options.push(opt);
            } else {
                i--;
            }
        } else {
            i--;
        }
    }
    console.log(options);
    return options;
}

function placeObjects(options) {
    // get the container
    let container = document.getElementById("option_display");
    // clear the container
    container.innerHTML = "";

    // create a new div for each object
    for (var i = 0; i < options.length; i++) {
        let name = options[i];
        if (name.length > 25) name = name.substring(0, 25) + "...";
        let newDiv = document.createElement("div");
        newDiv.className = "option";
        newDiv.id = "option" + i;
        newDiv.innerHTML = i+1 + ") " + name;
        document.getElementById("option_display").appendChild(newDiv);
    }
}

function start_timer() {
    // get the start btn and hide it
    var start_btn = document.getElementById("start_btn");
    start_btn.style.display = "none";
    // just check if the config says that its all enabled
    if (config_data["meta"]["enabled_voting"] && 
        config_data["meta"]["mod_enabled"] &&
        config_data["meta"]["twitch_enabled"]) {
        
        // set the prev_round to the current round
        if (possible_opts.length != 0) prev_round = possible_opts;

        possible_opts = roundsOptions(config_data['effects']);
        placeObjects(possible_opts);
        run_progbar();
    }
    
    setTimeout(start_timer, (config_data["meta"]["default_duration"] + config_data["meta"]["grace_period_s"]) * 1000);
}