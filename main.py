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

    if not cap.isOpened():
        st.write("Error: Camera could not be opened. Checking camera devices...")

        # Try different indices (1, 2, etc.) to open the camera
        for index in range(3):  # Check the first 3 camera indices
            cap = cv2.VideoCapture(index)
            if cap.isOpened():
                st.write(f"Success: Camera opened with index {index}")
                break
        else:
            st.write("Error: No camera found or accessible.")
    else:
        st.write("Success: Camera opened with index 0")

    # Release the camera if it was opened successfully
    cap.release()


# See PyCharm help at https://www.jetbrains.com/help/pycharm/
