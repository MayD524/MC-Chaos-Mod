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

class mc_chaos_handler(BaseHTTPRequestHandler):
    def do_OPTIONS(self):
        self.send_response(200, "ok")
        self.send_header("Access-Control-Allow-Origin", "*")
        self.send_header("Access-Control-Allow-Methods", "POST, OPTIONS")
        self.send_header("Access-Control-Allow-Headers", "X-Requested-With, content-type")
        self.end_headers()
    
    def do_POST(self):
        post_data = self.rfile.read(int(self.headers['Content-Length']))
        post_data = json.loads(post_data)
        pprint(post_data)

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
    
    parser = argparse.ArgumentParser(description='Runs for twitch chat data')
    parser.add_argument('-p', '--port', type=int, required=False, default=8888, help='Port to run server on')
    parser.add_argument('-hh', '--host', type=str, required=False, default='localhost', help='Host to run server on')
    args = parser.parse_args()
    server_thread = threading.Thread(target=run_server, args=(args.host, args.port))
    
    server_thread.start()
    server_thread.join()