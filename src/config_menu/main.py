"""
    Author: May Draskovics
    Discord: MayActual#8008
    Date : 1/8/2022
    Description:
        The configuration menu for the MC Chaos mod.
    (seperate app from the main mod app)
"""
from tkinter.messagebox import showinfo, showerror, askyesno
from tkinter import colorchooser
from types import MethodType
from tkinter import font
from tkinter import ttk
import tkinter as tk
import json
import sys

class configMenuGUI:
    def __init__(self, default_config:str="config.json") -> None:
        self.default_config = default_config
        self.config         = self.load_config(default_config)
        self.last_conf      = self.config
        self.first_load     = True
        
        self.states_check:dict[str, tk.BooleanVar] = {}
        self.meta_states:dict[str, tk.BooleanVar]  = {}
        
        self.root           = tk.Tk()
        self.root.title("MC Chaos Config Menu")
        self.root.geometry("1000x500")
        
        self._layout()
        
        self.root.mainloop()        
    
    def _layout(self) -> None:
        self.tabCtrl = ttk.Notebook(self.root)
        style = ttk.Style()
        
        self.font_name:str  = self.config['general_config']['font']
        self.font_size:int  = self.config['general_config']['font_size']
        
        ## effects tab
        self.effectsTab = ttk.Frame(self.tabCtrl)
        effectsText     = tk.Text(self.effectsTab, font=(self.font_name, self.font_size), width=100, height=16, background=self.root.cget("bg"), borderwidth=0)
        sb = tk.Scrollbar(self.effectsTab, orient="vertical", command=effectsText.yview)
        effectsText.configure(yscrollcommand=sb.set)
        sb.pack(side="right", fill="y")
        effectsText.pack()
        
        for i, effect in enumerate(self.config['effects']):
            
            self.states_check[effect] = tk.BooleanVar(value=self.config['effects'][effect]['enabled'])
            button = ttk.Checkbutton(self.effectsTab,
                                     text=effect,
                                     variable=self.states_check[effect],
                                     onvalue=True,
                                     offvalue=False,
                                     style="effects.TCheckbutton")
            effectsText.window_create("end", window=button)
            effectsText.insert("end", "\n")
        effectsText.config(state="disabled")
        
        ## Meta tab
        self.metaTab = ttk.Frame(self.tabCtrl)
        
        self.metaTabScroll = ttk.Scrollbar(self.metaTab)
        self.metaTabScroll.place(relheight=1, relx=1)

        ## make a text box for default duration (in seconds)
        self.defaultDuration = tk.IntVar(value=self.config['meta']['default_duration'])
        ttk.Spinbox(self.metaTab, font=(self.font_name, self.font_size), width=10, from_=0, to=3600, textvariable=self.defaultDuration).place(x=10, y=5)
        tk.Label(self.metaTab, text="Default Duration (seconds)", font=(self.font_name, self.font_size)).place(x=150, y=5)
  
        ## make a text box for default cooldown (in seconds)
        self.defaultCooldown = tk.IntVar(value=self.config['meta']['grace_period_s'])
        ttk.Spinbox(self.metaTab, font=(self.font_name, self.font_size), width=10, from_=0, to=3600, textvariable=self.defaultCooldown).place(x=10, y=40)
        tk.Label(self.metaTab, text="Default Cooldown (seconds)", font=(self.font_name, self.font_size)).place(x=150, y=40)
  
        
        ## make a password box for the twitch oauth token
        self.twitchToken = tk.Entry(self.metaTab, font=(self.font_name, self.font_size), width=10)
        self.twitchToken.insert(0, self.config['meta']['twitch_token'])
        self.twitchToken.place(x=10, y=75)
        
        twitchTokenText = tk.Label(self.metaTab, text="Twitch Name", font=(self.font_name, self.font_size))
        twitchTokenText.place(x=150, y=75)
        for i, meta in enumerate(self.config['meta'].keys()):
            if isinstance(self.config['meta'][meta], bool):
   
                self.meta_states[meta] = tk.BooleanVar(value=self.config['meta'][meta])
                box = ttk.Checkbutton(self.metaTab, text=meta,
                                       variable=self.meta_states[meta], 
                                       onvalue=True, 
                                       offvalue=False,
                                       style="effects.TCheckbutton")
                box.place(x=10, y=70+i*30)
        
        ## settings tab
        self.settingsTab = ttk.Frame(self.tabCtrl)
        
        ## make a dropdown for the font
        self.fontSelectValue = tk.StringVar(value=self.font_name)
        self.fontSelect = ttk.Combobox(self.settingsTab, values=list(font.families()), state="readonly", textvariable=self.fontSelectValue)
        self.fontSelect.place(x=10, y=10)
        
        ttk.Label(self.settingsTab, text="Font Name", font=(self.font_name, self.font_size)).place(x=150, y=10)
        
        ## make a textbox with arrows for the font size
        self.fontSizeValue = tk.IntVar(value=self.font_size)
        ttk.Spinbox(self.settingsTab, from_=1, to=100, textvariable=self.fontSizeValue, style="effects.TSpinbox").place(x=10, y=40)
        ttk.Label(self.settingsTab, text="Font Size", font=(self.font_name, self.font_size)).place(x=150, y=40)
        
        self.httpPortValue = tk.IntVar(value=self.config['general_config']['runtime_port'])
        ttk.Spinbox(self.settingsTab, from_=1, to=65535, textvariable=self.httpPortValue, style="effects.TSpinbox").place(x=10, y=70)
        ttk.Label(self.settingsTab, text="HTTP Port", font=(self.font_name, self.font_size)).place(x=150, y=70)
        
        ## add some general about text
        ttk.Label(self.settingsTab, text=f"Version: {self.config['about']['version']}", font=(self.font_name, self.font_size)).place(x=10, y=100)
        ttk.Label(self.settingsTab, text=f"Author: {self.config['about']['author']}", font=(self.font_name, self.font_size)).place(x=10, y=130)
        ttk.Label(self.settingsTab, text=f"Github: {self.config['about']['author_url']}", font=(self.font_name, self.font_size)).place(x=10, y=160)
        ttk.Label(self.settingsTab, text="Core Author: May", font=(self.font_name, self.font_size)).place(x=10, y=190)
        ttk.Label(self.settingsTab, text="Mod Author: Jerk (Sweden)", font=(self.font_name, self.font_size)).place(x=10, y=220)
        
        ## add a frame for css config
        self.cssTab = ttk.Frame(self.tabCtrl)
        
        for i, css in enumerate(self.config['css_settings']):
            ttk.Button(self.cssTab, text=css, style="ctrl_btn.TButton", command=lambda css=css: self.edit_css(css)).place(x=10, y=10+i*40)
        
        
        self.tabCtrl.add(self.effectsTab, text="Effects")
        self.tabCtrl.add(self.metaTab, text="Meta")
        self.tabCtrl.add(self.settingsTab, text="Settings")
        self.tabCtrl.add(self.cssTab, text="CSS")
        
        self.tabCtrl.pack(expand=1, fill="both")
        
        ## make a frame for the buttons at the bottom
        self.btnFrame = ttk.Frame(self.root)
        
        ## make a button to save the config
        ttk.Button(self.btnFrame, text="Save", command=self.save_config, style="ctrl_btn.TButton").pack(side='right')
        ttk.Button(self.btnFrame, text="Export", command=self.export_config, style="ctrl_btn.TButton").pack(side="right")
        ttk.Button(self.btnFrame, text="Reload", command=self.reset_config, style="ctrl_btn.TButton").pack(side='right')
        ttk.Button(self.btnFrame, text="Restart", command=self.restart_prog, style="ctrl_btn.TButton").pack(side='right')
        ttk.Button(self.btnFrame, text="New Effect", command=self.create_effect, style="ctrl_btn.TButton").pack(side='right')
        self.btnFrame.pack(side="bottom", fill="x")
        
        style.configure("ctrl_btn.TButton", font=(self.font_name, self.font_size))
        style.configure("effects.TCheckbutton", font=(self.font_name, self.font_size))
        style.configure("effects.TLabel", font=(self.font_name, self.font_size))
        
        if self.first_load:
            ## window config
            self.root.resizable(False, False)
            
            for key_seq, event in self.config['key_binds'].items():
                self.root.bind(key_seq, lambda e, event=event: self.getFunction(event)())
            
            self.first_load = False
    
    def edit_css(self, css_flag:str) -> None:
        color_code = colorchooser.askcolor(title="Choose a color")
        if not color_code: return
        self.config["css_settings"][css_flag] = color_code[1]
    
    def create_effect(self) -> None:
            
        self.win = tk.Toplevel(self.root)
        self.win.title("Create Effect")
        self.win.geometry("400x200")
        
        self.effectName      = tk.StringVar()
        self.effectDesc      = tk.StringVar()
        self.effectCommand   = tk.StringVar()
        self.effectTts       = tk.StringVar()
        self.effectrarity    = tk.DoubleVar()
        
        ttk.Label(self.win, text="Effect Name:", style="effects.TLabel").grid(row=0, column=0)
        ttk.Entry(self.win, textvariable=self.effectName).grid(row=0, column=1)
        
        ttk.Label(self.win, text="Effect Description:", style="effects.TLabel").grid(row=1, column=0)
        ttk.Entry(self.win, textvariable=self.effectDesc).grid(row=1, column=1)
        
        ttk.Label(self.win, text="Effect Command:", style="effects.TLabel").grid(row=2, column=0)
        ttk.Entry(self.win, textvariable=self.effectCommand).grid(row=2, column=1)
        
        ttk.Label(self.win, text="Effect TTS:", style="effects.TLabel").grid(row=3, column=0)
        ttk.Entry(self.win, textvariable=self.effectTts).grid(row=3, column=1)
        
        ttk.Label(self.win, text="Effect Rarity:", style="effects.TLabel").grid(row=4, column=0)
        ttk.Spinbox(self.win, from_=0.0, to=1.0, increment=0.1, textvariable=self.effectrarity, style="effects.TSpinbox").grid(row=4, column=1)
        
        ttk.Button(self.win, text="Create", command=self.createEffectCallback, style="effects.TButton").grid(row=5, column=0)

        self.win.mainloop()
    
    def createEffectCallback(self) -> None:
        if (name := self.effectName.get()) == "":
            showerror("Error", "Please enter a name for the effect.")
            self.win.focus_force()
            return
        
        elif name in self.config['effects']:
            resp = askyesno("Error", f"An effect with the name {name} already exists. Do you want to overwrite it?")
            if not resp: 
                self.win.focus_force()
                return
            
        self.config['effects'][self.effectName.get()] = {
            "enabled": True,
            "description": self.effectDesc.get(),
            "command": self.effectCommand.get(),
            "tts": self.effectTts.get(),
            "rarity": self.effectrarity.get()
        }
        self.save_config()
        self.reset_config(True)
        self.win.destroy()
    
    def getFunction(self, func:str) -> MethodType:
        match func:
            case "save_config": return self.save_config
            case "reload_config": return self.reset_config
            case "quit": return self.root.destroy
            case "restart": return self.restart_prog
            case _: return None
    
    def restart_prog(self, bypass_ask:bool=False) -> None:
        resp = askyesno("Restart", "This will restart the program. Are you sure?") if not bypass_ask else False
        if not resp: return
        import subprocess
        subprocess.Popen(f'{sys.executable} {sys.argv[0]}')
        sys.exit(0)
    
    def reset_config(self, bypass_ask:bool=False) -> None:
        """
            Reset the config to the default
        """
        if not bypass_ask and not askyesno("Reset Config", "Are you sure you want to reload the config?"): return
        self.config = self.load_config(self.default_config)
        self.states_check = {}
        
        self.effectsTab.destroy()
        self.metaTab.destroy()
        self.tabCtrl.destroy()
        self.btnFrame.destroy()
        
        self._layout()
    
    def save_config(self) -> None:
        """
            Save the config to a file
        """
        try:
            for effect, state in self.states_check.items():
                self.config['effects'][effect]['enabled'] = state.get()
            
            for meta, state in self.meta_states.items():
                self.config['meta'][meta] = state.get()
            
            self.config['meta']['default_duration'] = int(self.defaultDuration.get())
            self.config['meta']['twitch_token'] = self.twitchToken.get()
            
            self.config['general_config']['font'] = self.fontSelectValue.get()
            self.config['general_config']['font_size'] =self.fontSizeValue.get()
            self.config['general_config']['runtime_port'] = self.httpPortValue.get()
            print("Saving config")
            with open(self.config['general_config']['file_name'], 'w') as f:
                json.dump(self.config, f, indent=4)
                
            self.last_conf = self.config   
            showinfo("Success", f"Config saved to: {self.config['general_config']['file_name']}")
        except Exception as e:
            showerror("Error", f"Error saving config: {e}")
        
    
    def export_config(self) -> None:
        """
            Exporting the config to a js file
        """
        try:
            out_data = self.config
            
            ## read the template css
            with open(f'defaults/default.css', 'r') as f:
                css_data = f.read()
            
            for css in out_data['css_settings'].keys():
                if f"/*{css}*/" in css_data:
                    css_data = css_data.replace(f"/*{css}*/", out_data['css_settings'][css])
                    
            with open("styles.css", 'w+') as f:
                f.write(css_data)
            
            del out_data['key_binds']
            del out_data['css_settings']
            ## remove all disabled effects
            ## set the value to the rarity of the effect
            out_data["effects"] = {k:v['rarity'] for k,v in out_data["effects"].items() if v['enabled']}
            
            json_data = json.dumps(out_data)
            with open("config.js", 'w') as f:
                f.write(f"var config_data = {json_data}")
            showinfo("Success", "Config exported to config.js")
        except Exception as e:
            showerror("Error", f"Error exporting config: {e}")

    @staticmethod
    def load_config(config_file="config.json") -> dict:
        """
            Loads the config file and returns the config as a dictionary.
        """
        try:
            with open(config_file, "r") as f:
                config = json.load(f)
        except FileNotFoundError:
            showerror("Error", f"Config file not found: {config_file}")
            print("Config file not found.")
            sys.exit(1)
        return config
    
if __name__ == "__main__":
    cMenu = configMenuGUI()
    