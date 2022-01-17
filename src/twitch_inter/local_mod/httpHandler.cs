using System;


namespace local_mod
{   
    namespace http_handler 
    {
        public class localModClient
        {
            private string url;
            private static string parseUrl(string host, string port="8080", bool isHttps=false)
            {
                string url = "";
                url += (isHttps ? "https://" : "http://");
                url += host + ":" + port;
                return url;
            }

            public string doPOST(string data)
            {
                string response = "";
                try
                {
                    System.Net.WebClient client = new System.Net.WebClient();
                    client.Headers.Add("Content-Type", "application/x-www-form-urlencoded");
                    response = client.UploadString(url, data);
                }
                catch (Exception e)
                {
                    Console.WriteLine("Error: " + e.Message);
                }
                return response;
            }

            public string doGET(string file="/")
            {
                string response = "";
                try
                {
                    System.Net.WebClient client = new System.Net.WebClient();
                    response = client.DownloadString(url + file);
                }
                catch (Exception e)
                {
                    Console.WriteLine("Error: " + e.Message);
                }
                return response;
            }

            public localModClient (string host, string port="8080", bool isHttps=false)
            {
                this.url = parseUrl(host, port, isHttps);
            }
        }
    }
}