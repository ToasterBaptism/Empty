#!/usr/bin/env python3
"""
Simple web server for Controller Tuner app showcase
Serves the HTML page and provides APK downloads
"""

import http.server
import socketserver
import os
import mimetypes
from urllib.parse import urlparse

class ControllerTunerHandler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory="/workspace/Empty/web", **kwargs)
    
    def do_GET(self):
        parsed_path = urlparse(self.path)
        path = parsed_path.path
        
        # Handle APK downloads
        if path == '/apk/debug':
            self.serve_apk('/workspace/Empty/app/build/outputs/apk/debug/app-debug.apk', 'ControllerTuner-debug.apk')
        elif path == '/apk/release':
            self.serve_apk('/workspace/Empty/app/build/outputs/apk/release/app-release-unsigned.apk', 'ControllerTuner-release.apk')
        else:
            # Serve static files
            super().do_GET()
    
    def serve_apk(self, file_path, download_name):
        """Serve APK file with proper headers"""
        try:
            if os.path.exists(file_path):
                file_size = os.path.getsize(file_path)
                
                self.send_response(200)
                self.send_header('Content-Type', 'application/vnd.android.package-archive')
                self.send_header('Content-Disposition', f'attachment; filename="{download_name}"')
                self.send_header('Content-Length', str(file_size))
                self.send_header('Cache-Control', 'no-cache')
                self.end_headers()
                
                with open(file_path, 'rb') as f:
                    self.copyfile(f, self.wfile)
            else:
                self.send_error(404, f"APK file not found: {download_name}")
        except Exception as e:
            self.send_error(500, f"Error serving APK: {str(e)}")
    
    def end_headers(self):
        # Add CORS headers
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, POST, OPTIONS')
        self.send_header('Access-Control-Allow-Headers', 'Content-Type')
        super().end_headers()

def run_server(port=12000):
    """Run the web server"""
    handler = ControllerTunerHandler
    
    with socketserver.TCPServer(("0.0.0.0", port), handler) as httpd:
        print(f"🚀 Controller Tuner showcase server running at:")
        print(f"   Local: http://localhost:{port}")
        print(f"   Network: http://0.0.0.0:{port}")
        print(f"   Public: https://work-1-ivgasjztndoipbxw.prod-runtime.all-hands.dev")
        print(f"\n📱 APK Downloads available at:")
        print(f"   Debug APK: /apk/debug")
        print(f"   Release APK: /apk/release")
        print(f"\n🛑 Press Ctrl+C to stop the server")
        
        try:
            httpd.serve_forever()
        except KeyboardInterrupt:
            print(f"\n🛑 Server stopped")

if __name__ == "__main__":
    run_server()