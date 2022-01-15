from tkinter.messagebox import showinfo
from tkinter import ttk
import tkinter as tk
import sys

class effectGUI:
    def __init__(self, master:tk.Tk) -> None:
        self.win = tk.Toplevel(master)
        self.win.title("Create A New Effect")
        self.win.geometry("400x200")
        
        self.result:dict[str,str] = {}
        self.command:str          = ""
        self.effectName           = tk.StringVar()
        self.effectNameDesc       = tk.StringVar()
        self.effectTTS            = tk.StringVar()
        self.effectrarity         = tk.DoubleVar() ## this doesn't do anything yet
        
    def _layout(self) -> None:
        """
            Text Boxes 
                Effect name
                Description
                TTS Message
            Spin Box
                effect rarity
        """
        txtFrame = tk.Frame(self.win)
        
        ## Effect Name
        ttk.Label(txtFrame, text="Effect Name:").grid(row=0, column=0, sticky=tk.W)
        ttk.Entry(txtFrame, textvariable=self.effectName).grid(row=0, column=1, sticky=tk.W)
        
        ## Effect Description
        ttk.Label(txtFrame, text="Effect Description:").grid(row=1, column=0, sticky=tk.W)
        ttk.Entry(txtFrame, textvariable=self.effectNameDesc).grid(row=1, column=1, sticky=tk.W)
        
        ## Effect TTS
        ttk.Label(txtFrame, text="Effect TTS Message:").grid(row=2, column=0, sticky=tk.W)
        ttk.Entry(txtFrame, textvariable=self.effectTTS).grid(row=2, column=1, sticky=tk.W)
        
        ## Effect Rarity
        ttk.Label(txtFrame, text="Effect Rarity:").grid(row=3, column=0, sticky=tk.W)
        ttk.Spinbox(txtFrame, from_=0, to=1, increment=0.1 , textvariable=self.effectrarity).grid(row=3, column=1, sticky=tk.W)
        
        ## Button to create effect
        ttk.Button(txtFrame, text="Command", command=self.effectPopUp).grid(row=4, column=0, sticky=tk.W) 
        txtFrame.pack(side=tk.TOP, fill=tk.X)
        

    def effectPopUp(self) -> None:
        popup_win = tk.Toplevel(self.win)
        
    def makeWindow(self) -> dict[str, str]:
        self._layout()
        self.win.mainloop()
        self.win_kill()
        
        return {
            "effectName": self.effectName.get(),
            "effectDesc": self.effectNameDesc.get(),
            "effectTTS": self.effectTTS.get(),
            "effectRarity": self.effectrarity.get(),
            "command": self.command
        }
    
    def win_kill(self) -> None:
        self.win.destroy()