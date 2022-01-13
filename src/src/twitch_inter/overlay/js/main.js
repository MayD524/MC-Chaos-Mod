
var roundsOptions = function(obj) {
    var options = [];
    for (var i = 0; i < 4; i++) {
        options.push(randomProperty(obj));
    }
    return options;
}

function placeObjects(options) {
    // get the container
    var container = document.getElementById("option_display");
    // clear the container
    container.innerHTML = "";

    // create a new div for each object
    for (var i = 0; i < options.length; i++) {
        var newDiv = document.createElement("div");
        newDiv.className = "option";
        newDiv.id = "option" + i;
        newDiv.innerHTML = options[i];
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
        possible_opts = roundsOptions(config_data['effects']);
        placeObjects(possible_opts);
        run_progbar();
    }
    
    setTimeout(start_timer, (config_data["meta"]["default_duration"] + config_data["meta"]["grace_period_s"]) * 1000);
}