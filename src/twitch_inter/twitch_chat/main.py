"""
    Author: May Draskovics
    Discord: MayActual#8008
    date   : 1/8/2022
    Description:
        This is used as a handler for the twitch overlay.
        Twitch server sends a request to this server, and this server
        writes that data to the file which the mod reads from.
"""
from http.server import BaseHTTPRequestHandler, HTTPServer
from pprint import pprint
import threading
import argparse
import json
import os

class mc_chaos_handler(BaseHTTPRequestHandler):
    def set_headers(self, code:int=200) -> None:
        self.send_response(code)
        self.send_header("Access-Control-Allow-Origin", "*")
        self.send_header("Access-Control-Allow-Methods", "POST, OPTIONS")
        self.send_header("Access-Control-Allow-Headers", "X-Requested-With, content-type")
        self.end_headers()
        
    def do_OPTIONS(self):
        self.set_headers(200)
    
    def do_GET(self) -> None:
        if len(current_requests) == 0: self.set_headers(400); return
        self.set_headers(200)
        self.wfile.write(current_requests[-1].encode())
        current_requests.pop()
    
    def do_POST(self):
        post_data = self.rfile.read(int(self.headers['Content-Length']))
        
        command:str = config['effects'][post_data.decode()]["command"]
        if "|" in command:
            cmd = command.split("|")
            for c in cmd:
                current_requests.append(c)
        else:
            current_requests.append(command)
        
        ## tell the UI that msg was received (avoids errors on the overlay side)
        self.set_headers(200)
        print(current_requests)

def run_server(host, port):
    server_address = (host, port)
    httpd = HTTPServer(server_address, mc_chaos_handler)
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
    
    
    current_requests:list[str] = []
    tts_queue:list[str] = []
    
    parser = argparse.ArgumentParser(description='Runs for twitch chat data')
    parser.add_argument('-p', '--port', type=int, required=False, default=8080, help='Port to run server on')
    parser.add_argument('-hh', '--host', type=str, required=False, default='localhost', help='Host to run server on')
    args = parser.parse_args()
    
    ## for testing
    run_server(args.host, args.port)
    
    
    ## use this in the future
    #server_thread = threading.Thread(target=run_server, args=(args.host, args.port))
    
    #server_thread.start()
    #server_thread.join()