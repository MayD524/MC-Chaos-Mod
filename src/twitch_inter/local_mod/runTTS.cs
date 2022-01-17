using System.Speech.Synthesis;

namespace local_mod
{
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