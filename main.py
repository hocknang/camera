# This is a sample Python script.

# Press Shift+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.
import streamlit as st
from streamlit_webrtc import webrtc_streamer, VideoTransformerBase
import av
import cv2

def print_hi(name):
    # Use a breakpoint in the code line below to debug your script.
    print(f'Hi, {name}')  # Press Ctrl+F8 to toggle the breakpoint.


# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    st.title("Streamlit WebRTC Image Capture")


    class VideoTransformer(VideoTransformerBase):
        def transform(self, frame):
            # Convert frame to ndarray for processing
            img = frame.to_ndarray(format="bgr24")

            # Optional: Apply custom image processing (e.g., sharpen, grayscale)
            # Here, we'll just display the original frame
            return av.VideoFrame.from_ndarray(img, format="bgr24")


    # Start WebRTC streamer
    ctx = webrtc_streamer(key="example", video_transformer_factory=VideoTransformer)

    # Button to take a snapshot
    if ctx.video_transformer:
        if st.button("Capture Image"):
            frame = ctx.video_transformer.last_frame
            if frame is not None:
                st.image(frame, channels="BGR", caption="Captured Image")
            else:
                st.warning("No frame available to capture!")


# See PyCharm help at https://www.jetbrains.com/help/pycharm/
