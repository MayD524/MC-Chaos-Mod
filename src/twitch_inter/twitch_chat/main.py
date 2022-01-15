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
        if len(current_requests) == 0: self.set_headers(400); return
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
    
    current_requests:list[str] = [
        "enchant;1;doRandom",
        "sound;1;doRandom",
    ]
    event_queue:list[str] = []
    tts_queue:list[str] = []
    
    parser = argparse.ArgumentParser(description='Runs for twitch chat data')
    parser.add_argument('-p', '--port', type=int, required=False, default=8080, help='Port to run server on')
    parser.add_argument('-hh', '--host', type=str, required=False, default='localhost', help='Host to run server on')
    args = parser.parse_args()
    
    run_server(args.host, args.port)
    