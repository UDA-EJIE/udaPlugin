import customtkinter as ctk
from PIL import Image
class GIFLabel(ctk.CTkLabel):
    def __init__(self, master, image_path, **kwargs):
        self._gif_image = Image.open(image_path)
        # set the size of the label to the same as the GIF image
        kwargs.setdefault("width", self._gif_image.width)
        kwargs.setdefault("height", self._gif_image.height)
        # don't show the text initially
        kwargs.setdefault("text", "")
        # delay for the after loop
        self._duration = kwargs.pop("duration", 40) or self._gif_image.info["duration"]
        super().__init__(master, **kwargs)
        # load all the frames
        self._frames = []
        for i in range(self._gif_image.n_frames):
            self._gif_image.seek(i)
            self._frames.append(ctk.CTkImage(self._gif_image.copy(), size=(self["width"], self["height"])))
        # start animation
        self._animate()

    def _animate(self, idx=0):
        self.configure(image=self._frames[idx])
        self.after(self._duration, self._animate, (idx+1)%len(self._frames))