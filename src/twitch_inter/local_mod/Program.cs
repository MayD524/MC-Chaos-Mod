using System.Collections.Generic;
using System.Threading;
using System;

/*
    TODO:
        - add mouse clicks
        - add mouse movement
        - add mouse wheel
*/

namespace local_mod
{
    class Program
    {   
        private static void appMain(Config config, 
                                    tts_engine tts, 
                                    http_client.http_handler.localModClient client,
                                    keyStrokeHandler keyStrokeHandler)
        {
            List<Thread> threads = new List<Thread>();


            while (config.isRunning)
            {
                if (config.get("tts") == "true")
                {
                    string text = client.doGET("/tts");
                    Console.WriteLine("tts: " + text);
                    if (text != "")
                        threads.Add(new Thread(() => tts.say(text)));
                        threads[threads.Count - 1].Start();
                }

                if (config.get("allow_keyStrokes") == "true")
                {
                    string keystrokes = client.doGET("/keystrokes");
                    Console.WriteLine("keyStrokes: " + keystrokes);
                    if (keystrokes != "")
                    {
                        string[] keys = keystrokes.Split(',');
                        foreach (string key in keys)
                        {
                            if (key == "")
                                continue;

                            else if (key.StartsWith("hold"))
                            {
                                // remove "hold" from the string
                                string[] key_split = key.Split(';');
                                string key_code = key_split[1];
                                int seconds = int.Parse(key_split[2]);
                                // check if the key is a letter 
                                
                                threads.Add(new Thread(() => keyStrokeHandler.holdKeyForSeconds(keyStrokeHandler.GetKeyCode(key_code), seconds)));
                                threads[threads.Count - 1].Start();
                            }

                            else if (key.StartsWith("mouse"))
                            {
                                string[] key_split = key.Split(';');
                                int button = int.Parse(key_split[1]);
                                int x = int.Parse(key_split[2]);
                                int y = int.Parse(key_split[3]);
                                threads.Add(new Thread(() => keyStrokeHandler.click(x, y, button)));
                                threads[threads.Count - 1].Start();
                            }

                            else
                            {
                                threads.Add(new Thread(() => keyStrokeHandler.pressKey(key)));
                                threads[threads.Count - 1].Start();
                            }
                        }
                    }
                }
                // wait for all threads to finish
                for (int i = 0; i < threads.Count; i++)
                {
                    if (threads[i] != null && threads[i].IsAlive)
                        threads[i].Join();
                }
                // wait for poll_delaySeconds
                Thread.Sleep(int.Parse(config.get("poll_delaySeconds")) * 1000);

            }
        }

        static void Main(string[] args) {
            Console.WriteLine("MC Twitch Chaos Plugin Optional Mod\nAuthor: Char(69) Dev Team (May)\nVersion 1.0b5");
            
            string configFile = "config.conf";
            if (args.Length > 0) {
                configFile = args[0];
            }

            Config config = new Config(configFile);
            keyStrokeHandler keyHandler = null;
            http_client.http_handler.localModClient client = new http_client.http_handler.localModClient(config.get("http_host"), config.get("http_port"), config.get("use_https") == "true");
            tts_engine tts = null;

            if (config.get("tts") == "true") {
                tts = new tts_engine(config.get("tts_voice"));
                tts.say("This is a test message for tts. If you are hearing this it worked!");
            }

            if (config.get("allow_keyStrokes") == "true") {
                keyHandler = new keyStrokeHandler();
            }
            config.displayConfig();
            Thread.Sleep(1000); // wait a second before starting
            appMain(config, tts, client, keyHandler);
        }
    }
}
