using System.Collections.Generic;
using System.Linq;
using System;


namespace local_mod {
    public class Config {

        public string configFile = "config.conf";
        public bool isRunning = true;
        public Dictionary<string, string> config = new Dictionary<string, string>();

        public void LoadConfig() {
            // Load config
            if (System.IO.File.Exists(configFile)) {
                string[] lines = System.IO.File.ReadAllLines(configFile);
                foreach (string line in lines) {
                    if (line.StartsWith("#") || line.StartsWith("//")) {
                        continue;
                    }
                    string[] split = line.Split('=');
                    Console.WriteLine("Loaded config: " + split[0] + " = " + split[1]);
                    if (split.Length == 2) {
                        config[split[0]] = split[1];
                    }
                }
            }
        }

        public string get(string key) {
            if (config.ContainsKey(key)) {
                return config[key];
            }
            return "";
        }

        public void displayConfig() {
            Console.WriteLine("Config file: " + configFile);
            for (int i = 0; i < config.Count; i++) {
                Console.WriteLine("\t-" + config.ElementAt(i).Key + " = " + config.ElementAt(i).Value);
            }
        }
        public Config(string configFile) {
            this.configFile = configFile;
            LoadConfig(); // load the config
        }
    }
}