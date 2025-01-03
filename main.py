# This is a sample Python script.

# Press Shift+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.
import cv2
import numpy as np
import streamlit as st


def print_hi(name):
    # Use a breakpoint in the code line below to debug your script.
    print(f'Hi, {name}')  # Press Ctrl+F8 to toggle the breakpoint.


# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    st.title("Real-Time Barcode Scanner")
    cap = cv2.VideoCapture(0)

    # Set camera resolution to a higher value
    cap.set(cv2.CAP_PROP_FRAME_WIDTH, 1280)  # Width = 1280 pixels
    cap.set(cv2.CAP_PROP_FRAME_HEIGHT, 720)  # Height = 720 pixels

    # Capture a frame from the camera
    ret, frame = cap.read()

    if not ret:
        st.write("Error: No camera detected or accessible.")
    else:
        # Process the frame for barcode detection
        frame, barcode_info = detect_barcode(frame)

        # Display the captured frame in Streamlit
        st.image(frame, caption="Captured Frame", channels="BGR", use_column_width=True)


# See PyCharm help at https://www.jetbrains.com/help/pycharm/
