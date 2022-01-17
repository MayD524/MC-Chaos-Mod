// include threading
using System.Threading;
using System;

/*
    TODO:
        - Add keystrokes
        - add mouse clicks
        - add mouse movement
        - add mouse wheel
        - threaded tts & keystrokes
*/

namespace local_mod
{
    class Program
    {   
        private static void appMain(Config config, tts_engine tts, http_handler.localModClient client)
        {
            while (config.isRunning)
            {
                if (config.get("tts") == "true")
                {
                    string text = client.doGET("/tts");
                    if (text != "")
                        tts.say(text);
                }

                
            }
        }

        static void Main(string[] args) {
            Console.WriteLine("MC Twitch Chaos Plugin Optional Mod\nAuthor: Char(69) Dev Team (May)\nVersion 1.0b5");
            
            string configFile = "config.conf";
            if (args.Length > 0) {
                configFile = args[0];
            }

            Config config = new Config(configFile);
            http_handler.localModClient client = new http_handler.localModClient(config.get("http_host"), config.get("http_port"), config.get("use_https") == "true");
            tts_engine tts = new tts_engine();

            if (config.get("tts") == "true") {
                tts = new tts_engine(config.get("tts_voice"));
                tts.say("This is a test message for tts. If you are hearing this it worked!");
            }
            config.displayConfig();
            //appMain(config, tts, client);
        }
    }
}
