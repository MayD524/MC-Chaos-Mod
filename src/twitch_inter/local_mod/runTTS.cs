using System.Speech.Synthesis;
using WindowsInput.Native;
using WindowsInput;
using System;

namespace local_mod
{

    public class keyStrokeHandler {
        private InputSimulator sim;

        public VirtualKeyCode GetKeyCode(string key)
        {
            return (VirtualKeyCode)Enum.Parse(typeof(VirtualKeyCode), key.ToUpper());
            
        }

        public void holdKeyForSeconds(VirtualKeyCode key, int seconds) {
            
            this.sim.Keyboard.KeyDown(key);
            System.Threading.Thread.Sleep(seconds * 1000);
            this.sim.Keyboard.KeyUp(key);
        }

        public void pressKey(string key){
            // press the key
            this.sim.Keyboard.TextEntry(key);
            //this.sim.Keyboard.KeyPress(this.GetKeyCode(key));
        }

        public void typeWords(string words) {
            this.sim.Keyboard.TextEntry(words);
        }

        public void click(int x, int y, int button) {
            this.sim.Mouse.MoveMouseTo(x, y);
            if (button == 0)
                this.sim.Mouse.LeftButtonClick();
            else if (button == 1)
                this.sim.Mouse.RightButtonClick();
        }

        public void move(int x, int y) {
            this.sim.Mouse.MoveMouseTo(x, y);
        }

        public keyStrokeHandler() {
            this.sim = new InputSimulator();
        }
    }

    public class tts_engine {
        private SpeechSynthesizer synth;

        public void say(string text) {
            synth.Speak(text);
        }

        public tts_engine(string voice_name="Microsoft David Desktop") {
            voice_name = (voice_name == "default") ? "Microsoft David Desktop" : voice_name;
            this.synth = new SpeechSynthesizer();
            this.synth.SetOutputToDefaultAudioDevice();
            this.synth.SelectVoice(voice_name);
        }
    }
}