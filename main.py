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
    cap = cv2.VideoCapture(0)  # 0 is the default camera

    if not cap.isOpened():
        print("Cannot access the camera!")
        exit()

    while True:
        ret, frame = cap.read()  # Read a frame from the webcam
        if not ret:
            break


# See PyCharm help at https://www.jetbrains.com/help/pycharm/
