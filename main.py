# This is a sample Python script.

# Press Shift+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.
import cv2
from streamlit_webrtc import VideoTransformerBase, webrtc_streamer
import streamlit as st


def print_hi(name):
    # Use a breakpoint in the code line below to debug your script.
    print(f'Hi, {name}')  # Press Ctrl+F8 to toggle the breakpoint.


# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    st.title("Real-Time Barcode Scanner")
    faceCascade = cv2.CascadeClassifier(cv2.haarcascades + 'haarcascade_frontalface_default.xml')


    class VideoTransformer(VideoTransformerBase):
        def __init__(self):
            self.i = 0

        def transform(self, frame):
            img = frame.to_ndarray(format="bgr24")
            gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
            faces = faceCascade.detectMultiScale(gray, 1.3, 5)
            i = self.i + 1
            for (x, y, w, h) in faces:
                cv2.rectangle(img, (x, y), (x + w, y + h), (95, 207, 30), 3)
                cv2.rectangle(img, (x, y - 40), (x + w, y), (95, 207, 30), -1)
                cv2.putText(img, 'F-' + str(i), (x, y - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (0, 0, 0), 2)

            return img


    webrtc_streamer(key="example", video_transformer_factory=VideoTransformer)


# See PyCharm help at https://www.jetbrains.com/help/pycharm/
