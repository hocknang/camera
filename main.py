# This is a sample Python script.

# Press Shift+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.
import streamlit as st
import zxing

def print_hi(name):
    # Use a breakpoint in the code line below to debug your script.
    print(f'Hi, {name}')  # Press Ctrl+F8 to toggle the breakpoint.


# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    st.title("Real-Time Barcode Scanner")
    run = st.checkbox("Start Camera")

    if run:
        # Initialize OpenCV webcam capture
        cap = cv2.VideoCapture(0)  # 0 is the default camera

        if not cap.isOpened():
            st.error("Cannot access the camera!")

        # Optional: Set camera resolution (if needed)
        cap.set(3, 1280)  # Set width
        cap.set(4, 720)  # Set height

        # Loop for real-time video feed
        while cap.isOpened():
            ret, frame = cap.read()
            if not ret:
                break

# See PyCharm help at https://www.jetbrains.com/help/pycharm/
