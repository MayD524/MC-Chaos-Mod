"""
    Author: May Draskovics
    Discord: MayActual#8008
    date   : 1/8/2022
    Description:
        This is used as a handler for the twitch overlay.
        Twitch server sends a request to this server, and this server
        writes that data to the file which the mod reads from.
"""
from http.server import SimpleHTTPRequestHandler, HTTPServer
from socketserver import ThreadingMixIn
import threading
import argparse
import json
import sys
import os

"""try:
    import pyautogui
except ImportError:
    os.system(f"{sys.executable} -m pip install pyautogui")
    import pyautogui"""

try:
    import pyttsx3 ## for tts
except ImportError:
    os.system(f"{sys.executable} -m pip install pyttsx3")
    if os.name == 'nt':
        os.system(f"{sys.executable} -m pip install pywin32")
    import pyttsx3

def run_tts(text:str) -> None:
    TTS_ENGINE.say(text)
    TTS_ENGINE.runAndWait()

class ThreadingHTTPServer(ThreadingMixIn, HTTPServer):
    pass

class mc_chaos_handler(SimpleHTTPRequestHandler):
    def set_headers(self, code:int=200) -> None:
        self.send_response(code)
        self.send_header("Access-Control-Allow-Origin", "*")
        self.send_header("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
        self.send_header("Access-Control-Allow-Headers", "X-Requested-With, content-type")
        self.end_headers()
        
    def do_OPTIONS(self):
        self.set_headers(200)
    
    def do_GET(self) -> None:
        
        self.set_headers(200)
        if len(current_requests) == 0: self.wfile.write(b"msg;No new effects"); return#self.set_headers(400); return
        if len(tts_queue) > 0:
            run_tts(tts_queue.pop())
        self.wfile.write(current_requests[-1].encode())
        current_requests.pop()
    
    def do_POST(self):
        post_data = self.rfile.read(int(self.headers['Content-Length']))
        
        command:str = config['effects'][post_data.decode()]["command"]
        if config['meta']['enabled_tts']:
            tts_queue.append(config['effects'][post_data.decode()]["tts"])
        current_requests.append(command)
        
        ## tell the UI that msg was received (avoids errors on the overlay side)
        self.set_headers(200)
        print(current_requests)


def run_server(host, port):
    server_address = (host, port)
    httpd = ThreadingHTTPServer(server_address, mc_chaos_handler)
    try:
        print("Starting server on {}:{}".format(host if host != '' else 'localhost', port))
        httpd.serve_forever()
    except KeyboardInterrupt:
        pass
    
    print('\nShutting down server...')
    httpd.server_close()
    print('Server shut down.')


if __name__ == '__main__':
    ## read in the config
    
    if not os.path.exists('config.json'):
        raise FileNotFoundError("config.json not found!")
    with open("config.json", "r") as f:
        config = json.load(f)
    
    TTS_ENGINE = pyttsx3.init()
    
    current_requests:list[str] = []
        #"give;1;diamond",
        #"msg;test message",
        #"give;1;diamond|msg;test message2 diamond",
        #"msg;Hello?'|msg;Hello?'|msg;Who's this?'|msg;Who's this?'|msg;I'm asking the questions, I called you.'|msg;No, I called you, and you sound like the ugliest son of a bitch I ever heard.'|msg;You sound like the physical manifestation of some loser's inner demons!'|msg;Well, you sound like some total chode's inability to confront the reality of his past actions.'|msg;If I ever get your stinky mug in my line of sight I swear to jack off I'll cock your clock off.'|msg;Well I'm gonna be the bigger man, and hang up first- ugh dammit!'|msg;Listen, we don't cotton to freaks round these parts, scram wierdo!'|msg;Oh yeah, I don't polycotton to coping tropes, even my own. So why don't you split?'|msg;Looks like I already did. You're the sad figment of my twisted psyche's tragic dividend. You're the un-me. I'm the real me! You wanna be? Me?'|msg;Kiddo, I was the real me when you were still in my short pants.'|msg;Hate to break it to ya, but I wore them first. Me bequeathed thee, the psychopathological hand-you-downs.'|msg;So you're the one who stained them?'|msg;Whoever found it, browned it.'|msg;You'd like me to be you, wouldn't me? But it's too late, you snoze, you lose.'|msg;You sleeped, you weeped.'|msg;You nap-a, You get slap-a!'|msg;You slumber, a cucumber.'|msg;You catch up on some Zed's, you get out of my heads!'|msg;You slumber, ham- BURGER I DON'T WANNA TALK BOUT NOTHIN ELSE!'|msg;Listen, this psyche is not big enough for two metaphysical seekers.'|msg;You couldn't seek your way out of a cardboard bag!'|msg;Yeah, I know, cause it would be an egg!'|msg;OOH! This guy might be better than me-'|msg;You're right! I am better than me!'|msg;Look buddy, know when you defeated. Accept your defecation.'|msg;No thanks, I'm full, cause I eat pussies like you for breakfast!'|msg;Look at you, you look so superficial, you probably judge things by their physical appearance.'|msg;Oh yeah? Your mom's so shallow, she probably thinks this quip is about her.'|msg;You're about as deep as a bowl of soup, and your tongue is about as sharp as a soup spoon!'|msg;Hey! Say what you want about me, but lay off the soup.'|msg;If you love soup so much, why don't you marry soup?'|msg;Because I'm already married, to justice.'|msg;Yeah, only a blind girl would marry you.'|msg;I know everything you're gonna-'|msg;Say, and I know everything you're gonna-'|msg;Don't.'|msg;Oh yeah? Well when God was passing out insight, you thought he said that when God was passing out holy prophets, you thought he said oily faucets, cause your soul has diarrhoea, of the mouth, faucet!'|msg;Are you so dumb, you'd even answer rhetorical questions?'|msg;I don't know? Do you?'|msg;We can play this game all night.'|msg;First of all, it's daytime! And this is no game.'|msg;Checkmate!'|msg;Oh! So you admit that you're checking me out, and you want to mate!'|msg;You got a license to sell hot-dogs, Chico man?'|msg;No, they wouldn't give it to me, because, when I was filling out the application, my penis was sticking out!'|msg;Oh yeah! You only got one peni? Let me see it…'|msg;See with your eyes, not with your mouth!'|msg;I'll call your bluff, I'll see your penis with your mouth, and I raise you, with my hand.'|msg;Ante up!'|msg;OoOh! Dammit!'|msg;What's wrong?'|msg;I crapped out, but I'm tough, I can suck it up. Huoourgh!'|msg;Ok! Count of three, we show what's under the loincloth! Weiner, take all!'|msg;1, 2, 3!"
        #"msg;Hello?|delay;1|msg;Hello?|delay;1|msg;Who's this?|delay;1|msg;Who's this?|delay;1|msg;I'm asking the questions, I called you.|delay;1|msg;No, I called you, and you sound like the ugliest son of a bitch I ever heard.|delay;1|msg;You sound like the physical manifestation of some loser's inner demons!|delay;1|msg;Well, you sound like some total chode's inability to confront the reality of his past actions.|delay;1|msg;If I ever get your stinky mug in my line of sight I swear to jack off I'll cock your clock off.|delay;1|msg;Well I'm gonna be the bigger man, and hang up first- ugh dammit!|delay;1|msg;Listen, we don't cotton to freaks round these parts, scram wierdo!|delay;1|msg;Oh yeah, I don't polycotton to coping tropes, even my own. So why don't you split?|delay;1|msg;Looks like I already did. You're the sad figment of my twisted psyche's tragic dividend. You're the un-me. I'm the real me! You wanna be? Me?|delay;1|msg;Kiddo, I was the real me when you were still in my short pants.|delay;1|msg;Hate to break it to ya, but I wore them first. Me bequeathed thee, the psychopathological hand-you-downs.|delay;1|msg;So you're the one who stained them?|delay;1|msg;Whoever found it, browned it.|delay;1|msg;You'd like me to be you, wouldn't me? But it's too late, you snoze, you lose.|delay;1|msg;You sleeped, you weeped.|delay;1|msg;You nap-a, You get slap-a!|delay;1|msg;You slumber, a cucumber.|delay;1|msg;You catch up on some Zed's, you get out of my heads!|delay;1|msg;You slumber, ham- BURGER I DON'T WANNA TALK BOUT NOTHIN ELSE!|delay;1|msg;Listen, this psyche is not big enough for two metaphysical seekers.|delay;1|msg;You couldn't seek your way out of a cardboard bag!|delay;1|msg;Yeah, I know, cause it would be an egg!|delay;1|msg;OOH! This guy might be better than me-|delay;1|msg;You're right! I am better than me!|delay;1|msg;Look buddy, know when you defeated. Accept your defecation.|delay;1|msg;No thanks, I'm full, cause I eat pussies like you for breakfast!|delay;1|msg;Look at you, you look so superficial, you probably judge things by their physical appearance.|delay;1|msg;Oh yeah? Your mom's so shallow, she probably thinks this quip is about her.|delay;1|msg;You're about as deep as a bowl of soup, and your tongue is about as sharp as a soup spoon!|delay;1|msg;Hey! Say what you want about me, but lay off the soup.|delay;1|msg;If you love soup so much, why don't you marry soup?|delay;1|msg;Because I'm already married, to justice.|delay;1|msg;Yeah, only a blind girl would marry you.|delay;1|msg;I know everything you're gonna-|delay;1|msg;Say, and I know everything you're gonna-|delay;1|msg;Don't.|delay;1|msg;Oh yeah? Well when God was passing out insight, you thought he said that when God was passing out holy prophets, you thought he said oily faucets, cause your soul has diarrhoea, of the mouth, faucet!|delay;1|msg;Are you so dumb, you'd even answer rhetorical questions?|delay;1|msg;I don't know? Do you?|delay;1|msg;We can play this game all night.|delay;1|msg;First of all, it's daytime! And this is no game.|delay;1|msg;Checkmate!|delay;1|msg;Oh! So you admit that you're checking me out, and you want to mate!|delay;1|msg;You got a license to sell hot-dogs, Chico man?|delay;1|msg;No, they wouldn't give it to me, because, when I was filling out the application, my penis was sticking out!|delay;1|msg;Oh yeah! You only got one peni? Let me see it\u2026|delay;1|msg;See with your eyes, not with your mouth!|delay;1|msg;I'll call your bluff, I'll see your penis with your mouth, and I raise you, with my hand.|delay;1|msg;Ante up!|delay;1|msg;OoOh! Dammit!|delay;1|msg;What's wrong?|delay;1|msg;I crapped out, but I'm tough, I can suck it up. Huoourgh!|delay;1|msg;Ok! Count of three, we show what's under the loincloth! Weiner, take all!|delay;1|msg;1, 2, 3!"
    #]
    event_queue:list[str] = []
    tts_queue:list[str] = []
    
    parser = argparse.ArgumentParser(description='Runs for twitch chat data')
    parser.add_argument('-p', '--port', type=int, required=False, default=8080, help='Port to run server on')
    parser.add_argument('-hh', '--host', type=str, required=False, default='localhost', help='Host to run server on')
    args = parser.parse_args()
    
    run_server(args.host, args.port)
    