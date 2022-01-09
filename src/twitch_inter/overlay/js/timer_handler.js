var data = {};
var possible_opts = [];
var has_voted = [];
var scores = [0,0,0,0];
var active_round = true;
var total_votes  = 0;
var i = 0;

var randomProperty = function (obj) {
    // check if object is an array
    if (Array.isArray(obj)) { return obj[Math.floor(Math.random() * obj.length)]; }
    var keys = Object.keys(obj);
    var k =  keys[keys.length * Math.random() << 0];
    if (k in possible_opts) {
        return randomProperty(obj);
    }
    return k;
};

var writeFile = function(fname, data) {
    var elm = document.createElement("a");
    elm.setAttribute("href", "data:text/plain;charset=utf-8," + encodeURIComponent(data));
    elm.setAttribute("download", fname);
    elm.style.display = "none";
    document.body.appendChild(elm);
    elm.click();
    document.body.removeChild(elm);
};

var _send_vote = function (vote) {

    if (typeof vote !== 'object') {
        throw new Error("vote must be an object");
    }

    console.log("sending vote: ", vote);

    var xhr = new XMLHttpRequest();
    vote = JSON.stringify(vote)
    xhr.open("POST", "http://localhost:" + config_data['general_config']['runtime_port']);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.send(vote);
    console.log(vote);
}

var optionWin = function (arr) {
    var win_index = arr.indexOf(Math.max(...arr));
    var win_key = possible_opts[win_index];
    document.getElementById('winner').innerHTML = "The winner is: " + win_key;
    document.getElementsByClassName("win_popUp")[0].classList.add("active");
    // send the win to the server
    data = {
        "effect_name": win_key,
        "effect_details": config_data['effects'][win_key]
    }
    //_send_vote(data);
    // wait 2 seconds
    setTimeout(function () {
        document.getElementsByClassName("win_popUp")[0].classList.remove("active");
    }, 2000);
};

function run_progbar() {
         if (i == 0) {
        // reset the votes
        active_round = true;
        total_votes = 0;
        scores = [0, 0, 0, 0];

        i = 1;
        var elem = document.getElementById("bar");

        // check if the bar exists
        if (elem === null) throw new Error("could not find bar container in DOM");

        var width = 1;
        var id = setInterval(frame, 10);
        function frame() {
            if (width >= 100) {
                width = 0;
                elem.style.width = width + '%';
                clearInterval(id);
                i = 0;
                active_round = false;
                if (total_votes == 0) {
                    let win = randomProperty(config_data['effects']);
                    document.getElementById('winner').innerHTML = "The winner is: " + win;
                    document.getElementsByClassName("win_popUp")[0].classList.add("active");
                    data = {
                        "effect_name" : win,
                        "effect_details" : config_data['effects'][data]
                    };
                        //_send_vote(data);
                    setTimeout(function () {
                        document.getElementsByClassName("win_popUp")[0].classList.remove("active");
                    }, 2000);
                    return;
                }
                optionWin(scores);
               
            } else {
                width += 100 / (config_data['meta']['default_duration'] * 100);
                elem.style.width = width + '%';
            }
        }
    }
}